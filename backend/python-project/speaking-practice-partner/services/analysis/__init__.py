"""
数据分析引擎
"""
from typing import Dict, List
import numpy as np
import pandas as pd
from scipy import stats
from loguru import logger


class TrendAnalyzer:
    """趋势分析器"""
    
    def __init__(self):
        logger.info("初始化TrendAnalyzer")
    
    def analyze_trend(
        self, 
        scores: List[Dict], 
        dimension: str, 
        window: int = 7
    ) -> Dict:
        """
        能力趋势分析
        
        Args:
            scores: 分数列表 [{"date": "2024-01-01", "score": 85}, ...]
            dimension: 维度名称
            window: 移动平均窗口
            
        Returns:
            趋势分析结果
        """
        try:
            if not scores:
                return {"trend": "unknown", "message": "无数据"}
            
            # 转换为DataFrame
            df = pd.DataFrame(scores)
            df['date'] = pd.to_datetime(df['date'])
            df = df.set_index('date').sort_index()
            
            # 移动平均平滑
            df['smoothed'] = df['score'].rolling(window=window, min_periods=1).mean()
            
            # 线性回归趋势
            x = np.arange(len(df))
            slope, intercept, r_value, p_value, std_err = stats.linregress(x, df['smoothed'].values)
            
            # 趋势判断
            if slope > 0.1:
                trend = "improving"
            elif slope < -0.1:
                trend = "declining"
            else:
                trend = "stable"
            
            return {
                "dimension": dimension,
                "trend": trend,
                "slope": float(slope),
                "r_squared": float(r_value ** 2),
                "current_score": float(df['score'].iloc[-1]),
                "trend_points": df['smoothed'].tolist(),
                "dates": [d.strftime("%Y-%m-%d") for d in df.index]
            }
        except Exception as e:
            logger.error(f"趋势分析失败: {e}")
            raise


class WeaknessIdentifier:
    """薄弱点识别器"""
    
    def __init__(self):
        logger.info("初始化WeaknessIdentifier")
    
    def identify_weaknesses(
        self, 
        error_history: List[Dict], 
        top_n: int = 5
    ) -> List[Dict]:
        """
        识别薄弱点
        
        Args:
            error_history: 错误历史 [{"error_type": "grammar", "count": 10}, ...]
            top_n: 返回前N个薄弱点
            
        Returns:
            薄弱点列表
        """
        try:
            if not error_history:
                return []
            
            # 统计错误类型
            error_counts = {}
            for error in error_history:
                error_type = error.get("error_type", "unknown")
                error_counts[error_type] = error_counts.get(error_type, 0) + error.get("count", 1)
            
            # 排序
            sorted_errors = sorted(error_counts.items(), key=lambda x: x[1], reverse=True)
            
            # 返回TOP N
            weaknesses = []
            for error_type, count in sorted_errors[:top_n]:
                weaknesses.append({
                    "error_type": error_type,
                    "count": count,
                    "severity": "high" if count > 10 else "medium" if count > 5 else "low"
                })
            
            return weaknesses
        except Exception as e:
            logger.error(f"薄弱点识别失败: {e}")
            raise


class VocabSizeEstimator:
    """词汇量估算器"""
    
    def __init__(self):
        logger.info("初始化VocabSizeEstimator")
    
    def estimate_vocab_size(
        self, 
        known_words: List[str], 
        level: str = "intermediate"
    ) -> Dict:
        """
        估算词汇量
        
        Args:
            known_words: 已知词汇列表
            level: 当前水平
            
        Returns:
            词汇量估算结果
        """
        try:
            # 基于Zipf定律估算
            estimated_size = len(known_words) * 2.5  # 简化示例
            
            # 映射到CEFR等级
            if estimated_size < 500:
                cefr_level = "A1"
            elif estimated_size < 1000:
                cefr_level = "A2"
            elif estimated_size < 2000:
                cefr_level = "B1"
            elif estimated_size < 4000:
                cefr_level = "B2"
            elif estimated_size < 8000:
                cefr_level = "C1"
            else:
                cefr_level = "C2"
            
            return {
                "estimated_size": int(estimated_size),
                "known_words_count": len(known_words),
                "cefr_level": cefr_level,
                "confidence": 0.7  # 置信度
            }
        except Exception as e:
            logger.error(f"词汇量估算失败: {e}")
            raise
