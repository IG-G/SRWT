from datetime import datetime

from sqlalchemy.orm import Session
from sqlalchemy.sql import func
from fastapi import HTTPException

from src import models
from src.logger import log
from src.enums import CampaignStatus, check_if_campaign_status_is_valid
from src.schemas.test_campaign_schema import TestCampaignCreate, TestCampaignEnd
from src.const import const


def get_test_campaign_by_id(db: Session, campaign_id: int):
    log.info(f"Query for campaign id, campaign_id={campaign_id}")
    return (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.testCampaignID == campaign_id)
        .first()
    )


def get_test_campaigns(db: Session, limit: int = 100):
    log.info(f"Query for all campaigns, limit={limit}")
    return db.query(models.TestCampaign).limit(limit).all()


def create_test_campaign(db: Session, campaign: TestCampaignCreate):
    if len(campaign.campaignName) > const.MAX_CAMPAIGN_NAME:
        raise HTTPException(
            status_code=422,
            detail=f"Campaign name exceeds max length ({const.MAX_CAMPAIGN_NAME})",
        )
    if len(campaign.envName) > const.MAX_ENV_NAME:
        raise HTTPException(
            status_code=422,
            detail=f"Campaign env exceeds max length ({const.MAX_ENV_NAME})",
        )
    db_test_campaign = models.TestCampaign(
        campaignName=campaign.campaignName,
        envName=campaign.envName,
        status=CampaignStatus.RUNNING.name,
        username=campaign.username,
        begTime=datetime.now(),
    )
    log.info(
        f"Create new campaign, name={db_test_campaign.campaignName}"
        + f" env={db_test_campaign.envName} id={db_test_campaign.testCampaignID}"
    )
    db.add(db_test_campaign)
    db.commit()
    db.refresh(db_test_campaign)
    return db_test_campaign


def end_test_campaign(db: Session, campaign_id: int, campaign: TestCampaignEnd):
    if not check_if_campaign_status_is_valid(campaign.status):
        raise HTTPException(
            status_code=422, detail=f"Campaign status {campaign.status} does not exist"
        )
    log.info(f"End test campaign id={campaign_id}, data={campaign}")
    result = (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.testCampaignID == campaign_id)
        .update(
            values={
                models.TestCampaign.status: campaign.status,
                models.TestCampaign.endTime: datetime.now(),
            }
        )
    )
    if result is None:
        raise HTTPException(
            status_code=404, detail=f"Campaign {campaign_id} does not exist"
        )
    db.commit()
    return (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.testCampaignID == campaign_id)
        .first()
    )
