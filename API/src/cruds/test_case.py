import random
from sqlalchemy.orm import Session
from sqlalchemy.sql import func

from src import models
from src.schemas.test_case_schema import TestCaseCreate, TestCaseEnd


def get_test_case_by_id(db: Session, campaign_id: int, test_case_id: int):
    return (
        db.query(models.TestCase)
        .filter(
            models.TestCase.testCampaignID == campaign_id
            and models.TestCase.id == test_case_id
        )
        .first()
    )


def get_test_cases(db: Session, campaign_id: int, limit: int = 100):
    return (
        db.query(models.TestCase)
        .filter(models.TestCase.testCampaignID == campaign_id)
        .limit(limit)
        .all()
    )


def create_test_case(db: Session, test_case: TestCaseCreate, campaign_id: int):
    new_id = random.randint(0, 1000)
    db_test_case = models.TestCase(
        id=new_id,
        testCaseName=test_case.testCaseName,
        status="STARTED",
        testCampaignID=campaign_id,
    )
    db.add(db_test_case)
    db.commit()
    db.refresh(db_test_case)
    return db_test_case


def end_test_case(
    db: Session, test_case: TestCaseEnd, test_case_id: int, campaign_id: int
):
    db.query(models.TestCase).filter(
        models.TestCase.testCampaignID == campaign_id
        and models.TestCase.id == test_case_id
    ).update(
        values={
            models.TestCase.status: test_case.status,
            models.TestCase.endTime: func.now(),
        }
    )
    db.commit()
