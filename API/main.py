from typing import Optional

from fastapi import FastAPI
from pydantic import BaseModel
import logging


class Item(BaseModel):
    name: str
    description: Optional[str] = None
    price: float
    tax: Optional[float] = None


app = FastAPI()


@app.get("/")
async def get_index():
    return "Hello word"


@app.post("/items/")
async def create_item(item: Item):
    logging.basicConfig(filename='app.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
    logging.warning('This will get logged to a')
    logging.warning('Test ' + item.name)
    return "Ok"
