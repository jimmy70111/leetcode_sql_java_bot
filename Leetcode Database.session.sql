USE leetcode_leaderboard;

CREATE TABLE LeetCodeProblems (
    problem_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    difficulty ENUM('Easy', 'Medium', 'Hard'),
    url VARCHAR(255)
);

