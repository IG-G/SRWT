from datetime import timedelta, datetime
from typing import Optional, Dict

import bcrypt
from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

import env
from src import models
from src.logger import log

ACCESS_TOKEN_EXPIRE_HOURS = 6
ALGORITHM = "HS256"

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")


def authenticate_admin(username: str, password: str) -> bool:
    if username != env.Settings().admin_username:
        return False
    if password != env.Settings().admin_password:
        return False
    return True


def verify_password(password_to_verify: str, password) -> bool:
    return bcrypt.checkpw(password_to_verify.encode("utf-8"), password)


def authenticate_user(username: str, password: str, db: Session) -> bool:
    user = (
        db.query(models.AuthorizationInfo)
        .filter(models.AuthorizationInfo.username == username)
        .first()
    )
    if user is None:
        return False
    log.info(f"Test {user.password}")
    if verify_password(password_to_verify=password, password=user.password):
        return True
    return False


def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, env.Settings().secret_key, algorithm=ALGORITHM)
    return encoded_jwt


def check_auth(token: str = Depends(oauth2_scheme)) -> Dict:
    credentials_exception = HTTPException(
        status_code=401,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, env.Settings().secret_key, algorithms=[ALGORITHM])
        return payload
    except JWTError:
        raise credentials_exception


def add_user(new_username: str, new_password: str, form_data, db: Session) -> str:
    if not authenticate_admin(form_data.username, form_data.password):
        raise HTTPException(
            status_code=401,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    db_user = models.AuthorizationInfo(
        username=new_username,
        password=bcrypt.hashpw(bytes(new_password, "utf-8"), bcrypt.gensalt()),
    )
    try:
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
    except IntegrityError:
        raise HTTPException(status_code=422, detail="User already exists")
    return "Ok"
