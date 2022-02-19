import datetime
from typing import List, Optional

from pydantic import BaseModel


class TestCampaignBase(BaseModel):
    pass


class TestCampaignCreate(TestCampaignBase):
    repositoryName: str
    envName: str

    class Config:
        orm_mode = True


class TestCampaignEnd(TestCampaignBase):
    status: str


class TestCampaign(TestCampaignBase):
    repositoryName: str
    envName: str
    id: int
    begTime: datetime.datetime
    endTime: Optional[datetime.datetime]
    status: str

    class Config:
        orm_mode = True
