package com.example.model;

import java.sql.Timestamp;

/**
 * 考试记录实体类
 */
public class ExamRecord {
    private int id;
    private String studentId;
    private String subject;
    private int score;
    private int totalQuestions;
    private Timestamp examDate;
    private String comment;

    public ExamRecord() {}

    public ExamRecord(String studentId, String subject, int score, int totalQuestions) {
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.comment = "";
    }

    public ExamRecord(int id, String studentId, String subject, int score, 
                     int totalQuestions, Timestamp examDate) {
        this.id = id;
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.examDate = examDate;
        this.comment = "";
    }

    public ExamRecord(int id, String studentId, String subject, int score, 
                     int totalQuestions, Timestamp examDate, String comment) {
        this.id = id;
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.examDate = examDate;
        this.comment = comment;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Timestamp getExamDate() {
        return examDate;
    }

    public void setExamDate(Timestamp examDate) {
        this.examDate = examDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 计算得分百分比
     */
    public double getScorePercentage() {
        if (totalQuestions == 0) return 0;
        return (double) score / totalQuestions * 100;
    }

    @Override
    public String toString() {
        return "ExamRecord{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", subject='" + subject + '\'' +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", examDate=" + examDate +
                ", comment='" + comment + '\'' +
                "}";
    }
}