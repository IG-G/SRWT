from flask import url_for

from src import utils
from src.connection import ConnectionClient

conn = ConnectionClient()


def create_log_html_element(campaign_id: int, test_case_id: int):
    logs = utils.correct_logs_format(
        conn.get_logs_given_test_case(campaign_id, test_case_id)
    )
    if not logs:
        return "<h4>No logs were collected</h4>"
    beginning_of_table = """<h4>Collected logs</h4>
                    <table>
                        <tr>
                            <th>Reported time</th>
                            <th>Level</th>
                            <th>Message</th>
                        </tr>"""
    logs_row = ""
    for log in logs:
        string_row = f"""
                        <tr>
                            <td>{log['reportTime']}</td>
                            <td>{log['levelStatus']}</td>
                            <td>{log['message']}</td>
                        </tr>
                        """
        logs_row += string_row
    end_of_table = "</table>"
    return beginning_of_table + logs_row + end_of_table


def create_screenshot_html_element(test_case_id: int):
    list_of_screenshots = conn.get_screenshot_info_given_test_case(test_case_id)
    if not list_of_screenshots:
        return "<h4>No screenshots were collected</h4>"
    beginning_of_screenshots_html = "<h4>Collected screenshots</h4>"
    screenshot_elements = ""
    for screenshot in list_of_screenshots:
        screenshot_id = screenshot["screenshotID"]
        path = url_for("download_file", screenshot_id=screenshot_id)
        string_row = f"""
                        <p>Screenshot was taken {utils.cut_time_to_seconds(screenshot, 'reportTime')['reportTime']}</p>
                        <a href={url_for('get_screenshot_file', screenshot_id=screenshot_id)}>
                            <img class='small_img' src="{path}">
                        </a>
                        """
        screenshot_elements += string_row
    return beginning_of_screenshots_html + screenshot_elements


def create_fail_info_html_element(test_case_id: int):
    fails = conn.get_fails_given_test_case(test_case_id)
    if not fails:
        return "<h4>No fails recorded during execution</h4>"
    beginning_of_table = """<h4>Collected fails</h4>
                    <table>
                        <tr>
                            <th>Reported time</th>
                            <th>Message</th>
                        </tr>"""
    fail_row = ""
    for fail in fails:
        fail = utils.cut_time_to_seconds(fail, "reportTime")
        string_row = f"""
                        <tr>
                            <td>{fail['reportTime']}</td>
                            <td>{fail['message']}</td>
                        </tr>
                        """
        fail_row += string_row
    end_of_table = "</table>"
    return beginning_of_table + fail_row + end_of_table
