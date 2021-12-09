call next value for hibernate_sequence;
insert into user(id, created_at, updated_at, answer_count, eval_count, eval_total, password, role, username, nickname)
values (1, current_timestamp, current_timestamp, 0, 0, 0, 1, 'ROLE_USER', 'user1', 'nickname1');
call next value for hibernate_sequence;
insert into user(id, created_at, updated_at, answer_count, eval_count, eval_total, password, role, username, nickname)
values (2, current_timestamp, current_timestamp, 0, 0, 0, 1, 'ROLE_USER', 'user2', 'nickname2');
call next value for hibernate_sequence;
insert into user(id, created_at, updated_at, answer_count, eval_count, eval_total, password, role, username, nickname)
values (3, current_timestamp, current_timestamp, 2, 23, 69, 1, 'ROLE_REVIEWER', 'JavaGod', 'nickname3');
call next value for hibernate_sequence;
insert into user(id, created_at, updated_at, answer_count, eval_count, eval_total, password, role, username, nickname)
values (4, current_timestamp, current_timestamp, 1, 11, 11, 1, 'ROLE_REVIEWER', 'PythonGod', 'nickname4');
call next value for hibernate_sequence;

call next value for hibernate_sequence;
insert into language(id, created_at, updated_at, name, user_id)
values (1, current_timestamp, current_timestamp, 'JAVA', 3);
call next value for hibernate_sequence;
insert into language(id, created_at, updated_at, name, user_id)
values(2, current_timestamp, current_timestamp, 'PYTHON', 4);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (1, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request_comment(id, created_at, updated_at, content,  review_request_id, user_id)
values (1, current_timestamp, current_timestamp, '테스트댓글내용', 1, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (2, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목1 - java', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (3, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목2 - java', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (4, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목1 - python', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (5, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목2 - python', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (6, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목3 - spring', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (7, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목4 - spring', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (8, current_timestamp, current_timestamp, '테스트내용 - 입력', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (9, current_timestamp, current_timestamp, '테스트내용 - 출력', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (10, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (11, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (12, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (13, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (14, current_timestamp, current_timestamp, '테스트내용', 'JAVA', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (15, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (16, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (17, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (18, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (19, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (20, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (21, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (22, current_timestamp, current_timestamp, '테스트내용', 'PYTHON', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (23, current_timestamp, current_timestamp, '테스트내용', 'C', 'UNSOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (24, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (25, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (26, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (27, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (28, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (29, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (30, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);

call next value for hibernate_sequence;
insert into review_request(id, created_at, updated_at, content, language_name, status, title, answer_user_id,request_user_id)
values (31, current_timestamp, current_timestamp, '테스트내용', 'C', 'SOLVE', '테스트제목', 2, 1);