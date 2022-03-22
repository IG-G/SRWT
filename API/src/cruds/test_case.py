from sqlalchemy.orm import Session
from fastapi import HTTPException
from sqlalchemy.sql import func

from src.cruds.campaign import get_test_campaign_by_id
from src.logger import log
from src.enums import TestCaseStatus, check_if_test_case_status_is_valid
from src import models
from src.schemas.test_case_schema import TestCaseCreate, TestCaseEnd, TestCaseFail
from src.const import const


def get_test_case_by_id(db: Session, campaign_id: int, test_case_id: int):
    log.info(f"Query for test case id={test_case_id} in campaign={campaign_id}")
    return (
        db.query(models.TestCase)
        .filter(
            models.TestCase.testCampaignID == campaign_id
            and models.TestCase.id == test_case_id
        )
        .first()
    )


def get_test_cases(db: Session, campaign_id: int, limit: int = 100):
    log.info(f"Query for all test cases in campaign={campaign_id} with limit={limit}")
    return (
        db.query(models.TestCase)
        .filter(models.TestCase.testCampaignID == campaign_id)
        .limit(limit)
        .all()
    )


def create_test_case(db: Session, test_case: TestCaseCreate, campaign_id: int):
    if get_test_campaign_by_id(db, campaign_id=campaign_id) is None:
        raise HTTPException(
            status_code=404, detail=f"Campaign: {campaign_id} does not exist"
        )
    if len(test_case.testCaseName) > const.MAX_TEST_CASE_NAME:
        raise HTTPException(
            status_code=422,
            detail=f"Test case name exceeds max length ({const.MAX_TEST_CASE_NAME})",
        )
    db_test_case = models.TestCase(
        testCaseName=test_case.testCaseName,
        status=TestCaseStatus.IN_PROGRESS.name,
        testCampaignID=campaign_id,
    )
    log.info(
        f"Created test case, name={db_test_case.testCaseName}"
        + f" id={db_test_case.id} campaign={db_test_case.testCampaignID}"
    )
    db.add(db_test_case)
    db.commit()
    db.refresh(db_test_case)
    return db_test_case


def end_test_case(
    db: Session, test_case: TestCaseEnd, test_case_id: int, campaign_id: int
):
    if not check_if_test_case_status_is_valid(test_case.status):
        raise HTTPException(
            status_code=422, detail=f"Status {test_case.status} does not exist"
        )
    log.info(
        f"Results for test case, test_case={test_case_id}, status={test_case.status}"
    )
    result = (
        db.query(models.TestCase)
        .filter(
            models.TestCase.testCampaignID == campaign_id,
            models.TestCase.id == test_case_id,
        )
        .update(
            values={
                models.TestCase.status: test_case.status,
                models.TestCase.endTime: func.now(),
            }
        )
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Test case id: {test_case_id} in campaign {campaign_id} does not exist",
        )
    else:
        db.commit()


def fail_test_case(
    db: Session, campaign_id: int, test_case_id: int, fail_info: TestCaseFail
):
    if len(fail_info.message) > const.MAX_MESSAGE_LENGTH:
        raise HTTPException(
            status_code=422,
            detail=f"Message exceeds max length ({const.MAX_MESSAGE_LENGTH})",
        )
    db_fail_info = models.FailInfo(
        testCaseID=test_case_id,
        message=fail_info.message,
    )
    log.info(
        f"Created fail info, testcase={db_fail_info.testCaseID} id={db_fail_info.id} data={db_fail_info.message}"
    )
    db.add(db_fail_info)
    db.commit()
    db.refresh(db_fail_info)
    result = (
        db.query(models.TestCase)
        .filter(
            models.TestCase.testCampaignID == campaign_id,
            models.TestCase.id == test_case_id,
        )
        .update(values={models.TestCase.status: TestCaseStatus.FAILED.name})
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Test case id: {test_case_id} in campaign {campaign_id} does not exist",
        )
    log.info("Updated row with corresponding test case id")
