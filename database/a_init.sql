CREATE DATABASE testreport;
CREATE USER fastapi with encrypted password 'fastapi_password';
GRANT ALL PRIVILEGES ON DATABASE testreport to fastapi;
