package com.example.model;

/**
 * 题目类型枚举
 * 定义了系统中支持的题目类型
 */
public enum QuestionType {
    SINGLE_CHOICE("单选题"),
    MULTIPLE_CHOICE("多选题"),
    FILL_BLANK("填空题");

    private final String description;

    QuestionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 