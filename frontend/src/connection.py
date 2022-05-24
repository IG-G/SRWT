from datetime import datetime
from typing import Any
import requests
import env

env = env.Settings()


class ConnectionClient:
    def __init__(self):
        self.jwt_token = None
        self.url = env.backend_url
        self.username = env.username
        self.password = env.password

    def create_frontend_user(self):
        path = (
            self.url
            + f"admin/users/add?new_username={self.username}&new_password={self.password}"
        )
        headers = {"Content-Type": "application/x-www-form-urlencoded"}
        data = f"grant_type=&username={env.admin_username}&password={env.admin_password}&scope=&client_id=&client_secret="
        response = requests.post(path, headers=headers, data=data)
        print(response.json())
        response.raise_for_status()

    def _send_get_request(self, endpoint: str):
        if self.jwt_token is None:
            self._get_access_token()
        response = requests.get(
            url=self.url + endpoint,
            headers={"Authorization": f"Bearer {self.jwt_token}"},
        )
        if response.status_code == 401:
            # timeout on token - improve this part later on
            self.jwt_token = None
        response.raise_for_status()
        return response

    def _send_get_request_and_return_json(self, endpoint: str) -> Any:
        return self._send_get_request(endpoint).json()

    def _send_request_for_screenshot(self, endpoint: str) -> bytes:
        return self._send_get_request(endpoint).content

    def _get_access_token(self):
        headers = {"Content-Type": "application/x-www-form-urlencoded"}
        data = f"grant_type=&username={self.username}&password={self.password}&scope=&client_id=&client_secret="
        response = requests.post(url=self.url + "token", headers=headers, data=data)
        response.raise_for_status()
        self.jwt_token = response.json()["access_token"]

    @staticmethod
    def change_string_into_date(date_in_str: str) -> datetime:
        return datetime.strptime(date_in_str, "%Y-%m-%dT%H:%M:%S.%f")

    def get_campaigns(self):
        return self._send_get_request_and_return_json(endpoint="campaigns/")

    def get_campaign(self, campaign_id):
        return self._send_get_request_and_return_json(
            endpoint=f"campaigns/{campaign_id}"
        )

    def get_test_cases_given_campaign(self, campaign_id: int):
        return self._send_get_request_and_return_json(
            endpoint=f"testcases/{campaign_id}"
        )

    def get_test_case_given_id(self, campaign_id: int, test_case_id: int):
        return self._send_get_request_and_return_json(
            endpoint=f"testcases/{campaign_id}/{test_case_id}"
        )

    def get_logs_given_test_case(self, campaign_id: int, test_case_id: int):
        return self._send_get_request_and_return_json(
            endpoint=f"logs/{campaign_id}/{test_case_id}"
        )

    def get_screenshot_info_given_test_case(self, test_case_id: int):
        return self._send_get_request_and_return_json(
            endpoint=f"screenshots/{test_case_id}"
        )

    def get_screenshot_info_given_id(self, screenshot_id: int):
        return self._send_get_request_and_return_json(
            endpoint=f"screenshots/{screenshot_id}/info"
        )

    def get_screenshot_file(self, screenshot_id: int):
        return self._send_request_for_screenshot(f"screenshots/{screenshot_id}/file")

    def get_fails_given_test_case(self, test_case_id: int):
        return self._send_get_request_and_return_json(f"fails/{test_case_id}")
