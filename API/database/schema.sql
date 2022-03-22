create table TestCampaign(
    id SERIAL PRIMARY KEY,
    campaignName VARCHAR(60) NOT NULL,
    envName VARCHAR(60) NOT NULL,
    begTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP DEFAULT null,
    status VARCHAR(30) NOT NULL
);
create table TestCase(
    id SERIAL PRIMARY KEY,
    testCaseName VARCHAR(60) NOT NULL,
    begTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP DEFAULT null,
    status VARCHAR(30) NOT NULL,
    testCampaignID INT,
    CONSTRAINT FK_testCampaignID FOREIGN KEY (testCampaignID)
    REFERENCES TestCampaign(id)
);
create table FailInfo(
    id SERIAL PRIMARY KEY,
    reportTime TIMESTAMP NOT NULL,
    message VARCHAR(120),
    testCaseID INT NOT NULL,
    CONSTRAINT FK_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(id)
);
create table LogInfo(
    id SERIAL PRIMARY KEY,
    reportTime TIMESTAMP,
    message VARCHAR(120),
    level_status VARCHAR(30) NOT NULL,
    testCaseID INT NOT NULL,
    CONSTRAINT FK_log_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(id)
);
