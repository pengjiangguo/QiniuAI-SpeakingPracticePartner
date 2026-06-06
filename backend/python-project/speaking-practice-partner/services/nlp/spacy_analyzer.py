"""
spaCy NLP分析器
使用spaCy进行自然语言处理和表达自然度检查
"""
import spacy
from typing import Dict, List, Any
from loguru import logger


class SpacyAnalyzer:
    """spaCy NLP分析器"""
    
    def __init__(self):
        """初始化spaCy"""
        self.nlp = None
        self.model_loaded = False
        try:
            # 尝试加载英语模型
            self.nlp = spacy.load('en_core_web_sm')
            self.model_loaded = True
            logger.info("spaCy初始化成功，加载模型: en_core_web_sm")
        except OSError:
            # 如果模型不存在，使用空模型
            logger.warning("spaCy模型不存在，将使用基础功能。请运行: python -m spacy download en_core_web_sm")
            try:
                # 创建一个空模型，只支持基本分词
                self.nlp = spacy.blank('en')
                logger.info("使用spaCy空模型（仅支持基本分词）")
            except Exception as e:
                logger.error(f"spaCy空模型创建失败: {e}")
                self.nlp = None
    
    def analyze(self, text: str) -> Dict[str, Any]:
        """
        分析句子
        
        Args:
            text: 待分析的文本
        
        Returns:
            Dict[str, Any]: 分析结果
        """
        if self.nlp is None:
            logger.warning("spaCy未初始化，返回空分析结果")
            return {
                'tokens': [],
                'pos_tags': [],
                'dependencies': [],
                'entities': [],
                'noun_chunks': []
            }
        
        try:
            doc = self.nlp(text)
            
            analysis = {
                'tokens': [],
                'pos_tags': [],
                'dependencies': [],
                'entities': [],
                'noun_chunks': []
            }
            
            # 词性标注
            for token in doc:
                analysis['tokens'].append(token.text)
                # 如果模型未完全加载，pos_ 和 tag_ 可能不可用
                if self.model_loaded:
                    analysis['pos_tags'].append({
                        'text': token.text,
                        'pos': token.pos_,
                        'tag': token.tag_,
                        'lemma': token.lemma_
                    })
                    analysis['dependencies'].append({
                        'text': token.text,
                        'dep': token.dep_,
                        'head': token.head.text
                    })
                else:
                    analysis['pos_tags'].append({
                        'text': token.text,
                        'pos': '',
                        'tag': '',
                        'lemma': token.text.lower()
                    })
                    analysis['dependencies'].append({
                        'text': token.text,
                        'dep': '',
                        'head': ''
                    })
            
            # 命名实体（仅完整模型支持）
            if self.model_loaded:
                for ent in doc.ents:
                    analysis['entities'].append({
                        'text': ent.text,
                        'label': ent.label_
                    })
                
                # 名词短语
                for chunk in doc.noun_chunks:
                    analysis['noun_chunks'].append(chunk.text)
            
            return analysis
        
        except Exception as e:
            logger.error(f"spaCy分析失败: {e}")
            return {}
    
    def check_expression_naturalness(self, text: str) -> List[Dict]:
        """
        检查表达是否自然
        
        Args:
            text: 待检查的文本
        
        Returns:
            List[Dict]: 问题列表
        """
        if self.nlp is None:
            logger.warning("spaCy未初始化，跳过表达自然度检查")
            return []
        
        if not self.model_loaded:
            logger.warning("spaCy模型未完全加载，跳过表达自然度检查")
            return []
        
        try:
            doc = self.nlp(text)
            issues = []
            
            # 检查"very"修饰动词
            for i, token in enumerate(doc):
                if token.text.lower() == 'very' and i + 1 < len(doc):
                    next_token = doc[i + 1]
                    if next_token.pos_ == 'VERB':
                        issues.append({
                            'type': 'expression',
                            'message': f'"very" 不能直接修饰动词 "{next_token.text}"',
                            'suggestion': f'建议使用 "really {next_token.text}" 或 "{next_token.text} very much"',
                            'offset': token.idx,
                            'length': len(token.text) + len(next_token.text) + 1
                        })
            
            # 检查双重否定
            negations = [token for token in doc if token.dep_ == 'neg']
            if len(negations) > 1:
                issues.append({
                    'type': 'expression',
                    'message': '双重否定可能造成理解困难',
                    'suggestion': '建议使用肯定表达',
                    'offset': 0,
                    'length': len(text)
                })
            
            # 检查重复词
            word_count = {}
            for token in doc:
                if token.pos_ in ['NOUN', 'VERB', 'ADJ', 'ADV']:
                    word = token.text.lower()
                    word_count[word] = word_count.get(word, 0) + 1
            
            for word, count in word_count.items():
                if count > 1:
                    issues.append({
                        'type': 'expression',
                        'message': f'重复使用单词 "{word}" {count} 次',
                        'suggestion': '建议使用同义词避免重复',
                        'offset': 0,
                        'length': len(text)
                    })
            
            logger.info(f"spaCy表达检查完成，发现{len(issues)}个问题")
            return issues
        
        except Exception as e:
            logger.error(f"spaCy表达检查失败: {e}")
            return []
