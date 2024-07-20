USE leetcode_leaderboard;

ALTER TABLE LeetCodeUsers
CHANGE COLUMN discordUserId discordUserId VARCHAR(50);
