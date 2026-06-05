"""
推荐算法引擎
"""
from typing import Dict, List
import numpy as np
from datetime import datetime, timedelta
from loguru import logger


class SceneRecommender:
    """场景推荐器"""
    
    def __init__(self):
        logger.info("初始化SceneRecommender")
    
    def recommend_scenes(
        self, 
        user_profile: Dict, 
        scene_list: List[Dict], 
        top_n: int = 5
    ) -> List[Dict]:
        """
        场景推荐
        
        Args:
            user_profile: 用户画像
            scene_list: 场景列表
            top_n: 返回前N个推荐
            
        Returns:
            推荐场景列表
        """
        try:
            if not scene_list:
                return []
            
            # TODO: 实现实际的推荐算法
            # 这里使用简单的基于难度的推荐
            
            user_level = user_profile.get("english_level", "B1")
            interests = user_profile.get("interests", [])
            
            recommendations = []
            for scene in scene_list:
                score = 0.5  # 基础分数
                
                # 基于难度匹配
                difficulty = scene.get("difficulty", "INTERMEDIATE")
                if difficulty == "BEGINNER" and user_level in ["A1", "A2"]:
                    score += 0.3
                elif difficulty == "INTERMEDIATE" and user_level in ["B1", "B2"]:
                    score += 0.3
                elif difficulty == "ADVANCED" and user_level in ["C1", "C2"]:
                    score += 0.3
                
                # 基于兴趣匹配
                scene_tags = scene.get("tags", [])
                if any(tag in interests for tag in scene_tags):
                    score += 0.2
                
                recommendations.append({
                    "scene_id": scene.get("id"),
                    "scene_name": scene.get("name"),
                    "score": float(score),
                    "reason": "基于难度和兴趣匹配"
                })
            
            # 排序并返回TOP N
            recommendations.sort(key=lambda x: x["score"], reverse=True)
            return recommendations[:top_n]
        except Exception as e:
            logger.error(f"场景推荐失败: {e}")
            raise


class ReviewRecommender:
    """复习推荐器"""
    
    def __init__(self):
        logger.info("初始化ReviewRecommender")
    
    def recommend_review(
        self, 
        learning_records: List[Dict]
    ) -> List[Dict]:
        """
        基于艾宾浩斯遗忘曲线的复习推荐
        
        Args:
            learning_records: 学习记录 [{"item_id": "xxx", "learned_at": "2024-01-01", "review_count": 2}, ...]
            
        Returns:
            复习推荐列表
        """
        try:
            if not learning_records:
                return []
            
            recommendations = []
            forgetting_rate = 0.3
            
            for record in learning_records:
                learned_at = datetime.fromisoformat(record["learned_at"])
                days_since_learning = (datetime.now() - learned_at).days
                review_count = record.get("review_count", 0)
                
                # 计算记忆保持率
                retention_rate = np.exp(-forgetting_rate * days_since_learning / (review_count + 1))
                
                # 计算优先级 (保持率越低优先级越高)
                priority = 1 - retention_rate
                
                recommendations.append({
                    "item_id": record.get("item_id"),
                    "priority": float(priority),
                    "retention_rate": float(retention_rate),
                    "days_since_learning": days_since_learning,
                    "review_count": review_count,
                    "recommended": retention_rate < 0.7  # 保持率低于70%建议复习
                })
            
            # 按优先级排序
            recommendations.sort(key=lambda x: x["priority"], reverse=True)
            return recommendations
        except Exception as e:
            logger.error(f"复习推荐失败: {e}")
            raise


class VocabRecommender:
    """词汇推荐器"""
    
    def __init__(self):
        logger.info("初始化VocabRecommender")
    
    def recommend_vocabulary(
        self,
        user_level: str,
        known_words: List[str],
        scene_context: str = None,
        top_n: int = 10
    ) -> List[Dict]:
        """
        词汇推荐
        
        Args:
            user_level: 用户水平
            known_words: 已知词汇
            scene_context: 场景上下文
            top_n: 返回前N个推荐
            
        Returns:
            推荐词汇列表
        """
        try:
            # TODO: 实现实际的词汇推荐逻辑
            # 这里返回模拟数据
            
            recommendations = [
                {"word": "accommodation", "difficulty": "B1", "frequency": 0.8, "reason": "常用词汇"},
                {"word": "enthusiastic", "difficulty": "B2", "frequency": 0.6, "reason": "表达升级"},
                {"word": "nevertheless", "difficulty": "C1", "frequency": 0.5, "reason": "连接词"},
            ]
            
            return recommendations[:top_n]
        except Exception as e:
            logger.error(f"词汇推荐失败: {e}")
            raise
