import os


def get_env_value_or_set_default(var: str, default: str) -> str:
    try:
        return os.environ[var]
    except KeyError:
        return default


class Settings:
    def __init__(self):
        self.username = get_env_value_or_set_default("username", "user")
        self.password = get_env_value_or_set_default("password", "password")
        self.backend_url = get_env_value_or_set_default(
            "backend_url", "http://localhost:8000/"
        )

        self.admin_username = get_env_value_or_set_default("admin_username", "user")
        self.admin_password = get_env_value_or_set_default("admin_password", "password")
