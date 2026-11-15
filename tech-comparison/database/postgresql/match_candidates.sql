-- Candidates for user :me with the number of shared interests, excluding users with a pending
-- or accepted request. Joins and NOT EXISTS keep the data consistent and the query declarative.
SELECT p.user_id,
       p.display_name,
       COUNT(pi_other.interests_id) AS shared_interests
FROM profiles p
JOIN users u                   ON u.id = p.user_id AND u.status = 'ACTIVE'
JOIN profile_interests pi_other ON pi_other.profiles_id = p.id
JOIN profile_interests pi_me    ON pi_me.interests_id = pi_other.interests_id
JOIN profiles me                ON me.id = pi_me.profiles_id AND me.user_id = :me
WHERE p.completed = TRUE
  AND p.user_id <> :me
  AND NOT EXISTS (SELECT 1 FROM match_requests r
                  WHERE r.status IN ('PENDING', 'ACCEPTED')
                    AND ((r.sender_id = :me AND r.receiver_id = p.user_id)
                      OR (r.receiver_id = :me AND r.sender_id = p.user_id)))
GROUP BY p.user_id, p.display_name
ORDER BY shared_interests DESC
LIMIT 50;
