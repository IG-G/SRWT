import random

from sqlalchemy.orm import Session
from sqlalchemy.sql import func

from . import models, schemas


def get_test_campaign_by_id(db: Session, campaign_id: int):
    return (
        db.query(models.TestCampaign)
        .filter(models.TestCampaign.id == campaign_id)
        .first()
    )


def get_test_campaigns(db: Session, limit: int = 100):
    return db.query(models.TestCampaign).limit(limit).all()


def create_test_campaign(db: Session, campaign: schemas.TestCampaignCreate):
    new_id = random.randint(0, 1000)
    db_test_campaign = models.TestCampaign(
        id=new_id,
        repositoryName=campaign.repositoryName,
        envName=campaign.envName,
        status="STARTED",
    )
    db.add(db_test_campaign)
    db.commit()
    db.refresh(db_test_campaign)
    return db_test_campaign


def end_test_campaign(db: Session, campaign_id: int, campaign: schemas.TestCampaignEnd):
    # TODO not safe!!!
    sql = f"UPDATE TestCampaign SET status='{campaign.status}', endTime={func.now()} WHERE id={campaign_id}"
    db.execute(sql)
    db.commit()
