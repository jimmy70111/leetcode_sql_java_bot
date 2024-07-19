USE leetcode_leaderboard;

CREATE TABLE LeetCodeProblems (
    title VARCHAR(255),
    difficulty ENUM('Easy', 'Medium', 'Hard'),
    url VARCHAR(255)
);
