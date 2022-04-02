import datetime

from sqlalchemy import Column, ForeignKey, Integer, String, DateTime, LargeBinary
from sqlalchemy.orm import relationship
from .database import Base


class TestCampaign(Base):
    __tablename__ = "TestCampaign"

    id = Column(Integer, primary_key=True, index=True)
    campaignName = Column(String(30))
    envName = Column(String(30))
    status = Column(String(30))
    begTime = Column(DateTime, default=datetime.datetime.now())
    endTime = Column(DateTime, default=None)

    testCases = relationship("TestCase", back_populates="testCampaign")


class TestCase(Base):
    __tablename__ = "TestCase"

    id = Column(Integer, primary_key=True, index=True)
    testCaseName = Column(String(30))
    status = Column(String(30))
    begTime = Column(DateTime, default=datetime.datetime.now())
    endTime = Column(DateTime, default=None)
    testCampaignID = Column(Integer, ForeignKey("TestCampaign.id"))

    testCampaign = relationship("TestCampaign", back_populates="testCases")
    failsInfo = relationship("FailInfo", back_populates="testCase")
    logsInfo = relationship("LogInfo", back_populates="testCase")
    screenshotInfo = relationship("ScreenshotInfo", back_populates="testCase")


class FailInfo(Base):
    __tablename__ = "FailInfo"

    id = Column(Integer, primary_key=True, autoincrement=True, index=False)
    reportTime = Column(DateTime, default=datetime.datetime.now())
    message = Column(String(120))
    testCaseID = Column(Integer, ForeignKey("TestCase.id"))

    testCase = relationship("TestCase", back_populates="failsInfo")


class LogInfo(Base):
    __tablename__ = "LogInfo"

    id = Column(Integer, primary_key=True, index=False)
    reportTime = Column(DateTime)
    message = Column(String(120))
    level_status = Column(String(30))
    testCaseID = Column(Integer, ForeignKey("TestCase.id"))

    testCase = relationship("TestCase", back_populates="logsInfo")


class ScreenshotInfo(Base):
    __tablename__ = "ScreenshotInfo"

    id = Column(Integer, primary_key=True)
    reportTime = Column(DateTime)
    path = Column(String(30))
    screenshot = Column(LargeBinary)
    testCaseID = Column(Integer, ForeignKey("TestCase.id"))

    testCase = relationship("TestCase", back_populates="screenshotInfo")
