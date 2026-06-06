"""
数据模型
"""
from pydantic import BaseModel
from typing import List, Dict, Optional
from datetime import datetime
from enum import Enum


# ==================== 发音评测相关 ====================

class PronunciationScoreRequest(BaseModel):
    """发音评分请求"""
    audio_path: str
    reference_text: str


class PronunciationScoreResponse(BaseModel):
    """发音评分响应"""
    overall_score: float
    accuracy_score: float
    fluency_score: float
    completeness_score: float
    pronunciation_score: float
    reference_text: str
    audio_duration: float
    phoneme_details: Optional[List[Dict]] = None


class PhonemeScoreRequest(BaseModel):
    """音素评分请求"""
    audio_path: str
    target_phoneme: Optional[str] = None
    reference_text: Optional[str] = None


class PhonemeScoreResponse(BaseModel):
    """音素评分响应"""
    overall_score: float
    phoneme_scores: Dict[str, float]
    fluency_score: float
    completeness_score: float
    audio_duration: float


# ==================== 语法检查相关 ====================

class GrammarCheckRequest(BaseModel):
    """语法检查请求"""
    text: str


class GrammarCheckResponse(BaseModel):
    """语法检查响应"""
    errors: List[Dict]


class ChinglishDetectRequest(BaseModel):
    """中式英语检测请求"""
    text: str


class ChinglishDetectResponse(BaseModel):
    """中式英语检测响应"""
    results: List[Dict]


# ==================== 语法纠错相关（新增） ====================

class ErrorType(str, Enum):
    """错误类型枚举"""
    GRAMMAR = "语法错误"
    EXPRESSION = "表达不自然"
    WORD_CHOICE = "用词不当"
    TENSE = "时态错误"
    SPELLING = "拼写错误"
    OTHER = "其他"


class GrammarError(BaseModel):
    """语法错误详情"""
    error_type: ErrorType
    message: str
    short_message: str
    suggestions: List[str]
    rule_id: str
    offset: int
    length: int


class CorrectionRequest(BaseModel):
    """语法纠错请求"""
    text: str
    detailed: bool = True


class CorrectionResult(BaseModel):
    """语法纠错结果"""
    has_error: bool
    original_sentence: str
    corrected_sentence: Optional[str] = None
    errors: List[GrammarError] = []
    explanation: Optional[str] = None
    suggestion: Optional[str] = None
    evaluation: Optional[str] = None


# ==================== 数据分析相关 ====================

class TrendAnalysisRequest(BaseModel):
    """趋势分析请求"""
    scores: List[Dict]
    dimension: str
    window: int = 7


class TrendAnalysisResponse(BaseModel):
    """趋势分析响应"""
    dimension: str
    trend: str
    slope: float
    r_squared: float
    current_score: float
    trend_points: List[float]
    dates: List[str]


class WeaknessIdentifyRequest(BaseModel):
    """薄弱点识别请求"""
    error_history: List[Dict]
    top_n: int = 5


class WeaknessIdentifyResponse(BaseModel):
    """薄弱点识别响应"""
    weaknesses: List[Dict]


# ==================== 推荐相关 ====================

class SceneRecommendRequest(BaseModel):
    """场景推荐请求"""
    user_profile: Dict
    scene_list: List[Dict]
    top_n: int = 5


class SceneRecommendResponse(BaseModel):
    """场景推荐响应"""
    recommendations: List[Dict]


class ReviewRecommendRequest(BaseModel):
    """复习推荐请求"""
    learning_records: List[Dict]


class ReviewRecommendResponse(BaseModel):
    """复习推荐响应"""
    recommendations: List[Dict]


# ==================== 音频处理相关 ====================

class AudioProcessRequest(BaseModel):
    """音频处理请求"""
    audio_path: str
    operation: str  # "denoise", "normalize", "resample"


class AudioProcessResponse(BaseModel):
    """音频处理响应"""
    processed_path: str
    original_duration: float
    processed_duration: float
