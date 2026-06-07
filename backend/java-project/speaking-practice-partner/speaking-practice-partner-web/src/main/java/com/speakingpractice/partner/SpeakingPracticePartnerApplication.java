package com.speakingpractice.partner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Speaking Practice Partner - AI口语陪练系统启动类
 */
@SpringBootApplication
public class SpeakingPracticePartnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakingPracticePartnerApplication.class, args);
        System.out.println(
                "==================================\n" +
                "AI口语陪练后端启动成功\n" +
                "==================================\n");
    }

}
