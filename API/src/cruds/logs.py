import random

from sqlalchemy.orm import Session
from sqlalchemy.sql import func
from fastapi import HTTPException
from datetime import datetime

from src import models
from src.logger import log
from src.enums import CampaignStatus, check_if_campaign_status_is_valid
from src.schemas.logs_schema import Logs
from src.const import const


def add_logs_to_database(db: Session, test_case_id: int, logs: Logs):
    for log_element in logs.logs:
        new_id = random.randint(0, 100000)
        db_logs = models.LogInfo(
            id=new_id,
            reportTime=datetime.strptime(
                log_element.datetime[:-3], "%Y-%m-%dT%H:%M:%S.%f"
            ),
            message=log_element.message,
            level_status=log_element.level,
            testCaseID=test_case_id,
        )
        db.add(db_logs)
        db.commit()
        db.refresh(db_logs)
        log.info(f"Added new log with message {log_element.message}")


def get_logs_for_test_cae_id():
    pass
