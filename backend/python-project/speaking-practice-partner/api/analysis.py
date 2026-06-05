"""数据分析API"""
from fastapi import HTTPException, APIRouter
from typing import List, Dict
from models import TrendAnalysisRequest, WeaknessIdentifyRequest
from services.analysis import TrendAnalyzer, WeaknessIdentifier, VocabSizeEstimator
from loguru import logger

router = APIRouter()

# 初始化服务
trend_analyzer = TrendAnalyzer()
weakness_identifier = WeaknessIdentifier()
vocab_size_estimator = VocabSizeEstimator()


@router.post("/trend", response_model=dict)
async def analyze_trend(request: TrendAnalysisRequest):
    """
    趋势分析
    
    - **scores**: 历史分数列表
    - **dimension**: 维度名称
    - **window**: 移动平均窗口
    """
    try:
        result = trend_analyzer.analyze_trend(
            request.scores,
            request.dimension,
            request.window
        )
        return result
    except Exception as e:
        logger.error(f"趋势分析失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/weakness", response_model=dict)
async def identify_weakness(request: WeaknessIdentifyRequest):
    """
    薄弱点识别
    
    - **error_history**: 错误历史
    - **top_n**: 返回前N个薄弱点
    """
    try:
        result = weakness_identifier.identify_weaknesses(
            request.error_history,
            request.top_n
        )
        return {"weaknesses": result}
    except Exception as e:
        logger.error(f"薄弱点识别失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/vocab-size", response_model=dict)
async def estimate_vocab_size(known_words: List[str], level: str = "intermediate"):
    """
    词汇量估算
    
    - **known_words**: 已知词汇列表
    - **level**: 当前水平
    """
    try:
        result = vocab_size_estimator.estimate_vocab_size(known_words, level)
        return result
    except Exception as e:
        logger.error(f"词汇量估算失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))
