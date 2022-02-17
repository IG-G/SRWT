from typing import List

from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy.orm import Session

from . import crud, models, schemas
from .database import SessionLocal, engine

models.Base.metadata.create_all(bind=engine)

app = FastAPI()


# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.post("/campaigns/", response_model=schemas.TestCampaign)
def create_test_campaign(
    campaign: schemas.TestCampaignCreate, db: Session = Depends(get_db)
):
    return crud.create_test_campaign(db=db, campaign=campaign)


@app.get("/campaigns/", response_model=List[schemas.TestCampaign])
def get_test_campaigns(limit: int = 100, db: Session = Depends(get_db)):
    campaign = crud.get_test_campaigns(db, limit=limit)
    return campaign


@app.get("/campaigns/{campaign_id}", response_model=schemas.TestCampaign)
def get_test_campaign(campaign_id: int, db: Session = Depends(get_db)):
    campaign = crud.get_test_campaign_by_id(db, campaign_id=campaign_id)
    if campaign is None:
        raise HTTPException(status_code=404, detail="Campaign not found")
    return campaign


@app.put("/campaigns/{campaign_id}", response_model=schemas.Empty)
def end_test_campaign(campaign_id: int, campaign: schemas.TestCampaignEnd, db: Session = Depends(get_db)):
    crud.end_test_campaign(db, campaign_id=campaign_id, campaign=campaign)
    t = schemas.Empty()
    return t
