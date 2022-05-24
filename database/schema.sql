\c testreport;
create table TestCampaign(
    testCampaignID SERIAL PRIMARY KEY,
    campaignName VARCHAR(400) NOT NULL,
    envName VARCHAR(400) NOT NULL,
    begTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP DEFAULT null,
    status VARCHAR(30) NOT NULL,
    username VARCHAR(400) NOT NULL
);
create table TestCase(
    testCaseID SERIAL PRIMARY KEY,
    testCaseName VARCHAR(400) NOT NULL,
    begTime TIMESTAMP NOT NULL,
    endTime TIMESTAMP DEFAULT null,
    status VARCHAR(30) NOT NULL,
    testCampaignID INT,
    CONSTRAINT FK_testCampaignID FOREIGN KEY (testCampaignID)
    REFERENCES TestCampaign(testCampaignID)
);
create table FailInfo(
    failInfoID SERIAL PRIMARY KEY,
    reportTime TIMESTAMP NOT NULL,
    message VARCHAR(4000),
    testCaseID INT NOT NULL,
    CONSTRAINT FK_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(testCaseID)
);
create table LogInfo(
    logInfoID SERIAL PRIMARY KEY,
    reportTime TIMESTAMP,
    message VARCHAR(4000),
    levelStatus VARCHAR(30) NOT NULL,
    testCaseID INT NOT NULL,
    CONSTRAINT FK_log_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(testCaseID)
);
create table ScreenshotInfo(
    screenshotID SERIAL PRIMARY KEY,
    reportTime TIMESTAMP,
    screenshot BYTEA,
    testCaseID INT NOT NULL,
    CONSTRAINT FK_screenshot_testCaseID FOREIGN KEY (testCaseID)
    REFERENCES TestCase(testCaseID)
);
create table AuthorizationInfo(
    username VARCHAR(400) PRIMARY KEY,
    password BYTEA
);

ALTER TABLE TestCampaign
    ADD CONSTRAINT FK_username FOREIGN KEY (username)
    REFERENCES AuthorizationInfo(username);

INSERT INTO AuthorizationInfo VALUES('user', 'password');
