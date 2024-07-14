USE leetcode_leaderboard;

-- Add a primary key constraint on username
ALTER TABLE LeetCodeUsers
ADD CONSTRAINT pk_username PRIMARY KEY (username);
