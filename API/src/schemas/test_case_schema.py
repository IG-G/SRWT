import datetime
from typing import Optional

from pydantic import BaseModel


class TestCaseBase(BaseModel):
    pass


class TestCaseCreate(TestCaseBase):
    testCaseName: str

    class Config:
        orm_mode = True


class TestCaseEnd(TestCaseBase):
    status: str

    class Config:
        orm_mode = True


class TestCase(BaseModel):
    testCaseID: int
    testCaseName: str
    campaignID: int
    begTime: datetime.datetime
    endTime: Optional[datetime.datetime]
    status: str

    class Config:
        orm_mode = True
