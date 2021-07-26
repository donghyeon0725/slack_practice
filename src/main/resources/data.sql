insert into ROLE  (id, role_name) values (10001, 'ROLE_ADMIN');
insert into ROLE  (id, role_name) values (10002, 'ROLE_USER');

INSERT INTO USER(id, email, password, name, state, date) VALUES(1000, 'ehdgus5015@gmail.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate());
INSERT INTO USER(id, email, password, name, state, date) VALUES(1001, 'ehdgus5015@naver.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate());
INSERT INTO USER(id, email, password, name, state, date) VALUES(1002, 'qudghk@naver.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '수성펜', 'OK', sysdate());

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (1, 1000, 10002); -- cksd12304@

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (2, 1001, 10002); -- cksd12304@

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (3, 1002, 10002); -- cksd12304@

insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10000','2021-05-24 15:47:16.903','밴드 소모임','Crush!','CREATED','1001');
insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10001','2021-05-24 15:47:16.903','Slack Team','Slack Team','CREATED','1000');
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20000','2021-05-24 15:47:16.918','CREATED','10000','1001');
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20001','2021-05-24 15:47:16.918','CREATED','10001','1000');
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20002','2021-05-24 15:47:16.918','CREATED','10001','1001');

-- insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20002','2021-05-24 15:47:16.918','CREATED','10001','1001');
-- insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20003','2021-05-24 15:47:16.918','CREATED','10001','1002');

-- insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30000',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_CREATED',NULL,'20000');
-- insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30001',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20001');
-- insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30002',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20002');
-- insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30003',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20003');

-- insert into BOARD(ID,CONTENT,DATE,STATE,TITLE,TEAM_ID,TEAM_MEMBER_ID) values ('10000','Board Come','2021-05-29 18:37:26.402','CREATED','Board Come','10000','20000');
insert into BOARD(ID,CONTENT,DATE,STATE,TITLE,TEAM_ID,TEAM_MEMBER_ID) values ('10001','IT 팀 Board','2021-05-29 18:37:26.402','CREATED','Board For IT Team','10001','20001');


insert into account (id, password, username) values (100, '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', 'admin');

insert into account_role (id, account_id, role_id) values (1, 100, 10001);


insert into ROLE_HIERARCHY  (id, child) values (1, 'ROLE_ADMIN');

insert into ROLE_HIERARCHY  (id, child, parent) values (2, 'ROLE_USER', 'ROLE_ADMIN');




insert into resources (id, order_num, resource_name, resource_type) values (1001, 1, '/board', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1002, 2, '/board/**', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1003, 3, '/teams', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1004, 4, '/teams/**', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1005, 5, '/card', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1006, 6, '/card/**', 'URL');

insert into resources_role (id, resources_id, role_id) values (1001, 1001, 10002);
insert into resources_role (id, resources_id, role_id) values (1002, 1002, 10002);
insert into resources_role (id, resources_id, role_id) values (1003, 1003, 10002);
insert into resources_role (id, resources_id, role_id) values (1004, 1004, 10002);
insert into resources_role (id, resources_id, role_id) values (1005, 1005, 10002);
insert into resources_role (id, resources_id, role_id) values (1006, 1006, 10002);


/*insert into ROLE  (id, role_name) values (1, 'ROLE_ADMIN')
insert into ROLE  (id, role_name) values (2, 'ROLE_USER')

INSERT INTO USER(id, email, password, name, state, date) VALUES(1000, 'ehdgus5015@gmail.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate())
INSERT INTO USER(id, email, password, name, state, date) VALUES(1001, 'ehdgus5015@naver.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate())
INSERT INTO USER(id, email, password, name, state, date) VALUES(1002, 'qudghk@naver.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '수성펜', 'OK', sysdate())

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (1, 1000, 2) -- cksd12304@

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (2, 1001, 2) -- cksd12304@

INSERT INTO USER_ROLE (ID, USER_ID, ROLE_ID) VALUES (3, 1002, 2) -- cksd12304@

insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10000','2021-05-24 15:47:16.903','Crush','Band For','CREATED','1000')
insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10001','2021-05-24 15:47:16.903','Control','Interface What I Am','CREATED','1001')
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20000','2021-05-24 15:47:16.918','CREATED','10000','1000')
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20001','2021-05-24 15:47:16.918','CREATED','10000','1001')

insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20002','2021-05-24 15:47:16.918','CREATED','10001','1001')
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20003','2021-05-24 15:47:16.918','CREATED','10001','1002')

insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30000',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_CREATED',NULL,'20000')
insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30001',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20001')
insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30002',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20002')
insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30003',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20003')

insert into BOARD(ID,CONTENT,DATE,STATE,TITLE,TEAM_ID,TEAM_MEMBER_ID) values ('10000','Board Come','2021-05-29 18:37:26.402','CREATED','Board Come','10000','20000')
insert into BOARD(ID,CONTENT,DATE,STATE,TITLE,TEAM_ID,TEAM_MEMBER_ID) values ('10001','Here Come','2021-05-29 18:37:26.402','CREATED','Here Come','10001','20003')


insert into account (id, password, username) values (100, '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', 'admin')

insert into account_role (id, account_id, role_id) values (1, 100, 1)



insert into resources (id, order_num, resource_name, resource_type) values (1001, 1, '/board/**', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1002, 2, '/team/**', 'URL');
insert into resources (id, order_num, resource_name, resource_type) values (1003, 3, '/card/**', 'URL');

insert into resources_role (id, resources_id, role_id) values (1001, 1001, 2);
insert into resources_role (id, resources_id, role_id) values (1002, 1002, 2);
insert into resources_role (id, resources_id, role_id) values (1003, 1003, 2);

*/
