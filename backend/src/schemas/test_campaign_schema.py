import datetime
from typing import Optional

from pydantic import BaseModel


class TestCampaignCreate(BaseModel):
    campaignName: str
    envName: str
    username: str

    class Config:
        orm_mode = True


class TestCampaignEnd(BaseModel):
    status: str

    class Config:
        orm_mode = True


class TestCampaign(BaseModel):
    campaignName: str
    envName: str
    testCampaignID: int
    begTime: datetime.datetime
    endTime: Optional[datetime.datetime]
    status: str
    username: str

    class Config:
        orm_mode = True
