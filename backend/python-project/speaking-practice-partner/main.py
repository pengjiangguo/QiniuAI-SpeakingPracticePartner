"""
Speaking Practice Partner - AI Service
FastAPI主入口
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from loguru import logger
from config import settings
from api import pronunciation, grammar, analysis, recommendation, audio

# 配置日志
logger.add("logs/app.log", rotation="10 MB", retention="7 days", level="INFO")

# 创建FastAPI应用
app = FastAPI(
    title=settings.app_name,
    description="AI口语陪练系统AI服务 - 发音评测、语法分析、数据统计、推荐算法",
    version=settings.app_version,
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
app.include_router(pronunciation.router, prefix="/api/v1/pronunciation", tags=["Pronunciation"])
app.include_router(grammar.router, prefix="/api/v1/grammar", tags=["Grammar"])
app.include_router(analysis.router, prefix="/api/v1/analysis", tags=["Analysis"])
app.include_router(recommendation.router, prefix="/api/v1/recommendation", tags=["Recommendation"])
app.include_router(audio.router, prefix="/api/v1/audio", tags=["Audio"])


@app.on_event("startup")
async def startup_event():
    """应用启动事件"""
    logger.info(f"{settings.app_name} v{settings.app_version} 启动中...")
    logger.info(f"服务地址: http://{settings.host}:{settings.port}")
    logger.info(f"API文档: http://{settings.host}:{settings.port}/docs")


@app.on_event("shutdown")
async def shutdown_event():
    """应用关闭事件"""
    logger.info("应用关闭")


@app.get("/health")
async def health_check():
    """健康检查"""
    return {
        "status": "healthy",
        "service": settings.app_name,
        "version": settings.app_version
    }


@app.get("/")
async def root():
    """根路径"""
    return {
        "service": settings.app_name,
        "version": settings.app_version,
        "docs": "/docs",
        "redoc": "/redoc",
        "health": "/health"
    }
