INSERT INTO USER(id, email, password, name, state, date) VALUES(1000, 'ehdgus5015@gmail.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate())
INSERT INTO USER(id, email, password, name, state, date) VALUES(1001, 'ehdgus5015@naver.com', '{bcrypt}$2a$10$Y8kKtFi/LfVGB2tDBtPHI.iT02.vdmhpxDFI3xpcBcE8OmE3FCgoG', '김동현', 'OK', sysdate())
INSERT INTO USER_ROLES (USER_ID, ROLES) VALUES (1000, 'ROLE_USER') -- cksd12304@

INSERT INTO USER_ROLES (USER_ID, ROLES) VALUES (1001, 'ROLE_USER') -- cksd12304@

insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10000','2021-05-24 15:47:16.903','demoData','demoData','CREATED','1000')
--insert into TEAM (ID,DATE,DESCRIPTION,NAME,STATE,USER_ID) VALUES ('10001','2021-05-24 15:47:16.903','demoData1','demoData1','CREATED','1001')
insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20000','2021-05-24 15:47:16.918','CREATED','10000','1000')
--insert into TEAM_MEMBER(ID,DATE,STATE,TEAM_ID,USER_ID) VALUES('20001','2021-05-24 15:47:16.918','CREATED','10000','1001')
insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30000',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_CREATED',NULL,'20000')
--insert into TEAM_ACTIVITY (ID,ATTACHMENT_ID,BOARD_ID,CARD_ID,DATE,DETAIL,REPLY_ID,TEAM_MEMBER_ID) VALUES ('30001',NULL,NULL,NULL,'2021-05-24 15:47:16.922','TEAM_JOINED',NULL,'20001')

insert into BOARD(ID,CONTENT,DATE,STATE,TITLE,TEAM_ID,TEAM_MEMBER_ID) values ('10000','demoData','2021-05-29 18:37:26.402','CREATED','demoData','10000','20000')
