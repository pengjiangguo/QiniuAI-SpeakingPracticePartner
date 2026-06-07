"""
LanguageTool语法检查器
使用LanguageTool进行专业的语法检查
需要手动启动LanguageTool服务器: java -jar languagetool-server.jar --port 8081 --allow-origin "*"
"""
from typing import List, Optional
from models import GrammarError, ErrorType
from config import settings
from loguru import logger
import requests
import time


class LanguageToolChecker:
    """LanguageTool语法检查器"""
    
    def __init__(self):
        """初始化LanguageTool"""
        try:
            # 验证服务器是否可访问
            self._verify_server()
            
            logger.info("LanguageTool初始化成功，已连接到服务器 http://localhost:8081")
        except Exception as e:
            logger.error(f"LanguageTool初始化失败: {e}")
            logger.error("请确保已手动启动LanguageTool服务器: java -jar languagetool-server.jar --port 8081 --allow-origin \"*\"")
            raise
    
    def _verify_server(self, max_retries: int = 3, retry_delay: int = 2):
        """
        验证LanguageTool服务器是否可访问
        
        Args:
            max_retries: 最大重试次数
            retry_delay: 重试间隔（秒）
        """
        url = "http://localhost:8081/v2/languages"
        
        for i in range(max_retries):
            try:
                response = requests.get(url, timeout=5)
                if response.status_code == 200:
                    logger.info("LanguageTool服务器验证成功")
                    return
            except requests.exceptions.RequestException as e:
                logger.warning(f"验证服务器失败 (尝试 {i+1}/{max_retries}): {e}")
                if i < max_retries - 1:
                    time.sleep(retry_delay)
        
        raise RuntimeError("无法连接到LanguageTool服务器，请确保服务器已启动: java -jar languagetool-server.jar --port 8081 --allow-origin \"*\"")
    
    def check(self, text: str, language: str = 'en-US') -> List[GrammarError]:
        """
        检查语法错误
        
        Args:
            text: 待检查的文本
            language: 语言代码，默认为 en-US
        
        Returns:
            List[GrammarError]: 错误列表
        """
        try:
            # 调用LanguageTool HTTP API
            url = "http://localhost:8081/v2/check"
            params = {
                'text': text,
                'language': language,
                'enabledOnly': 'false'
            }
            
            response = requests.post(url, data=params, timeout=30)
            response.raise_for_status()
            
            result = response.json()
            matches = result.get('matches', [])
            
            errors = []
            for match in matches:
                # 映射错误类型
                rule_id = match.get('rule', {}).get('id', '')
                error_type = self._map_error_type(rule_id)
                
                # 提取建议列表
                replacements = match.get('replacements', [])
                suggestions = [r.get('value', '') for r in replacements if isinstance(r, dict) and r.get('value')]
                
                error = GrammarError(
                    error_type=error_type,
                    message=match.get('message', ''),
                    short_message=match.get('shortMessage', ''),
                    suggestions=suggestions[:3],  # 最多3个建议
                    rule_id=rule_id,
                    offset=match.get('offset', 0),
                    length=match.get('length', 0)
                )
                errors.append(error)
            
            logger.info(f"LanguageTool检查完成，发现{len(errors)}个错误")
            return errors
        
        except requests.exceptions.RequestException as e:
            logger.error(f"LanguageTool API请求失败: {e}")
            return []
        except Exception as e:
            logger.error(f"LanguageTool检查失败: {e}")
            return []
    
    def correct(self, text: str, language: str = 'en-US') -> str:
        """
        自动纠正文本
        
        Args:
            text: 待纠正的文本
            language: 语言代码，默认为 en-US
        
        Returns:
            str: 纠正后的文本
        """
        try:
            # 获取错误列表
            errors = self.check(text, language)
            
            # 按偏移量倒序排序，从后往前替换
            errors_sorted = sorted(errors, key=lambda e: e.offset, reverse=True)
            
            corrected_text = text
            for error in errors_sorted:
                if error.suggestions:
                    # 使用第一个建议替换
                    suggestion = error.suggestions[0]
                    corrected_text = (
                        corrected_text[:error.offset] + 
                        suggestion + 
                        corrected_text[error.offset + error.length:]
                    )
            
            if corrected_text != text:
                logger.info(f"LanguageTool纠正完成: {text} -> {corrected_text}")
            return corrected_text
        except Exception as e:
            logger.error(f"LanguageTool纠正失败: {e}")
            return text
    
    def _map_error_type(self, rule_id: str) -> ErrorType:
        """
        映射LanguageTool规则ID到错误类型
        
        Args:
            rule_id: LanguageTool规则ID
        
        Returns:
            ErrorType: 错误类型
        """
        # 语法错误
        if any(keyword in rule_id for keyword in ['GRAMMAR', 'MORFOLOGIK', 'VERB', 'AGREEMENT']):
            return ErrorType.GRAMMAR
        # 拼写错误
        elif any(keyword in rule_id for keyword in ['SPELL', 'TYPOS', 'MISSPELLING']):
            return ErrorType.SPELLING
        # 时态错误
        elif 'TENSE' in rule_id:
            return ErrorType.TENSE
        # 用词不当
        elif any(keyword in rule_id for keyword in ['WORD', 'CONFUSION']):
            return ErrorType.WORD_CHOICE
        # 其他
        else:
            return ErrorType.OTHER
