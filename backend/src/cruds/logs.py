from sqlalchemy.orm import Session

from datetime import datetime

from src import models
from src.logger import log
from src.schemas.logs_schema import Logs


def add_logs_to_database(db: Session, test_case_id: int, logs: Logs):
    for log_element in logs.logs:
        db_logs = models.LogInfo(
            reportTime=datetime.strptime(
                log_element.reportTime[:-3], "%Y-%m-%dT%H:%M:%S.%f"
            ),
            message=log_element.message,
            levelStatus=log_element.levelStatus,
            testCaseID=test_case_id,
        )
        db.add(db_logs)
        db.commit()
        db.refresh(db_logs)
        log.info(f"Added new log with message {log_element.message}")


def get_logs(db: Session, test_case_id: int):
    result = (
        db.query(models.LogInfo).filter(models.LogInfo.testCaseID == test_case_id).all()
    )
    log.info(f"Collected results for logs given test_case_id = {test_case_id}")
    return result


def get_logs_on_given_level(db: Session, test_case_id: int, level: str):
    result = (
        db.query(models.LogInfo)
        .filter(
            models.LogInfo.testCaseID == test_case_id,
            models.LogInfo.levelStatus == level,
        )
        .all()
    )
    log.info(
        f"Collected results for logs given test_case_id = {test_case_id} and given level {level}"
    )
    return result
