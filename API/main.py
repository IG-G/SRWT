from typing import Optional

from fastapi import FastAPI
from pydantic import BaseModel
import logging
import mysql.connector


class TestCampaignParams(BaseModel):
    repositoryName: str
    envName: str
    dateOfBeginning: str


db_connection = mysql.connector.connect(
    host="localhost",
    user="api",
    password="passwordAPI",
    database="testreport"
)

cursor = db_connection.cursor()

test_campaigns_id = [0]

app = FastAPI()


@app.get("/")
async def get_index():
    return "Hello word"


@app.post("/campaigns/")
async def create_campaign(campaign: TestCampaignParams):

    i = test_campaigns_id[-1]
    i = i + 1
    test_campaigns_id.append(i)

    sql = "INSERT INTO TestCampaign(id, repositoryName, envName, begTime) VALUES (" \
          + i.__str__() + ", " + campaign.repositoryName + ", " + campaign.envName + ", " + campaign.dateOfBeginning + \
          ")"
    cursor.execute(sql)

    db_connection.commit()

    return i


@app.post("/items/")
async def create_item(item: TestCampaignParams):
    logging.basicConfig(filename='app.log', filemode='w', format='%(name)s - %(levelname)s - %(message)s')
    logging.warning('This will get logged to a')
    logging.warning('Test ' + item.repositoryName)

    sql = "INSERT INTO TestCampaign(id, name, status) VALUES (2, 'WEB2', 'END')"
    cursor.execute(sql)

    db_connection.commit()
    return "Ok"
