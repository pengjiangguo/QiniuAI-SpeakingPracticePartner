"""
自定义语法规则
针对口语练习场景的自定义语法检查规则
"""
from typing import List, Dict
import re
from loguru import logger


class CustomRules:
    """自定义语法规则（针对口语练习场景）"""
    
    def __init__(self):
        """初始化自定义规则"""
        self.rules = [
            self._check_very_with_verb,
            self._check_double_subject,
            self._check_wrong_tense_marker,
            self._check_article_usage,
            self._check_subject_verb_agreement,
            self._check_wrong_word_order,
            self._check_missing_article
        ]
        logger.info(f"自定义规则初始化完成，共{len(self.rules)}条规则")
    
    def check(self, text: str) -> List[Dict]:
        """
        应用所有自定义规则
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        for rule in self.rules:
            try:
                result = rule(text)
                if result:
                    issues.extend(result)
            except Exception as e:
                logger.error(f"规则执行失败: {e}")
        
        logger.info(f"自定义规则检查完成，发现{len(issues)}个问题")
        return issues
    
    def _check_very_with_verb(self, text: str) -> List[Dict]:
        """
        检查"very"修饰动词
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "very + verb" 模式
        pattern = r'\bvery\s+(\w+ing|\w+ed)\b'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        for match in matches:
            issues.append({
                'type': 'expression',
                'message': f'"very" 通常不直接修饰动词形式 "{match.group(1)}"',
                'suggestion': f'建议使用 "really {match.group(1)}" 或其他表达',
                'offset': match.start(),
                'length': len(match.group(0))
            })
        
        return issues
    
    def _check_double_subject(self, text: str) -> List[Dict]:
        """
        检查双重主语
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "The/A/An + noun + pronoun" 模式
        # 例如："The boy he is..."
        pattern = r'\b(the|a|an)\s+(\w+)\s+(he|she|it|they)\s+'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        for match in matches:
            issues.append({
                'type': 'grammar',
                'message': f'双重主语错误',
                'suggestion': f'移除多余的代词 "{match.group(3)}"',
                'offset': match.start(),
                'length': len(match.group(0))
            })
        
        return issues
    
    def _check_wrong_tense_marker(self, text: str) -> List[Dict]:
        """
        检查时态标记错误
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "yesterday/tomorrow" 等时态标记
        # 例如："I am go to school yesterday"
        if 'yesterday' in text.lower() or 'last' in text.lower():
            # 检查是否有现在时态标记
            pattern = r'\b(am|is|are)\s+(\w+)\b'
            matches = re.finditer(pattern, text, re.IGNORECASE)
            
            for match in matches:
                verb = match.group(2)
                # 简单检查：如果是动词原形，可能是错误
                if verb in ['go', 'come', 'eat', 'drink', 'play', 'study', 'work']:
                    issues.append({
                        'type': 'tense',
                        'message': f'时态错误：过去发生的事情应该用过去时态',
                        'suggestion': f'建议使用过去时态形式',
                        'offset': match.start(),
                        'length': len(match.group(0))
                    })
        
        return issues
    
    def _check_article_usage(self, text: str) -> List[Dict]:
        """
        检查冠词使用
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "a" + 元音开头的词
        pattern = r'\ba\s+([aeiou]\w+)'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        for match in matches:
            issues.append({
                'type': 'grammar',
                'message': f'冠词错误："a {match.group(1)}"',
                'suggestion': f'应该使用 "an {match.group(1)}"',
                'offset': match.start(),
                'length': len(match.group(0))
            })
        
        # 检查 "an" + 辅音开头的词
        pattern = r'\ban\s+([bcdfghjklmnpqrstvwxyz]\w+)'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        for match in matches:
            issues.append({
                'type': 'grammar',
                'message': f'冠词错误："an {match.group(1)}"',
                'suggestion': f'应该使用 "a {match.group(1)}"',
                'offset': match.start(),
                'length': len(match.group(0))
            })
        
        return issues
    
    def _check_subject_verb_agreement(self, text: str) -> List[Dict]:
        """
        检查主谓一致
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "He/She/It + verb" 错误（应该用第三人称单数）
        # 例如："He go to school" 应该是 "He goes to school"
        pattern = r'\b(he|she|it)\s+(\w+)\b'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        common_verbs = ['go', 'come', 'do', 'have', 'say', 'get', 'make', 'know', 'think', 'take']
        
        for match in matches:
            verb = match.group(2).lower()
            if verb in common_verbs:
                issues.append({
                    'type': 'grammar',
                    'message': f'主谓不一致："{match.group(1)} {verb}"',
                    'suggestion': f'应该使用第三人称单数形式',
                    'offset': match.start(),
                    'length': len(match.group(0))
                })
        
        return issues
    
    def _check_wrong_word_order(self, text: str) -> List[Dict]:
        """
        检查词序错误
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查形容词位置错误
        # 例如："I have a book red" 应该是 "I have a red book"
        pattern = r'\b(a|an|the)\s+(\w+)\s+(\w+)\b'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        # 这里需要更复杂的逻辑来判断词性
        # 简化示例
        
        return issues
    
    def _check_missing_article(self, text: str) -> List[Dict]:
        """
        检查缺失冠词
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        issues = []
        
        # 检查 "I have + noun" 缺失冠词
        # 例如："I have book" 应该是 "I have a book"
        pattern = r'\b(have|has)\s+(\w+)\b'
        matches = re.finditer(pattern, text, re.IGNORECASE)
        
        common_nouns = ['book', 'pen', 'car', 'house', 'dog', 'cat', 'friend', 'idea']
        
        for match in matches:
            noun = match.group(2).lower()
            if noun in common_nouns:
                issues.append({
                    'type': 'grammar',
                    'message': f'可能缺失冠词："{match.group(1)} {noun}"',
                    'suggestion': f'建议使用 "{match.group(1)} a/an {noun}"',
                    'offset': match.start(),
                    'length': len(match.group(0))
                })
        
        return issues
