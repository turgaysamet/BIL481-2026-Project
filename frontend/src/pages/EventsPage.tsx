import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { eventsApi } from '../api/eventsApi';
import type { EventCategory, EventResponse } from '../api/types';
import ErrorMessage from '../components/ErrorMessage';

const CATEGORIES: EventCategory[] = ['SPORT', 'LANGUAGE', 'CULTURE', 'STUDY', 'OTHER'];

/** UC-4: browse, create, join and leave events (FR-09, FR-10, FR-11). */
export default function EventsPage() {
  const { t, i18n } = useTranslation();
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [q, setQ] = useState('');
  const [category, setCategory] = useState<EventCategory | ''>('');
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const load = useCallback(() => {
    eventsApi.search({ q, category: category || undefined }).then(setEvents).catch(setError);
  }, [q, category]);

  useEffect(() => { load(); }, [load]);

  async function toggleJoin(event: EventResponse) {
    try {
      const updated = event.joinedByMe ? await eventsApi.leave(event.id) : await eventsApi.join(event.id);
      setEvents(events.map((e) => (e.id === updated.id ? updated : e)));
    } catch (err) {
      setError(err);
    }
  }

  return (
    <section>
      <div className="row space">
        <h1>{t('events.title')}</h1>
        <button onClick={() => setShowForm(!showForm)}>{t('events.create')}</button>
      </div>
      {showForm && <CreateEventForm onCreated={() => { setShowForm(false); load(); }} />}
      <div className="row">
        <input placeholder={t('events.search')} value={q} onChange={(e) => setQ(e.target.value)} />
        <select aria-label={t('events.category')} value={category} onChange={(e) => setCategory(e.target.value as EventCategory | '')}>
          <option value="">{t('events.category')}</option>
          {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
        </select>
      </div>
      <ErrorMessage error={error} />
      {events.length === 0 && <p>{t('events.empty')}</p>}
      <div className="grid">
        {events.map((event) => (
          <article key={event.id} className="card">
            <span className="tag">{event.category}</span>
            <h3>{event.title}</h3>
            <p className="muted">{new Date(event.startsAt).toLocaleString(i18n.language)} · {event.location}</p>
            <p>{event.description}</p>
            <p className="muted">{t('events.places', { free: event.freePlaces, capacity: event.capacity })}</p>
            <button disabled={!event.joinedByMe && event.freePlaces === 0} onClick={() => toggleJoin(event)}>
              {event.joinedByMe ? t('events.leave') : event.freePlaces === 0 ? t('events.full') : t('events.join')}
            </button>
          </article>
        ))}
      </div>
    </section>
  );
}

function CreateEventForm({ onCreated }: { onCreated: () => void }) {
  const { t } = useTranslation();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState<EventCategory>('SPORT');
  const [startsAt, setStartsAt] = useState('');
  const [location, setLocation] = useState('');
  const [capacity, setCapacity] = useState(10);
  const [languages, setLanguages] = useState('en, tr');
  const [error, setError] = useState<unknown>(null);

  async function submit(e: FormEvent) {
    e.preventDefault();
    try {
      await eventsApi.create({
        title, description, category, location, capacity,
        startsAt: new Date(startsAt).toISOString(),
        languages: languages.split(',').map((l) => l.trim()).filter(Boolean),
      });
      onCreated();
    } catch (err) {
      setError(err);
    }
  }

  return (
    <form className="card form" onSubmit={submit}>
      <label>{t('events.eventTitle')}<input required maxLength={100} value={title} onChange={(e) => setTitle(e.target.value)} /></label>
      <label>{t('events.description')}<textarea maxLength={1000} value={description} onChange={(e) => setDescription(e.target.value)} /></label>
      <label>{t('events.category')}
        <select value={category} onChange={(e) => setCategory(e.target.value as EventCategory)}>
          {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
        </select>
      </label>
      <label>{t('events.date')}<input type="datetime-local" required value={startsAt} onChange={(e) => setStartsAt(e.target.value)} /></label>
      <label>{t('events.location')}<input required value={location} onChange={(e) => setLocation(e.target.value)} /></label>
      <label>{t('events.capacity')}<input type="number" min={2} max={100} value={capacity} onChange={(e) => setCapacity(Number(e.target.value))} /></label>
      <label>{t('events.languages')}<input value={languages} onChange={(e) => setLanguages(e.target.value)} /></label>
      <ErrorMessage error={error} />
      <button type="submit">{t('events.publish')}</button>
    </form>
  );
}
