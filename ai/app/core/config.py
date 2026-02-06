from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    PROJECT_NAME: str = "AI Trip AI Service"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"

    # CORS
    ALLOWED_ORIGINS: list[str] = [
        "http://localhost:3000",
        "http://localhost:8080",
    ]

    # Backend API
    BACKEND_API_URL: str = "http://localhost:8080"

    # AI Model settings
    MODEL_NAME: str = "gpt-4"
    OPENAI_API_KEY: str = ""

    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
