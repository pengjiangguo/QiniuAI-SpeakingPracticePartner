package com.speakingpractice.partner.common.enums;

import lombok.Getter;

/**
 * 英语水平枚举
 */
@Getter
public enum EnglishLevel {

    /**
     * 初学者
     */
    A1("A1", "初学者", 1),

    /**
     * 初级
     */
    A2("A2", "初级", 2),

    /**
     * 中级
     */
    B1("B1", "中级", 3),

    /**
     * 中高级
     */
    B2("B2", "中高级", 4),

    /**
     * 高级
     */
    C1("C1", "高级", 5),

    /**
     * 精通
     */
    C2("C2", "精通", 6);

    private final String code;
    private final String name;
    private final Integer level;

    EnglishLevel(String code, String name, Integer level) {
        this.code = code;
        this.name = name;
        this.level = level;
    }

    /**
     * 根据code获取枚举
     */
    public static EnglishLevel getByCode(String code) {
        for (EnglishLevel value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

}
