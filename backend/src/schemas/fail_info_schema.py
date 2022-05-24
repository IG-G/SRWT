from datetime import datetime
from pydantic import BaseModel


class ReturnFailInfo(BaseModel):
    reportTime: datetime
    message: str
    FailInfoID: int
    testCaseID: int

    class Config:
        orm_mode = True
