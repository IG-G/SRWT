import datetime

from sqlalchemy import Column, ForeignKey, Integer, String, DateTime
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
