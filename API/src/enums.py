from enum import Enum


class TestCaseStatus(Enum):
    IN_PROGRESS = "IN PROGRESS"
    FAILED = "FAILED"
    PASSED = "PASSED"
    INTERRUPTED = "INTERRUPTED"


def check_if_test_case_status_is_valid(new_status: str) -> bool:
    return new_status in TestCaseStatus.__members__


class CampaignStatus(Enum):
    CREATED = "CREATED"
    RUNNING = "RUNNING"
    FINISHED = "FINISHED"
    FINISHED_WITH_FAILS = "FINISHED_WITH_FAILS"


def check_if_campaign_status_is_valid(new_status: str) -> bool:
    return new_status in CampaignStatus.__members__
