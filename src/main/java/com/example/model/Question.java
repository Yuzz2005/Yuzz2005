package com.example.model;

import java.util.List;
import java.util.ArrayList;

/**
 * 题目实体类
 */
public class Question {
    private int id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String subject;
    private QuestionType type;
    private List<String> correctAnswers; // 用于多选题的多个正确答案
    private String fillBlankAnswer; // 用于填空题的答案

    public Question() {
        this.type = QuestionType.SINGLE_CHOICE; // 默认为单选题
        this.correctAnswers = new ArrayList<>();
    }

    // 单选题构造函数
    public Question(String questionText, String optionA, String optionB, 
                   String optionC, String optionD, String correctAnswer, String subject) {
        this();
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.subject = subject;
    }

    // 多选题构造函数
    public Question(String questionText, String optionA, String optionB, 
                   String optionC, String optionD, List<String> correctAnswers, String subject) {
        this();
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswers = correctAnswers;
        this.subject = subject;
        this.type = QuestionType.MULTIPLE_CHOICE;
    }

    // 填空题构造函数
    public Question(String questionText, String fillBlankAnswer, String subject) {
        this();
        this.questionText = questionText;
        this.fillBlankAnswer = fillBlankAnswer;
        this.subject = subject;
        this.type = QuestionType.FILL_BLANK;
    }

    public Question(int id, String questionText, String optionA, String optionB, 
                   String optionC, String optionD, String correctAnswer, String subject) {
        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.subject = subject;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(List<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public String getFillBlankAnswer() {
        return fillBlankAnswer;
    }

    public void setFillBlankAnswer(String fillBlankAnswer) {
        this.fillBlankAnswer = fillBlankAnswer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", type='" + type.getDescription() + '\'' +
                ", subject='" + subject + '\'' +
                "}";
    }
}