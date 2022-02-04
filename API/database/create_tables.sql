use testreport;
drop table TestCampaign;
create table TestCampaign(
id INT(6),
repositoryName VARCHAR(30),
envName VARCHAR(30),
begTime TIMESTAMP
);
