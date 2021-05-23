INSERT INTO USER(id, email, password, name, status, date) VALUES(1000, 'ehdgus5015@gmail.com', '{bcrypt}$2a$10$iizVueJgX5VuBHEFj9xMJOR3H42JEnFa2qiljz2bwbRNbRgNDiqbq', '김동현', 'OK', sysdate())
INSERT INTO USER_ROLES (USER_ID, ROLES) VALUES (1000, 'ROLE_USER') -- cksd12304@

