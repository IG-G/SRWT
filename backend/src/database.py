from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from env import Settings

env = Settings()
SQLALCHEMY_DATABASE_URL = f"postgresql://{env.db_user}:{env.db_password}@{env.db_address}:{env.db_port}/{env.db_name}"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
