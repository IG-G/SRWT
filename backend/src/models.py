import datetime

from sqlalchemy import Column, ForeignKey, Integer, String, DateTime, LargeBinary
from sqlalchemy.orm import relationship
from .database import Base


class TestCampaign(Base):
    __tablename__ = "TestCampaign"

    testCampaignID = Column(Integer, primary_key=True, index=True)
    campaignName = Column(String(400))
    envName = Column(String(400))
    status = Column(String(30))
    begTime = Column(DateTime, default=datetime.datetime.now())
    endTime = Column(DateTime, default=None)
    username = Column(String(400), ForeignKey("AuthorizationInfo.username"))

    users = relationship("AuthorizationInfo", back_populates="testCampaign")
    testCases = relationship("TestCase", back_populates="testCampaign")


class TestCase(Base):
    __tablename__ = "TestCase"

    testCaseID = Column(Integer, primary_key=True, index=True)
    testCaseName = Column(String(400))
    status = Column(String(30))
    begTime = Column(DateTime, default=datetime.datetime.now())
    endTime = Column(DateTime, default=None)
    testCampaignID = Column(Integer, ForeignKey("TestCampaign.testCampaignID"))

    testCampaign = relationship("TestCampaign", back_populates="testCases")
    failsInfo = relationship("FailInfo", back_populates="testCase")
    logsInfo = relationship("LogInfo", back_populates="testCase")
    screenshotInfo = relationship("ScreenshotInfo", back_populates="testCase")


class FailInfo(Base):
    __tablename__ = "FailInfo"

    FailInfoID = Column(Integer, primary_key=True, autoincrement=True, index=False)
    reportTime = Column(DateTime, default=datetime.datetime.now())
    message = Column(String(4000))
    testCaseID = Column(Integer, ForeignKey("TestCase.testCaseID"))

    testCase = relationship("TestCase", back_populates="failsInfo")


class LogInfo(Base):
    __tablename__ = "LogInfo"

    logInfoID = Column(Integer, primary_key=True, index=False)
    reportTime = Column(DateTime)
    message = Column(String(4000))
    levelStatus = Column(String(30))
    testCaseID = Column(Integer, ForeignKey("TestCase.testCaseID"))

    testCase = relationship("TestCase", back_populates="logsInfo")


class ScreenshotInfo(Base):
    __tablename__ = "ScreenshotInfo"

    screenshotID = Column(Integer, primary_key=True)
    reportTime = Column(DateTime)
    screenshot = Column(LargeBinary)
    testCaseID = Column(Integer, ForeignKey("TestCase.testCaseID"))

    testCase = relationship("TestCase", back_populates="screenshotInfo")


class AuthorizationInfo(Base):
    __tablename__ = "AuthorizationInfo"
    username = Column(String(400), primary_key=True)
    password = Column(LargeBinary)

    testCampaign = relationship("TestCampaign", back_populates="users")
