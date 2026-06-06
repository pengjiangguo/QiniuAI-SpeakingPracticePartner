"""语法检查API"""
from fastapi import HTTPException, APIRouter
from models import GrammarCheckRequest, GrammarCheckResponse, ChinglishDetectRequest, ChinglishDetectResponse, CorrectionRequest, CorrectionResult
from services.nlp import GrammarChecker, ChinglishDetector
from services.nlp.grammar_corrector import GrammarCorrector
from loguru import logger

router = APIRouter()

# 初始化服务
grammar_checker = GrammarChecker()
chinglish_detector = ChinglishDetector()
grammar_corrector = GrammarCorrector()


@router.post("/check", response_model=GrammarCheckResponse)
async def check_grammar(request: GrammarCheckRequest):
    """
    语法检查
    
    - **text**: 待检查文本
    """
    try:
        errors = grammar_checker.check_grammar(request.text)
        return GrammarCheckResponse(errors=errors)
    except Exception as e:
        logger.error(f"语法检查失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/detect-chinglish", response_model=ChinglishDetectResponse)
async def detect_chinglish(request: ChinglishDetectRequest):
    """
    中式英语检测
    
    - **text**: 待检测文本
    """
    try:
        results = chinglish_detector.detect_chinglish(request.text)
        return ChinglishDetectResponse(results=results)
    except Exception as e:
        logger.error(f"中式英语检测失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/correct", response_model=CorrectionResult)
async def correct_grammar(request: CorrectionRequest):
    """
    语法纠错（新增）
    
    使用LanguageTool + spaCy + 自定义规则进行语法纠错
    
    - **text**: 待纠错文本
    - **detailed**: 是否返回详细解释（默认True）
    """
    try:
        result = grammar_corrector.correct(request.text, request.detailed)
        return result
    except Exception as e:
        logger.error(f"语法纠错失败: {e}")
        raise HTTPException(status_code=500, detail=str(e))
