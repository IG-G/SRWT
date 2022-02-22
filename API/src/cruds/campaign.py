import random

from sqlalchemy.orm import Session
from sqlalchemy.sql import func
from fastapi import HTTPException

from src import models
from src.enums import CampaignStatus
from src.schemas.test_campaign_schema import TestCampaignCreate, TestCampaignEnd


def get_test_campaign_by_id(db: Session, campaign_id: int):
    return (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.id == campaign_id)
        .first()
    )


def get_test_campaigns(db: Session, limit: int = 100):
    return db.query(models.TestCampaign).limit(limit).all()


def create_test_campaign(db: Session, campaign: TestCampaignCreate):
    new_id = random.randint(0, 1000)
    db_test_campaign = models.TestCampaign(
        id=new_id,
        campaignName=campaign.campaignName,
        envName=campaign.envName,
        status=CampaignStatus.RUNNING.name,
    )
    db.add(db_test_campaign)
    db.commit()
    db.refresh(db_test_campaign)
    return db_test_campaign


def end_test_campaign(db: Session, campaign_id: int, campaign: TestCampaignEnd):
    result = (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.id == campaign_id)
        .update(
            values={
                models.TestCampaign.status: campaign.status,
                models.TestCampaign.endTime: func.now(),
            }
        )
    )
    if result is None:
        raise HTTPException(
            status_code=404, detail=f"Campaign {campaign_id} does not exist"
        )
    db.commit()
