import datetime

from pydantic import BaseModel


class ScreenshotInfo(BaseModel):
    reportTime: str

    class Config:
        orm_mode = True


class ScreenshotID(BaseModel):
    screenshotID: int

    class Config:
        orm_mode = True


class FullScreenshotInfo(BaseModel):
    screenshotID: int
    testCaseID: int
    reportTime: datetime.datetime

    class Config:
        orm_mode = True
