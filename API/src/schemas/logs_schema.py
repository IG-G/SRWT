from typing import List
from pydantic import BaseModel


class LogElement(BaseModel):
    datetime: str
    level: str
    message: str

    class Config:
        orm_mode = True


class Logs(BaseModel):
    logs: List[LogElement]

    class Config:
        orm_mode = True
