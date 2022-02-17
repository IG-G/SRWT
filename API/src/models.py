from sqlalchemy import Column, ForeignKey, Integer, String, DateTime
from sqlalchemy.orm import relationship
from .database import Base
from sqlalchemy.sql import func
import datetime


class TestCampaign(Base):
    __tablename__ = "TestCampaign"

    id = Column(Integer, primary_key=True, index=True)
    repositoryName = Column(String(30))
    envName = Column(String(30))
    status = Column(String(30))
    begTime = Column(DateTime, server_default=func.now())
    endTime = Column(DateTime, default=None)
