"""
工具类
"""
import os
import json
from typing import Dict, Any
from pathlib import Path
from loguru import logger


class FileUtils:
    """文件工具类"""
    
    @staticmethod
    def ensure_dir(path: str) -> None:
        """确保目录存在"""
        Path(path).mkdir(parents=True, exist_ok=True)
    
    @staticmethod
    def save_json(data: Dict[Any, Any], file_path: str) -> None:
        """保存JSON文件"""
        with open(file_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
    
    @staticmethod
    def load_json(file_path: str) -> Dict[Any, Any]:
        """加载JSON文件"""
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)


class AudioUtils:
    """音频工具类"""
    
    @staticmethod
    def get_audio_duration(audio_path: str) -> float:
        """获取音频时长"""
        import librosa
        y, sr = librosa.load(audio_path, sr=None)
        return len(y) / sr
    
    @staticmethod
    def resample_audio(audio_path: str, target_sr: int = 16000) -> str:
        """音频重采样"""
        import librosa
        import soundfile as sf
        
        y, sr = librosa.load(audio_path, sr=target_sr)
        output_path = audio_path.replace('.wav', f'_{target_sr}.wav')
        sf.write(output_path, y, target_sr)
        return output_path


class TextUtils:
    """文本工具类"""
    
    @staticmethod
    def clean_text(text: str) -> str:
        """清理文本"""
        # 移除多余空格
        text = ' '.join(text.split())
        # 移除特殊字符
        text = ''.join(char for char in text if char.isalnum() or char in ' .,!?\'"')
        return text
    
    @staticmethod
    def split_sentences(text: str) -> list:
        """分割句子"""
        import re
        sentences = re.split(r'[.!?]+', text)
        return [s.strip() for s in sentences if s.strip()]
