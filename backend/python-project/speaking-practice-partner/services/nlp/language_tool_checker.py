"""
LanguageTool语法检查器
使用LanguageTool进行专业的语法检查
"""
from typing import List, Optional
from models import GrammarError, ErrorType
from config import settings
from loguru import logger
import os
import zipfile
import requests
from pathlib import Path
import subprocess
import time
import json


class LanguageToolChecker:
    """LanguageTool语法检查器"""
    
    def __init__(self):
        """初始化LanguageTool"""
        try:
            # 配置LanguageTool本地路径
            self._setup_local_languagetool()
            
            # 使用本地LanguageTool（通过启动本地服务器）
            self._start_local_server()
            
            logger.info("LanguageTool初始化成功")
        except Exception as e:
            logger.error(f"LanguageTool初始化失败: {e}")
            raise
    
    def _setup_local_languagetool(self):
        """设置本地LanguageTool"""
        # 定义路径
        tools_dir = Path(__file__).parent.parent.parent / "tools"
        self.zip_path = tools_dir / "LanguageTool-6.6.zip"
        self.extract_dir = tools_dir / "LanguageTool-6.6"
        
        # 确保tools目录存在
        tools_dir.mkdir(parents=True, exist_ok=True)
        
        # 找到languagetool.jar文件（优先检查已解压的目录）
        possible_jar_paths = [
            self.extract_dir / "LanguageTool-6.6" / "languagetool.jar",  # 解压后的标准路径
            self.extract_dir / "languagetool.jar",  # 直接在目录下
            tools_dir / "LanguageTool-6.6" / "LanguageTool-6.6" / "languagetool.jar"  # 其他可能路径
        ]
        
        # 检查是否已有解压后的jar文件
        for jar_path in possible_jar_paths:
            if jar_path.exists():
                self.languagetool_jar = jar_path
                logger.info(f"使用本地LanguageTool: {self.languagetool_jar}")
                return
        
        # 如果没有找到jar文件，检查zip是否存在
        if self.zip_path.exists():
            logger.info("LanguageTool zip文件存在，开始解压...")
            self._extract_languagetool(self.zip_path, self.extract_dir)
            # 解压后重新查找jar文件
            self.languagetool_jar = self.extract_dir / "LanguageTool-6.6" / "languagetool.jar"
            if not self.languagetool_jar.exists():
                raise FileNotFoundError(f"解压后找不到languagetool.jar文件")
        else:
            # 需要下载
            logger.info("LanguageTool不存在，开始下载...")
            self._download_languagetool(self.zip_path)
            self._extract_languagetool(self.zip_path, self.extract_dir)
            self.languagetool_jar = self.extract_dir / "LanguageTool-6.6" / "languagetool.jar"
        
        logger.info(f"使用本地LanguageTool: {self.languagetool_jar}")
    
    def _download_languagetool(self, zip_path: Path):
        """
        下载LanguageTool
        
        Args:
            zip_path: 保存路径
        """
        try:
            url = "https://languagetool.org/download/LanguageTool-6.6.zip"
            logger.info(f"下载LanguageTool: {url}")
            
            # 下载文件
            response = requests.get(url, stream=True, timeout=30)
            response.raise_for_status()
            
            # 保存文件
            total_size = int(response.headers.get('content-length', 0))
            downloaded_size = 0
            
            with open(zip_path, 'wb') as f:
                for chunk in response.iter_content(chunk_size=8192):
                    f.write(chunk)
                    downloaded_size += len(chunk)
                    if total_size > 0:
                        progress = (downloaded_size / total_size) * 100
                        if downloaded_size % (1024 * 1024) == 0:  # 每MB打印一次
                            logger.info(f"下载进度: {progress:.1f}%")
            
            logger.info(f"LanguageTool下载完成: {zip_path}")
        
        except Exception as e:
            logger.error(f"LanguageTool下载失败: {e}")
            raise
    
    def _extract_languagetool(self, zip_path: Path, extract_dir: Path):
        """
        解压LanguageTool
        
        Args:
            zip_path: zip文件路径
            extract_dir: 解压目录
        """
        try:
            logger.info(f"解压LanguageTool: {zip_path} -> {extract_dir}")
            
            # 解压文件
            with zipfile.ZipFile(zip_path, 'r') as zip_ref:
                zip_ref.extractall(extract_dir)
            
            logger.info(f"LanguageTool解压完成: {extract_dir}")
        
        except Exception as e:
            logger.error(f"LanguageTool解压失败: {e}")
            raise
    
    def _get_java_executable(self) -> str:
        """
        获取Java可执行文件路径
        优先级：配置文件 > JAVA_HOME环境变量 > 系统PATH
        
        Returns:
            str: Java可执行文件的完整路径
        """
        # 1. 优先使用配置文件中的java_home
        if settings.java_home:
            java_exe = os.path.join(
                settings.java_home,
                'bin',
                'java.exe' if os.name == 'nt' else 'java'
            )
            if os.path.exists(java_exe):
                logger.info(f"使用配置文件中的Java: {java_exe}")
                return java_exe
            else:
                logger.warning(f"配置的java_home路径不存在: {settings.java_home}")
        
        # 2. 使用JAVA_HOME环境变量
        java_home = os.environ.get('JAVA_HOME')
        if java_home:
            java_exe = os.path.join(java_home, 'bin', 'java.exe' if os.name == 'nt' else 'java')
            if os.path.exists(java_exe):
                logger.info(f"使用JAVA_HOME中的Java: {java_exe}")
                return java_exe
        
        # 3. 尝试查找PATH中的Java
        import shutil
        java_exe = shutil.which('java')
        if java_exe:
            # 验证Java版本
            try:
                result = subprocess.run(
                    [java_exe, '-version'],
                    capture_output=True,
                    text=True
                )
                version_output = result.stderr + result.stdout
                if 'version "17' in version_output or 'version "1' not in version_output:
                    logger.info(f"使用PATH中的Java: {java_exe}")
                    return java_exe
            except Exception as e:
                logger.warning(f"验证Java版本失败: {e}")
        
        # 4. 默认返回java，让系统处理
        logger.warning("未找到合适的Java，使用系统默认")
        return 'java'
    
    def _start_local_server(self):
        """启动本地LanguageTool服务器"""
        try:
            # 获取Java可执行文件路径，优先使用JAVA_HOME
            java_exe = self._get_java_executable()
            
            # 启动LanguageTool HTTP服务器
            # 使用Java启动languagetool.jar
            cmd = [
                java_exe,
                '-cp', str(self.languagetool_jar),
                'org.languagetool.server.HTTPServer',
                '--port', '8081'
            ]
            
            logger.info(f"启动LanguageTool服务器: {' '.join(cmd)}")
            
            # 启动进程
            self.server_process = subprocess.Popen(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE
            )
            
            # 等待服务器启动
            time.sleep(5)
            
            # 检查服务器是否启动成功
            if self.server_process.poll() is not None:
                # 进程已结束，启动失败
                stdout, stderr = self.server_process.communicate()
                raise RuntimeError(f"LanguageTool服务器启动失败: {stderr.decode()}")
            
            # 验证服务器是否可访问
            self._verify_server()
            
            logger.info("LanguageTool服务器启动成功，端口: 8081")
            
        except Exception as e:
            logger.error(f"启动LanguageTool服务器失败: {e}")
            raise RuntimeError(f"LanguageTool初始化失败: {e}")
    
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
        
        raise RuntimeError("无法连接到LanguageTool服务器")
    
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
    
    def cleanup(self):
        """清理资源，关闭服务器"""
        try:
            if hasattr(self, 'server_process') and self.server_process:
                logger.info("关闭LanguageTool服务器")
                self.server_process.terminate()
                self.server_process.wait(timeout=5)
                logger.info("LanguageTool服务器已关闭")
        except Exception as e:
            logger.error(f"关闭LanguageTool服务器失败: {e}")
    
    def __del__(self):
        """析构函数"""
        self.cleanup()
    
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
