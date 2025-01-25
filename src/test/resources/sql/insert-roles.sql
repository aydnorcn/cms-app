insert into users (id, name, created_at) values ('user-1','user1',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user1@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-1','cred-1');

insert into users (id, name, created_at) values ('user-2','user2',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user2@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-2','cred-2');

insert into users (id, name, created_at) values ('user-3','user3',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user3@mail.com', '$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-3','cred-3');

insert into users (id, name, created_at) values ('user-4','user4',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user4@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-4','cred-4');

insert into users (id, name, created_at) values ('user-5','user5',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user5@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-5','cred-5');

insert into users (id, name, created_at) values ('user-6','user6',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user6@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-6','cred-6');

insert into users (id, name, created_at) values ('user-7','user7',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user7@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-7','cred-7');

insert into users (id, name, created_at) values ('user-8','user8',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user8@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-8','cred-8');

insert into users (id, name, created_at) values ('user-9','user9',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user9@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-9','cred-9');

insert into users (id, name, created_at) values ('user-10','user10',CURRENT_TIMESTAMP);
insert into user_credentials (email,password,user_id,id) values ('user10@mail.com','$2a$10$66ZmQrC9dGk51fkj3H6eAeKqQ0JlD1q3Bp8pSA.iAOPwpzwBxKytS','user-10','cred-10');

insert into roles (name,id) values ('ROLE_USER','1');
insert into roles (name,id) values ('ROLE_ADMIN','2');
insert into roles (name,id) values ('ROLE_MODERATOR','3');
insert into roles (name,id) values ('ROLE_ORGANIZATOR','4');

insert into users_roles (user_id,role_id) values ('user-1','1');
insert into users_roles (user_id,role_id) values ('user-2','1');
insert into users_roles (user_id,role_id) values ('user-2','2');