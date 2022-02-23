use testreport;

ALTER TABLE TestCase DROP FOREIGN KEY FK_testCampaignID;
ALTER TABLE FailInfo DROP FOREIGN KEY FK_testCaseID;
ALTER TABLE LogInfo DROP FOREIGN KEY FK_log_testCaseID;

drop table FailInfo;
drop table LogInfo;
drop table TestCase;
drop table TestCampaign;


create table TestCampaign(
    id INT(6) PRIMARY KEY,
    campaignName VARCHAR(60) NOT NULL,
    envName VARCHAR(60) NOT NULL,
    begTime DATETIME NOT NULL,
    endTime DATETIME DEFAULT null,
    status VARCHAR(30) NOT NULL
);
create table TestCase(
    id INT(6) PRIMARY KEY,
    testCaseName VARCHAR(60) NOT NULL,
    begTime DATETIME NOT NULL,
    endTime DATETIME DEFAULT null,
    status VARCHAR(30) NOT NULL,
    testCampaignID INT(6),
    CONSTRAINT FK_testCampaignID FOREIGN KEY (testCampaignID)
    REFERENCES TestCampaign(id)
);
create table FailInfo(
    id INT(6) PRIMARY KEY AUTO_INCREMENT,
    reportTime DATETIME NOT NULL,
    message VARCHAR(120),
    testCaseID INT(6) NOT NULL,
    CONSTRAINT FK_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(id)
);
create table LogInfo(
    id INT(6) PRIMARY KEY AUTO_INCREMENT,
    reportTime DATETIME NOT NULL,
    message VARCHAR(120),
    level_status VARCHAR(30) NOT NULL,
    testCaseID INT(6) NOT NULL,
    CONSTRAINT FK_log_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(id)
);