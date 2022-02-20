use testreport;
drop table TestCampaign;
drop table TestCase;
create table TestCampaign(
    id INT(6) PRIMARY KEY,
    campaignName VARCHAR(30) NOT NULL,
    envName VARCHAR(30) NOT NULL,
    begTime DATETIME NOT NULL,
    endTime DATETIME DEFAULT null,
    status VARCHAR(30) NOT NULL
);
create table TestCase(
    id INT(6) PRIMARY KEY,
    testCaseName VARCHAR(30) NOT NULL,
    begTime DATETIME NOT NULL,
    endTime DATETIME DEFAULT null,
    status VARCHAR(30) NOT NULL,
    testCampaignID INT(6),
    CONSTRAINT FK_testCampaignID FOREIGN KEY (testCampaignID)
    REFERENCES TestCampaign(id)
);
