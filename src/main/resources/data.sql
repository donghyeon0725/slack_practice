INSERT INTO USER(id, email, password, name, status, date) VALUES(1000, 'ehdgus5015@naver.com', '{여기에는 암호화된 비밀번호가 들어가야 합니다. => 그냥 join 하는 것이 편합니다.}', '김동현', 'OK', sysdate())
INSERT INTO USER_ROLES (USER_ID, ROLES) VALUES (1000, 'ROLE_USER')

