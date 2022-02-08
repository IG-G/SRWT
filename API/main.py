import mysql.connector
from fastapi import FastAPI, Path
from pydantic import BaseModel


class TestCampaignInitParams(BaseModel):
    repositoryName: str
    envName: str
    dateOfBeginning: str


class TestCampaignEndingParams(BaseModel):
    status: str
    dateOfEnding: str


db_connection = mysql.connector.connect(
    host="localhost",
    user="api",
    password="passwordAPI",
    database="testreport"
)
cursor = db_connection.cursor()
test_campaigns_id = [0]
app = FastAPI()


@app.post("/campaigns/")
async def create_campaign(campaign: TestCampaignInitParams):
    i = test_campaigns_id[-1]
    i = i + 1
    test_campaigns_id.append(i)

    sql = "INSERT INTO TestCampaign(id, repositoryName, envName, begTime, status) VALUES ('" \
          + i.__str__() + "', '" + campaign.repositoryName + "', '" + campaign.envName + "', '" \
          + campaign.dateOfBeginning + "', '" + "STARTED" + "')"
    cursor.execute(sql)
    db_connection.commit()
    return i


@app.put("/campaigns/{campaign_id}")
async def end_campaign(
        campaign: TestCampaignEndingParams,
        campaign_id: int = Path(..., title="Campaign ID from POST request")
):
    sql = "UPDATE TestCampaign SET endTime = '" + campaign.dateOfEnding + "', status = '" + campaign.status \
          + "' WHERE id = " + campaign_id.__str__() + ";"
    cursor.execute(sql)
    db_connection.commit()
    return
