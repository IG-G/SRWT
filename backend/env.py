import os


def get_env_value_or_set_default(var: str, default: str) -> str:
    try:
        return os.environ[var]
    except KeyError:
        return default


class Settings:
    def __init__(self):
        self.db_user = "fastapi"
        self.db_password = "fastapi_password"
        self.db_name = "testreport"

        self.db_address = get_env_value_or_set_default("db_address", "localhost")
        self.db_port = get_env_value_or_set_default("db_port", "5432")
        self.admin_username = get_env_value_or_set_default("admin_username", "user")
        self.admin_password = get_env_value_or_set_default("admin_password", "password")
        # Insert your own key:
        # openssl rand -hex 32
        self.secret_key = get_env_value_or_set_default(
            "secret_key",
            "be51d52dabc8fd8ed4557ff0e834d9c585b9bdda453c4d182b9d7856abebb361",
        )
