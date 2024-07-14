


USE leetcode_leaderboard;

-- Delete the acceptanceRate column from the LeetCodeUsers table
ALTER TABLE LeetCodeUsers
DROP COLUMN email;
