from datetime import datetime
from typing import List
from pydantic import BaseModel


class LogElement(BaseModel):
    reportTime: str
    levelStatus: str
    message: str

    class Config:
        orm_mode = True


class Logs(BaseModel):
    logs: List[LogElement]

    class Config:
        orm_mode = True


class ReturnLogElement(BaseModel):
    reportTime: datetime
    levelStatus: str
    message: str
    logInfoID: int
    testCaseID: int

    class Config:
        orm_mode = True
