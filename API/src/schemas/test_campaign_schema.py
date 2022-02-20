import datetime
from typing import List, Optional

from pydantic import BaseModel


class TestCampaignBase(BaseModel):
    pass


class TestCampaignCreate(TestCampaignBase):
    campaignName: str
    envName: str

    class Config:
        orm_mode = True


class TestCampaignEnd(TestCampaignBase):
    status: str

    class Config:
        orm_mode = True


class TestCampaign(TestCampaignBase):
    campaignName: str
    envName: str
    id: int
    begTime: datetime.datetime
    endTime: Optional[datetime.datetime]
    status: str

    class Config:
        orm_mode = True
