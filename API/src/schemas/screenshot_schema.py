from pydantic import BaseModel


class ScreenshotInfo(BaseModel):
    path: str
    datetime: str

    class Config:
        orm_mode = True


class ScreenshotID(BaseModel):
    id: int

    class Config:
        orm_mode = True


class FullScreenshotInfo(ScreenshotInfo):
    id: int
    testCaseID: int

    class Config:
        orm_mode = True
