/* mysql://localhost:3306/saikorogame */


DROP DATABASE IF EXISTS saikorogame;
CREATE DATABASE saikorogame;
USE saikorogame;
DROP TABLE IF EXISTS userprofile;
CREATE TABLE userprofile (
	id INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(16) NOT NULL,
    user_password VARCHAR(16) NOT NULL,
    date_create DATE,
    date_lastlogin DATE,
    high_score int,
    PRIMARY KEY (id)
);

CREATE TABLE usersession (
	session_id INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(16) NOT NULL,
    game_status BOOLEAN NOT NULL,
    time_start DATETIME NOT NULL,
    time_end DATETIME,
    score_player INT,
    score_cpu INT,
    winner INT,
    PRIMARY KEY (session_id)
);

INSERT INTO userprofile VALUES (1,'test1','test1','2019-01-01','2019-01-02',10);
INSERT INTO userprofile (id, user_name, user_password) VALUES (2,'test2','test2');

