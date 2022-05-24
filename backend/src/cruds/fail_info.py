from sqlalchemy.orm import Session

from src import models
from src.logger import log


def get_fail_info(db: Session, test_case_id: int):
    result = (
        db.query(models.FailInfo)
        .filter(models.FailInfo.testCaseID == test_case_id)
        .all()
    )
    log.info(f"Collected results for fail info given test_case_id = {test_case_id}")
    return result
