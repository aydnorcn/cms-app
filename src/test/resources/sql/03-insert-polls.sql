insert into polls (active, created_at, created_by, description, max_vote_count, title, type, id) values
(true, '2023-01-01 00:00:00', 'user1', 'Description 1', 1, 'Title 1', 'SINGLE_CHOICE', 1),
(true, '2023-01-02 00:00:00', 'user2', 'Description 2', 2, 'Title 2', 'MULTIPLE_CHOICE', 2),
(false, '2023-01-03 00:00:00', 'user3', 'Description 3', 1, 'Title 3', 'SINGLE_CHOICE', 3),
(true, '2023-01-04 00:00:00', 'user4', 'Description 4', 3, 'Title 4', 'MULTIPLE_CHOICE', 4),
(false, '2023-01-05 00:00:00', 'user5', 'Description 5', 1, 'Title 5', 'SINGLE_CHOICE', 5),
(true, '2023-01-06 00:00:00', 'user6', 'Description 6', 2, 'Title 6', 'MULTIPLE_CHOICE', 6),
(false, '2023-01-07 00:00:00', 'user7', 'Description 7', 1, 'Title 7', 'SINGLE_CHOICE', 7),
(true, '2023-01-08 00:00:00', 'user8', 'Description 8', 3, 'Title 8', 'MULTIPLE_CHOICE', 8),
(false, '2023-01-09 00:00:00', 'user9', 'Description 9', 1, 'Title 9', 'SINGLE_CHOICE', 9),
(true, '2023-01-10 00:00:00', 'user10', 'Description 10', 2, 'Title 10', 'MULTIPLE_CHOICE', 10);

insert into options (created_at, created_by, poll_id, text, id) values
('2023-01-01 00:00:00', 'user1', 1, 'Option 1-1', 1),
('2023-01-01 00:00:00', 'user1', 1, 'Option 1-2', 2),
('2023-01-02 00:00:00', 'user2', 2, 'Option 2-1', 3),
('2023-01-02 00:00:00', 'user2', 2, 'Option 2-2', 4),
('2023-01-03 00:00:00', 'user3', 3, 'Option 3-1', 5),
('2023-01-03 00:00:00', 'user3', 3, 'Option 3-2', 6),
('2023-01-04 00:00:00', 'user4', 4, 'Option 4-1', 7),
('2023-01-04 00:00:00', 'user4', 4, 'Option 4-2', 8),
('2023-01-05 00:00:00', 'user5', 5, 'Option 5-1', 9),
('2023-01-05 00:00:00', 'user5', 5, 'Option 5-2', 10);