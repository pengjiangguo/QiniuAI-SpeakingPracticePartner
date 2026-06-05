"""
配置文件
"""
from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    """应用配置"""
    
    # 应用基础配置
    app_name: str = "Speaking Practice Partner - AI Service"
    app_version: str = "1.0.0"
    debug: bool = True
    
    # 服务配置
    host: str = "0.0.0.0"
    port: int = 8000
    
    # Java服务配置
    java_service_url: str = "http://localhost:8080/api"
    
    # 腾讯云配置
    tencent_secret_id: Optional[str] = None
    tencent_secret_key: Optional[str] = None
    tencent_asr_app_id: Optional[str] = None
    
    # DeepSeek配置
    deepseek_api_key: Optional[str] = None
    deepseek_base_url: str = "https://api.deepseek.com/v1"
    
    # 模型配置
    spacy_model: str = "en_core_web_sm"
    grammar_model: str = "vennify/t5-base-grammar-correction"
    
    # Redis配置
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_db: int = 1
    redis_password: Optional[str] = None
    
    # 文件存储配置
    audio_storage_path: str = "./storage/audio"
    model_cache_path: str = "./storage/models"
    
    class Config:
        env_file = ".env"
        case_sensitive = False


# 创建全局配置实例
settings = Settings()
