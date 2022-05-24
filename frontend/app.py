import requests
from flask import Flask, render_template, url_for

from src import utils
from src.connection import ConnectionClient
from src.test_case_report import (
    create_log_html_element,
    create_screenshot_html_element,
    create_fail_info_html_element,
)

app = Flask(__name__)

conn = ConnectionClient()
try:
    conn.create_frontend_user()
except requests.exceptions.HTTPError:
    pass


@app.route("/")
def index():
    campaigns = conn.get_campaigns()
    corrected_campaigns = []
    for campaign in campaigns:
        corrected_campaigns.append(utils.datetime_correction(campaign))
    return render_template("campaigns.html", result=campaigns)


@app.route("/campaigns/<int:campaign_id>")
def get_campaign_report(campaign_id):
    test_cases = conn.get_test_cases_given_campaign(campaign_id)
    corrected_test_cases = []
    for test_case in test_cases:
        corrected_test_cases.append(utils.datetime_correction(test_case))
    campaign_name = conn.get_campaign(campaign_id)["campaignName"]
    return render_template(
        "campaign_details.html",
        result=corrected_test_cases,
        campaign_name=campaign_name,
    )


@app.route("/testcases/<int:campaign_id>/<int:test_case_id>")
def get_test_case_report(campaign_id, test_case_id):
    test_case = conn.get_test_case_given_id(campaign_id, test_case_id)
    test_case = utils.datetime_correction(test_case)
    campaign_name = conn.get_campaign(campaign_id)["campaignName"]
    logs = create_log_html_element(campaign_id, test_case_id)
    fails = create_fail_info_html_element(test_case_id)
    screenshots = create_screenshot_html_element(test_case_id)
    return render_template(
        "test_case.html",
        testcase=test_case,
        campaign_name=campaign_name,
        fails=fails,
        logs=logs,
        screenshots=screenshots,
    )


@app.route("/screenshots/<int:screenshot_id>")
def get_screenshot_file(screenshot_id):
    screenshot_info = conn.get_screenshot_info_given_id(screenshot_id)
    return render_template(
        "screenshot.html",
        info=screenshot_info,
        path=url_for("download_file", screenshot_id=screenshot_id),
    )


@app.route("/file/<int:screenshot_id>")
def download_file(screenshot_id):
    file_bytes = conn.get_screenshot_file(screenshot_id)
    return file_bytes
