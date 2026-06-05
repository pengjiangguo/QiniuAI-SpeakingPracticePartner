package com.speakingpractice.partner.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Python AI服务客户端
 */
@Slf4j
@Service
public class PythonAIClient {

    @Value("${python.service.url:http://localhost:8000}")
    private String pythonServiceUrl;

    private final WebClient webClient;

    public PythonAIClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 发音评分
     */
    public Map<String, Object> scorePronunciation(String audioPath, String referenceText) {
        try {
            return webClient.post()
                    .uri(pythonServiceUrl + "/api/v1/pronunciation/score")
                    .bodyValue(Map.of("audio_path", audioPath, "reference_text", referenceText))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("调用Python发音评分服务失败", e);
            return null;
        }
    }

    /**
     * 语法检查
     */
    public List<Map<String, Object>> checkGrammar(String text) {
        try {
            return webClient.post()
                    .uri(pythonServiceUrl + "/api/v1/grammar/check")
                    .bodyValue(Map.of("text", text))
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            log.error("调用Python语法检查服务失败", e);
            return null;
        }
    }

    /**
     * 场景推荐
     */
    public List<Map<String, Object>> recommendScenes(Map<String, Object> userProfile, List<Map<String, Object>> sceneList) {
        try {
            return webClient.post()
                    .uri(pythonServiceUrl + "/api/v1/recommendation/scenes")
                    .bodyValue(Map.of("user_profile", userProfile, "scene_list", sceneList))
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            log.error("调用Python场景推荐服务失败", e);
            return null;
        }
    }

    /**
     * 趋势分析
     */
    public Map<String, Object> analyzeTrend(List<Map<String, Object>> scores, String dimension) {
        try {
            return webClient.post()
                    .uri(pythonServiceUrl + "/api/v1/analysis/trend")
                    .bodyValue(Map.of("scores", scores, "dimension", dimension))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("调用Python趋势分析服务失败", e);
            return null;
        }
    }

    /**
     * 健康检查
     */
    public boolean healthCheck() {
        try {
            Map result = webClient.get()
                    .uri(pythonServiceUrl + "/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return result != null && "healthy".equals(result.get("status"));
        } catch (Exception e) {
            log.error("Python AI服务健康检查失败", e);
            return false;
        }
    }

}
