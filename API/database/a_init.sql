CREATE DATABASE testreport;
CREATE USER 'fastapi'@'%' IDENTIFIED BY 'fastapi_password';
GRANT ALL PRIVILEGES ON * . * TO 'fastapi'@'%';
FLUSH PRIVILEGES;

