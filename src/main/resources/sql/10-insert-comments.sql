INSERT INTO post_comments (id, content, created_at, created_by, post_id)
VALUES (1, 'This is a comment 1', '2023-10-01 10:00:00', 'user-1',  1);

INSERT INTO reply_comments (id, content, created_at, created_by, parent_comment_id)
VALUES (6, 'This is a comment 6', '2023-10-01 15:00:00', 'user-1',  1);

INSERT INTO post_comments (id, content, created_at, created_by, post_id)
VALUES (2, 'This is a comment 2', '2023-10-01 11:00:00', 'user-1',  1);

INSERT INTO post_comments (id, content, created_at, created_by, post_id)
VALUES (3, 'This is a comment 3', '2023-10-01 12:00:00', 'user-2',  2);

INSERT INTO reply_comments (id, content, created_at, created_by, parent_comment_id)
VALUES (7, 'This is a comment 7', '2023-10-01 16:00:00', 'user-5',  3);

INSERT INTO post_comments (id, content, created_at, created_by, post_id)
VALUES (4, 'This is a comment 4', '2023-10-01 13:00:00', 'user-3',  3);

INSERT INTO reply_comments (id, content, created_at, created_by, parent_comment_id)
VALUES (8, 'This is a comment 8', '2023-10-01 17:00:00', 'user-6',  4);

INSERT INTO reply_comments (id, content, created_at, created_by, parent_comment_id)
VALUES (9, 'This is a comment 9', '2023-10-01 18:00:00', 'user-6',  2);

INSERT INTO post_comments (id, content, created_at, created_by, post_id)
VALUES (5, 'This is a comment 5', '2023-10-01 14:00:00', 'user-4',  4);
