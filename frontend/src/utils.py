from datetime import datetime
from typing import Dict, List


def calculate_duration(beg: datetime, end: datetime) -> str:
    if end < beg:
        return ""
    diff = end - beg
    hours, remainder = divmod(diff.seconds, 3600)
    minutes, seconds = divmod(remainder, 60)
    if hours > 0:
        s = f"{hours} h,  {minutes} m, {seconds} s"
    else:
        if minutes > 0:
            s = f"{minutes} m, {seconds} s"
        else:
            if seconds > 0:
                s = f"{seconds} s"
            else:
                s = "<0 s"
    return s


def add_duration_to_response(response: Dict) -> Dict:
    if response["begTime"] is None or response["endTime"] is None:
        response["duration"] = ""
        return response
    beg = datetime.strptime(response["begTime"], "%Y-%m-%dT%H:%M:%S.%f")
    end = datetime.strptime(response["endTime"], "%Y-%m-%dT%H:%M:%S.%f")
    response["duration"] = calculate_duration(beg, end)
    return response


def cut_time_to_seconds(response: Dict, time: str) -> Dict:
    if response[time] is None:
        response[time] = ""
        return response
    date_time = datetime.strptime(response[time], "%Y-%m-%dT%H:%M:%S.%f")
    response[time] = datetime.strftime(date_time, "%Y-%m-%d %H:%M:%S")
    return response


def datetime_correction(response: Dict) -> Dict:
    response = add_duration_to_response(response)
    response = cut_time_to_seconds(response, "begTime")
    response = cut_time_to_seconds(response, "endTime")
    return response


def correct_logs_format(logs: List[Dict]) -> List[Dict]:
    display_logs = []
    for log in logs:
        display_logs.append(cut_time_to_seconds(log, "reportTime"))
    return display_logs
