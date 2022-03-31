from typing import List

from fastapi import Depends, FastAPI, HTTPException, UploadFile
from fastapi.params import File
from sqlalchemy.orm import Session
from src import models
from src.schemas.test_campaign_schema import (
    TestCampaignCreate,
    TestCampaign,
    TestCampaignEnd,
)
from src.schemas.test_case_schema import (
    TestCaseCreate,
    TestCase,
    TestCaseEnd,
    TestCaseFail,
    TestCaseCreateResponse,
)
from src.schemas.logs_schema import Logs
from src.schemas.screenshot_schema import (
    ScreenshotInfo,
    ScreenshotID,
    FullScreenshotInfo,
)
from src.cruds import campaign as crud_campaign
from src.cruds import test_case as crud_test_case
from src.cruds import logs as crud_log
from src.cruds import screenshots
from src.database import SessionLocal, engine

models.Base.metadata.create_all(bind=engine)

app = FastAPI()


def assert_campaign_and_test_case_exist(
    db: Session, campaign_id: int, test_case_id: int
):
    test_case = crud_test_case.get_test_case_by_id(
        db, campaign_id=campaign_id, test_case_id=test_case_id
    )
    if test_case is None:
        raise HTTPException(status_code=404, detail="Test case or campaign not found")


# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.post("/campaigns/begin", response_model=TestCampaign)
def create_test_campaign(
    new_campaign: TestCampaignCreate, db: Session = Depends(get_db)
):
    return crud_campaign.create_test_campaign(db=db, campaign=new_campaign)


@app.get("/campaigns/", response_model=List[TestCampaign])
def get_test_campaigns(limit: int = 100, db: Session = Depends(get_db)):
    return crud_campaign.get_test_campaigns(db, limit=limit)


@app.get("/campaigns/{campaign_id}", response_model=TestCampaign)
def get_test_campaign(campaign_id: int, db: Session = Depends(get_db)):
    campaign = crud_campaign.get_test_campaign_by_id(db, campaign_id=campaign_id)
    if campaign is None:
        raise HTTPException(status_code=404, detail="Campaign not found")
    return campaign


@app.put("/campaigns/{campaign_id}/end", response_model=None)
def end_test_campaign(
    campaign_id: int, campaign_end: TestCampaignEnd, db: Session = Depends(get_db)
):
    crud_campaign.end_test_campaign(db, campaign_id=campaign_id, campaign=campaign_end)


@app.post("/testcases/{campaign_id}/begin", response_model=TestCaseCreateResponse)
def begin_test_case(
    campaign_id: int, test_case: TestCaseCreate, db: Session = Depends(get_db)
):
    begun_test_case = crud_test_case.create_test_case(
        db, test_case=test_case, campaign_id=campaign_id
    )
    return begun_test_case


@app.get("/testcases/{campaign_id}", response_model=List[TestCase])
def get_test_cases(campaign_id: int, limit: int = 100, db: Session = Depends(get_db)):
    return crud_test_case.get_test_cases(db, campaign_id=campaign_id, limit=limit)


@app.get("/testcases/{campaign_id}/{test_case_id}", response_model=TestCase)
def get_test_case(campaign_id: int, test_case_id: int, db: Session = Depends(get_db)):
    test_case = crud_test_case.get_test_case_by_id(
        db, campaign_id=campaign_id, test_case_id=test_case_id
    )
    if test_case is None:
        raise HTTPException(status_code=404, detail="Test case or campaign not found")
    return test_case


@app.put("/testcases/{campaign_id}/{test_case_id}/end", response_model=None)
def end_test_case(
    campaign_id: int,
    test_case_id: int,
    test_case: TestCaseEnd,
    db: Session = Depends(get_db),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    crud_test_case.end_test_case(
        db, campaign_id=campaign_id, test_case_id=test_case_id, test_case=test_case
    )


@app.post("/testcases/{campaign_id}/{test_case_id}/fail", response_model=None)
def fail_test_case(
    campaign_id: int,
    test_case_id: int,
    fail_info: TestCaseFail,
    db: Session = Depends(get_db),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    crud_test_case.fail_test_case(
        db, campaign_id=campaign_id, test_case_id=test_case_id, fail_info=fail_info
    )


@app.post("/logs/{campaign_id}/{test_case_id}/create", response_model=None)
def add_logs_to_test_case(
    campaign_id: int, test_case_id: int, logs: Logs, db: Session = Depends(get_db)
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    crud_log.add_logs_to_database(db, test_case_id, logs)


@app.get("/logs/{campaign_id}/{test_case_id}", response_model=Logs)
def get_logs_for_given_test_case_id(
    campaign_id: int,
    test_case_id: int,
    level: str = None,
    db: Session = Depends(get_db),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    if level is None:
        result = crud_log.get_logs(db, test_case_id)
    else:
        result = crud_log.get_logs_on_given_level(db, test_case_id, level)
    return result


@app.post("/screenshots/{campaign_id}/{test_case_id}/add", response_model=ScreenshotID)
def prepare_screenshot_for_given_test_case(
    campaign_id: int,
    test_case_id: int,
    screenshot_info: ScreenshotInfo,
    db: Session = Depends(get_db),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    return screenshots.add_path_to_screenshot(db, test_case_id, screenshot_info)


@app.post("/screenshots/{screenshot_id}/")
def save_screenshot(
    screenshot_id: int, file: bytes = File(...), db: Session = Depends(get_db)
):
    screenshots.save_screenshot(db, screenshot_id, file)


@app.get("/screenshots/")
def get_all_screenshots(limit: int = 100, db: Session = Depends(get_db)):
    return screenshots.get_all_screenshots(db, limit)
