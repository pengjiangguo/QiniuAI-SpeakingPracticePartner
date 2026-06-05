package com.speakingpractice.partner.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Welcome to Speaking Practice Partner API");
        result.put("version", "1.0.0");
        result.put("documentation", "/api/doc.html");
        return result;
    }
}
