// Express (Node.js) version of the same feature, for comparison.
// Uses the "pg" PostgreSQL driver with hand-written SQL.
const express = require('express');
const { Pool } = require('pg');

const pool = new Pool({ connectionString: process.env.DB_URL });
const router = express.Router();

router.post('/api/events/:eventId/participants', async (req, res) => {
  const eventId = Number(req.params.eventId);      // manual conversion and validation
  const userId = req.user.id;                      // set by an authentication middleware
  if (!Number.isInteger(eventId)) return res.status(400).json({ code: 'VALIDATION_ERROR' });

  const client = await pool.connect();
  try {
    await client.query('BEGIN');                   // transaction handled by hand
    const { rows } = await client.query(
      'SELECT capacity, participant_count FROM events WHERE id = $1 FOR UPDATE', [eventId]); // row lock
    if (rows.length === 0) { await client.query('ROLLBACK'); return res.status(404).json({ code: 'NOT_FOUND' }); }

    const joined = await client.query(
      'SELECT 1 FROM event_participations WHERE event_id = $1 AND user_id = $2', [eventId, userId]);
    if (joined.rowCount > 0) { await client.query('ROLLBACK'); return res.status(409).json({ code: 'ALREADY_JOINED' }); }
    if (rows[0].participant_count >= rows[0].capacity) {
      await client.query('ROLLBACK');
      return res.status(409).json({ code: 'EVENT_FULL', message: 'This event is full' });
    }

    await client.query('UPDATE events SET participant_count = participant_count + 1 WHERE id = $1', [eventId]);
    await client.query('INSERT INTO event_participations (event_id, user_id, joined_at) VALUES ($1, $2, now())',
      [eventId, userId]);
    await client.query('COMMIT');
    res.json({ id: eventId, joinedByMe: true });   // response shape is not type-checked
  } catch (err) {
    await client.query('ROLLBACK');
    res.status(500).json({ code: 'INTERNAL_ERROR' });
  } finally {
    client.release();
  }
});

module.exports = router;
// Shorter to start with, but transactions, validation, error mapping and types must be written by hand
// in every route (or added with extra libraries such as Prisma, zod and TypeScript).
