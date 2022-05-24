from datetime import timedelta
from typing import List, Dict

from fastapi import Depends, FastAPI, HTTPException
from fastapi.params import File
from sqlalchemy.orm import Session
from starlette.responses import Response

from src import models
from src.auth import (
    create_access_token,
    authenticate_user,
    ACCESS_TOKEN_EXPIRE_HOURS,
    check_auth,
    add_user,
)
from src.schemas.token_schema import Token
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
from src.schemas.logs_schema import Logs, ReturnLogElement
from src.schemas.screenshot_schema import (
    ScreenshotInfo,
    ScreenshotID,
    FullScreenshotInfo,
)
from src.schemas.fail_info_schema import ReturnFailInfo
from src.cruds import campaign as crud_campaign, fail_info
from src.cruds import test_case as crud_test_case
from src.cruds import logs as crud_log
from src.cruds import screenshots
from src.database import SessionLocal, engine
from fastapi.security import OAuth2PasswordRequestForm

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


def assert_campaign_exists(db: Session, campaign_id: int):
    campaign = crud_campaign.get_test_campaign_by_id(db, campaign_id)
    if campaign is None:
        raise HTTPException(
            status_code=404, detail=f"Campaign with ID = {campaign_id} not found"
        )


# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.post("/admin/users/add")
def add_user_with_password(
    new_username: str,
    new_password: str,
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db),
):
    return add_user(new_username, new_password, form_data, db)


@app.get("/admin/users")
def get_all_users(
    limit: int = 10, payload: Dict = Depends(check_auth), db: Session = Depends(get_db)
):
    return db.query(models.AuthorizationInfo).limit(limit).all()


@app.post("/token", response_model=Token)
def login_for_access_token(
    form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)
):
    if not authenticate_user(form_data.username, form_data.password, db):
        raise HTTPException(
            status_code=401,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(hours=ACCESS_TOKEN_EXPIRE_HOURS)
    access_token = create_access_token(
        data={"sub": form_data.username}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}


@app.post("/campaigns/begin", response_model=TestCampaign)
def create_test_campaign(
    new_campaign: TestCampaignCreate,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    return crud_campaign.create_test_campaign(db=db, campaign=new_campaign)


@app.get("/campaigns/", response_model=List[TestCampaign])
def get_test_campaigns(
    limit: int = 100, db: Session = Depends(get_db), payload: Dict = Depends(check_auth)
):
    return crud_campaign.get_test_campaigns(db, limit=limit)


@app.get("/campaigns/{campaign_id}", response_model=TestCampaign)
def get_test_campaign(
    campaign_id: int, db: Session = Depends(get_db), payload: Dict = Depends(check_auth)
):
    campaign = crud_campaign.get_test_campaign_by_id(db, campaign_id=campaign_id)
    if campaign is None:
        raise HTTPException(status_code=404, detail="Campaign not found")
    return campaign


@app.put("/campaigns/{campaign_id}/end", response_model=TestCampaign)
def end_test_campaign(
    campaign_id: int,
    campaign_end: TestCampaignEnd,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    assert_campaign_exists(db, campaign_id)
    return crud_campaign.end_test_campaign(
        db, campaign_id=campaign_id, campaign=campaign_end
    )


@app.post("/testcases/{campaign_id}/begin", response_model=TestCaseCreateResponse)
def begin_test_case(
    campaign_id: int,
    test_case: TestCaseCreate,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    return crud_test_case.create_test_case(
        db, test_case=test_case, campaign_id=campaign_id
    )


@app.get("/testcases/{campaign_id}", response_model=List[TestCase])
def get_test_cases(
    campaign_id: int,
    limit: int = 100,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    return crud_test_case.get_test_cases(db, campaign_id=campaign_id, limit=limit)


@app.get("/testcases/{campaign_id}/{test_case_id}", response_model=TestCase)
def get_test_case(
    campaign_id: int,
    test_case_id: int,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
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
    payload: Dict = Depends(check_auth),
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
    payload: Dict = Depends(check_auth),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    crud_test_case.fail_test_case(
        db, campaign_id=campaign_id, test_case_id=test_case_id, fail_info=fail_info
    )


@app.post("/logs/{campaign_id}/{test_case_id}/create", response_model=None)
def add_logs_to_test_case(
    campaign_id: int,
    test_case_id: int,
    logs: Logs,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    crud_log.add_logs_to_database(db, test_case_id, logs)


@app.get("/logs/{campaign_id}/{test_case_id}", response_model=List[ReturnLogElement])
def get_logs_for_given_test_case_id(
    campaign_id: int,
    test_case_id: int,
    level: str = None,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
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
    payload: Dict = Depends(check_auth),
):
    assert_campaign_and_test_case_exist(db, campaign_id, test_case_id)
    return screenshots.add_path_to_screenshot(db, test_case_id, screenshot_info)


@app.put("/screenshots/{screenshot_id}/")
def save_screenshot(
    screenshot_id: int,
    file: bytes = File(...),
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    screenshots.save_screenshot(db, screenshot_id, file)


@app.get("/screenshots/{screenshot_id}/info", response_model=FullScreenshotInfo)
def get_screenshot_info(
    screenshot_id: int,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    result = screenshots.get_screenshot_info_by_id(screenshot_id, db)
    if result is None:
        raise HTTPException(status_code=404, detail="Screenshot does not exist")
    return result


@app.get(
    "/screenshots/{screenshot_id}/file",
    responses={200: {"content": {"image/png": {}}}},
    response_class=Response,
)
def get_image(screenshot_id: int, db: Session = Depends(get_db)):
    file = screenshots.retrieve_screenshot_from_database(
        screenshot_id=screenshot_id, db=db
    )
    return Response(content=file, media_type="image/png")


@app.get("/screenshots/{test_case_id}", response_model=List[FullScreenshotInfo])
def get_screenshots_given_test_case(
    test_case_id: int,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    result = screenshots.get_screenshot_id_given_test_case_id(
        test_case_id=test_case_id, db=db
    )
    return result


@app.get("/fails/{test_case_id}", response_model=List[ReturnFailInfo])
def get_fail_info_given_test_case(
    test_case_id: int,
    db: Session = Depends(get_db),
    payload: Dict = Depends(check_auth),
):
    return fail_info.get_fail_info(db, test_case_id)
