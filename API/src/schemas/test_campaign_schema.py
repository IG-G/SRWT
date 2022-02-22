import datetime
from typing import Optional

from pydantic import BaseModel


class TestCampaignCreate(BaseModel):
    campaignName: str
    envName: str

    class Config:
        orm_mode = True


class TestCampaignEnd(BaseModel):
    status: str

    class Config:
        orm_mode = True


class TestCampaign(BaseModel):
    campaignName: str
    envName: str
    id: int
    begTime: datetime.datetime
    endTime: Optional[datetime.datetime]
    status: str

    class Config:
        orm_mode = True
