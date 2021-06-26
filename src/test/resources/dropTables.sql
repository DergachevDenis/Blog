DELETE FROM user_table;
DELETE FROM article_table;
DELETE FROM article_tag;
DELETE FROM comment_table;
DELETE FROM role_table;
DELETE FROM tags;

INSERT INTO role_table (name) VALUES ('ROLE_USER');
