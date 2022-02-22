from enum import Enum


class TestCaseStatus(Enum):
    IN_PROGRESS = "IN PROGRESS"
    FAILED = "FAILED"
    PASSED = "PASSED"
    INTERRUPTED = "INTERRUPTED"


class CampaignStatus(Enum):
    CREATED = "CREATED"
    RUNNING = "RUNNING"
    FINISHED = "FINISHED"
    FINISHED_WITH_FAILS = "FINISHED_WITH_FAILS"
