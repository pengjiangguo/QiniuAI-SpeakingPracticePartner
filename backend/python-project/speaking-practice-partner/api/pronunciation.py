"""发音评测API"""
from fastapi import UploadFile, File, HTTPException, APIRouter
from models import PronunciationScoreRequest, PronunciationScoreResponse, PhonemeScoreRequest, PhonemeScoreResponse
from services.pronunciation import PronunciationScorer, PhonemeScorer
from loguru import logger

router = APIRouter()

# 初始化服务
pronunciation_scorer = PronunciationScorer()
phoneme_scorer = PhonemeScorer()


@router.post("/score", response_model=dict)
async def score_pronunciation(request: PronunciationScoreRequest):
    """
    发音评分
    
    - **audio_path**: 音频文件路径
    - **reference_text**: 参考文本
    """
    try:
        result = pronunciation_scorer.score_pronunciation(
            request.audio_path,
            request.reference_text
        )
        return result
    except Exception as e:
        logger.error(f"发音评分失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/score-phoneme", response_model=dict)
async def score_phoneme(request: PhonemeScoreRequest):
    """
    音素评分
    
    - **audio_path**: 音频文件路径
    - **target_phoneme**: 目标音素
    - **reference_text**: 参考文本
    """
    try:
        result = phoneme_scorer.score_phoneme(
            request.audio_path,
            request.reference_text,
            request.target_phoneme
        )
        return result
    except Exception as e:
        logger.error(f"音素评分失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/score-audio-file")
async def score_audio_file(
    audio: UploadFile = File(...),
    reference_text: str = None
):
    """
    音频文件评分
    
    - **audio**: 音频文件
    - **reference_text**: 参考文本
    """
    try:
        # 保存上传的文件
        audio_path = f"./storage/audio/{audio.filename}"
        with open(audio_path, "wb") as f:
            f.write(await audio.read())
        
        # 评分
        result = pronunciation_scorer.score_pronunciation(
            audio_path,
            reference_text or ""
        )
        return result
    except Exception as e:
        logger.error(f"音频文件评分失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))
