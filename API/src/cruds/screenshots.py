from datetime import datetime

from fastapi import UploadFile, HTTPException
from fastapi.params import File
from src.logger import log

from sqlalchemy.orm import Session
from src import models
from src.schemas.screenshot_schema import (
    ScreenshotInfo,
    ScreenshotID,
    FullScreenshotInfo,
)


def get_all_screenshots(db: Session, limit: int):
    return db.query(models.ScreenshotInfo).limit(limit).all()


def add_path_to_screenshot(
    db: Session, test_case_id: int, screenshot_info: ScreenshotInfo
) -> ScreenshotID:
    db_screenshot_info = models.ScreenshotInfo(
        reportTime=datetime.strptime(
            screenshot_info.datetime[:-3], "%Y-%m-%dT%H:%M:%S.%f"
        ),
        path=screenshot_info.path,
        testCaseID=test_case_id,
    )
    db.add(db_screenshot_info)
    db.commit()
    db.refresh(db_screenshot_info)
    log.info(
        f"Added new screenshot path: {db_screenshot_info.path} with id={db_screenshot_info.id}"
    )
    return ScreenshotID(id=db_screenshot_info.id)


def get_path_given_screenshot_id(db: Session, screenshot_id: int) -> str:
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.id)
        .first()
    )
    return result.path


def save_screenshot(db: Session, screenshot_id: int, file: bytes = File(...)):
    path_to_save = get_path_given_screenshot_id(db, screenshot_id)
    log.info(f"Path to file: {path_to_save}")
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.id)
        .update(values={models.ScreenshotInfo.screenshot: file})
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Screenshot with id: {screenshot_id} cannot be save in database",
        )
    db.commit()


def retrieve_screenshot_from_database(db: Session, screenshot_id: int) -> bytes:
    """
    To save file locally
    with open(
        get_path_given_screenshot_id(db=db, screenshot_id=screenshot_id), "wb+"
    ) as f:
        f.write(retrieve_screenshot_from_database(db, screenshot_id))
    """

    log.info(f"Requested file: {screenshot_id}")
    result = (
        db.query(models.ScreenshotInfo)
        .filter(screenshot_id == models.ScreenshotInfo.id)
        .first()
    )
    if result is None:
        raise HTTPException(
            status_code=404,
            detail=f"Screenshot with id: {screenshot_id} cannot be save in database",
        )
    return result.screenshot
