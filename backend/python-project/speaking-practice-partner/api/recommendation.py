"""推荐算法API"""
from fastapi import APIRouter, HTTPException
from typing import List, Dict
from models import SceneRecommendRequest, ReviewRecommendRequest
from services.recommendation import SceneRecommender, ReviewRecommender, VocabRecommender
from loguru import logger

router = APIRouter()

# 初始化服务
scene_recommender = SceneRecommender()
review_recommender = ReviewRecommender()
vocab_recommender = VocabRecommender()


@router.post("/scenes", response_model=dict)
async def recommend_scenes(request: SceneRecommendRequest):
    """
    场景推荐
    
    - **user_profile**: 用户画像
    - **scene_list**: 场景列表
    - **top_n**: 返回前N个推荐
    """
    try:
        result = scene_recommender.recommend_scenes(
            request.user_profile,
            request.scene_list,
            request.top_n
        )
        return {"recommendations": result}
    except Exception as e:
        logger.error(f"场景推荐失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/review", response_model=dict)
async def recommend_review(request: ReviewRecommendRequest):
    """
    复习内容推荐
    
    - **learning_records**: 学习记录
    """
    try:
        result = review_recommender.recommend_review(request.learning_records)
        return {"recommendations": result}
    except Exception as e:
        logger.error(f"复习推荐失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/vocabulary", response_model=dict)
async def recommend_vocabulary(
    user_level: str,
    known_words: List[str],
    scene_context: str = None,
    top_n: int = 10
):
    """
    词汇推荐
    
    - **user_level**: 用户水平
    - **known_words**: 已知词汇
    - **scene_context**: 场景上下文
    - **top_n**: 返回前N个推荐
    """
    try:
        result = vocab_recommender.recommend_vocabulary(
            user_level,
            known_words,
            scene_context,
            top_n
        )
        return {"recommendations": result}
    except Exception as e:
        logger.error(f"词汇推荐失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))
