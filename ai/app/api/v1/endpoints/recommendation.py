from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

router = APIRouter()


class RecommendationRequest(BaseModel):
    destination: str | None = None
    preferences: list[str] = []
    budget: str | None = None
    duration_days: int | None = None


class RecommendationResponse(BaseModel):
    recommendations: list[dict]
    message: str


@router.post("/", response_model=RecommendationResponse)
async def get_recommendations(request: RecommendationRequest):
    """
    AI 기반 여행 추천을 생성합니다.
    """
    # TODO: AI 모델 연동
    return RecommendationResponse(
        recommendations=[
            {
                "place": "Sample Destination",
                "description": "A wonderful place to visit",
                "rating": 4.5,
            }
        ],
        message="Recommendations generated successfully",
    )


@router.get("/popular")
async def get_popular_destinations():
    """
    인기 여행지 목록을 반환합니다.
    """
    return {
        "destinations": [
            {"name": "Seoul", "country": "South Korea"},
            {"name": "Tokyo", "country": "Japan"},
            {"name": "Paris", "country": "France"},
        ]
    }
