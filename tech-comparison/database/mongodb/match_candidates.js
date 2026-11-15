// MongoDB version of the same query (aggregation pipeline, mongosh syntax).
// The profile is one document (no joins needed to read it), but interest names are copied
// into every profile, and the match requests live in another collection.
const me = db.profiles.findOne({ userId: 7 });
const myInterestIds = me.interests.map((i) => i.id);
const excluded = db.match_requests
  .find({ status: { $in: ['PENDING', 'ACCEPTED'] }, $or: [{ senderId: 7 }, { receiverId: 7 }] })
  .toArray()
  .map((r) => (r.senderId === 7 ? r.receiverId : r.senderId));   // second query + work in application code

db.profiles.aggregate([
  { $match: { completed: true, status: 'ACTIVE', userId: { $ne: 7, $nin: excluded } } },
  { $addFields: {
      sharedInterests: { $size: { $setIntersection: [ { $map: { input: '$interests', as: 'i', in: '$$i.id' } },
                                                      myInterestIds ] } } } },
  { $sort: { sharedInterests: -1 } },
  { $limit: 50 },
  { $project: { userId: 1, displayName: 1, sharedInterests: 1 } },
]);
// If an administrator renames an interest, every profile document containing it must be updated
// (no foreign keys), and "delete a user with all data" must be implemented across collections by hand.
