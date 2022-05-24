from datetime import datetime
from typing import List

from fastapi import HTTPException
from fastapi.params import File
from src.logger import log

from sqlalchemy.orm import Session
from src import models
from src.schemas.screenshot_schema import (
    ScreenshotInfo,
    ScreenshotID,
    FullScreenshotInfo,
)


def add_path_to_screenshot(
    db: Session, test_case_id: int, screenshot_info: ScreenshotInfo
) -> ScreenshotID:
    db_screenshot_info = models.ScreenshotInfo(
        reportTime=datetime.strptime(
            screenshot_info.reportTime[:-3], "%Y-%m-%dT%H:%M:%S.%f"
        ),
        testCaseID=test_case_id,
    )
    db.add(db_screenshot_info)
    db.commit()
    db.refresh(db_screenshot_info)
    log.info(f"Added new screenshot with id={db_screenshot_info.screenshotID}")
    return ScreenshotID(screenshotID=db_screenshot_info.screenshotID)


def save_screenshot(db: Session, screenshot_id: int, file: bytes = File(...)):
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.screenshotID)
        .update(values={models.ScreenshotInfo.screenshot: file})
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Screenshot with id: {screenshot_id} cannot be save in database",
        )
    db.commit()


def retrieve_screenshot_from_database(db: Session, screenshot_id: int) -> bytes:
    log.info(f"Requested file: {screenshot_id}")
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.screenshotID)
        .first()
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Screenshot with id: {screenshot_id} cannot be save in database",
        )
    return result.screenshot


def get_screenshot_id_given_test_case_id(
    test_case_id: int, db: Session
) -> FullScreenshotInfo:
    result = (
        db.query(models.ScreenshotInfo)
        .filter(test_case_id == models.ScreenshotInfo.testCaseID)
        .with_entities(
            models.ScreenshotInfo.screenshotID,
            models.ScreenshotInfo.testCaseID,
            models.ScreenshotInfo.reportTime,
        )
        .all()
    )
    return result


def get_screenshot_info_by_id(screenshot_id: int, db: Session) -> FullScreenshotInfo:
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.screenshotID)
        .with_entities(
            models.ScreenshotInfo.screenshotID,
            models.ScreenshotInfo.testCaseID,
            models.ScreenshotInfo.reportTime,
        )
        .first()
    )
    if result is None:
        pass
    return result
