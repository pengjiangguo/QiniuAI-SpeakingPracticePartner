"""
语法纠错主类
整合LanguageTool、spaCy和自定义规则进行语法纠错
"""
from typing import Optional
from models import CorrectionResult, GrammarError, ErrorType
from services.nlp.language_tool_checker import LanguageToolChecker
from services.nlp.spacy_analyzer import SpacyAnalyzer
from services.nlp.custom_rules import CustomRules
from loguru import logger


class GrammarCorrector:
    """语法纠错主类"""
    
    def __init__(self):
        """初始化纠错器"""
        try:
            self.lt_checker = LanguageToolChecker()
            self.spacy_analyzer = SpacyAnalyzer()
            self.custom_rules = CustomRules()
            logger.info("GrammarCorrector初始化成功")
        except Exception as e:
            logger.error(f"GrammarCorrector初始化失败: {e}")
            raise
    
    def correct(self, text: str, detailed: bool = True) -> CorrectionResult:
        """
        纠正语法错误
        
        Args:
            text: 待检查的文本
            detailed: 是否返回详细解释
        
        Returns:
            CorrectionResult: 纠错结果
        """
        try:
            logger.info(f"开始语法纠错: {text}")
            
            # 1. LanguageTool检查
            lt_errors = self.lt_checker.check(text)
            logger.info(f"LanguageTool检查完成，发现{len(lt_errors)}个错误")
            
            # 2. spaCy分析
            spacy_issues = self.spacy_analyzer.check_expression_naturalness(text)
            logger.info(f"spaCy分析完成，发现{len(spacy_issues)}个问题")
            
            # 3. 自定义规则检查
            custom_issues = self.custom_rules.check(text)
            logger.info(f"自定义规则检查完成，发现{len(custom_issues)}个问题")
            
            # 4. 合并所有错误
            all_errors = []
            
            # LanguageTool错误
            all_errors.extend(lt_errors)
            
            # spaCy问题
            for issue in spacy_issues:
                error = GrammarError(
                    error_type=ErrorType.EXPRESSION,
                    message=issue['message'],
                    short_message=issue['message'],
                    suggestions=[issue['suggestion']],
                    rule_id='spacy_expression',
                    offset=issue['offset'],
                    length=issue['length']
                )
                all_errors.append(error)
            
            # 自定义规则问题
            for issue in custom_issues:
                error_type = ErrorType.GRAMMAR if issue['type'] == 'grammar' else \
                            ErrorType.TENSE if issue['type'] == 'tense' else \
                            ErrorType.EXPRESSION
                error = GrammarError(
                    error_type=error_type,
                    message=issue['message'],
                    short_message=issue['message'],
                    suggestions=[issue['suggestion']],
                    rule_id='custom_rule',
                    offset=issue['offset'],
                    length=issue['length']
                )
                all_errors.append(error)
            
            # 5. 生成修正后的句子
            corrected_sentence = self.lt_checker.correct(text) if all_errors else text
            
            # 6. 生成解释和建议
            explanation = None
            suggestion = None
            evaluation = None
            
            if detailed and all_errors:
                explanation = self._generate_explanation(all_errors)
                suggestion = self._generate_suggestion(all_errors)
            elif not all_errors:
                evaluation = self._generate_evaluation(text)
            
            # 7. 返回结果
            result = CorrectionResult(
                has_error=len(all_errors) > 0,
                original_sentence=text,
                corrected_sentence=corrected_sentence if corrected_sentence != text else None,
                errors=all_errors,
                explanation=explanation,
                suggestion=suggestion,
                evaluation=evaluation
            )
            
            logger.info(f"语法纠错完成，共发现{len(all_errors)}个错误")
            return result
        
        except Exception as e:
            logger.error(f"语法纠错失败: {e}")
            # 返回空结果
            return CorrectionResult(
                has_error=False,
                original_sentence=text,
                errors=[]
            )
    
    def _generate_explanation(self, errors: list) -> str:
        """
        生成错误解释
        
        Args:
            errors: 错误列表
        
        Returns:
            str: 错误解释
        """
        explanations = []
        for error in errors:
            explanations.append(f"- {error.message}")
        return "\n".join(explanations)
    
    def _generate_suggestion(self, errors: list) -> str:
        """
        生成改进建议
        
        Args:
            errors: 错误列表
        
        Returns:
            str: 改进建议
        """
        suggestions = []
        for error in errors:
            if error.suggestions:
                suggestions.append(f"- {error.suggestions[0]}")
        return "\n".join(suggestions)
    
    def _generate_evaluation(self, text: str) -> str:
        """
        生成正面评价
        
        Args:
            text: 文本
        
        Returns:
            str: 正面评价
        """
        # 根据句子长度生成不同的评价
        word_count = len(text.split())
        
        if word_count <= 3:
            return "句子简短但语法正确！"
        elif word_count <= 6:
            return "句子语法正确，表达自然流畅！"
        else:
            return "句子结构完整，语法正确，表达清晰！"
