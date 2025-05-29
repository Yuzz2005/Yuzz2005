package com.example.model;

/**
 * 学生单题作答详情模型
 */
public class StudentAnswerDetail {
    private int id; // 自增ID
    private int examRecordId; // 关联的考试记录ID
    private int questionId; // 题目ID
    private String studentAnswer; // 学生答案
    private boolean isCorrect; // 是否正确
    private String correctAnswer; // 题目的正确答案 (冗余存储，方便查询)
    private String questionText; // 题干 (冗余存储，方便查询)
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private QuestionType questionType; // 题目类型
    // 如果题目有解析字段，也需要添加进来
    // private String analysis; 

    // 构造函数
    public StudentAnswerDetail() {
    }

    public StudentAnswerDetail(int examRecordId, int questionId, String studentAnswer, boolean isCorrect, String correctAnswer, String questionText, QuestionType questionType, String optionA, String optionB, String optionC, String optionD) {
        this.examRecordId = examRecordId;
        this.questionId = questionId;
        this.studentAnswer = studentAnswer;
        this.isCorrect = isCorrect;
        this.correctAnswer = correctAnswer; // 存储正确答案的文本表示
        this.questionText = questionText;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }


    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamRecordId() {
        return examRecordId;
    }

    public void setExamRecordId(int examRecordId) {
        this.examRecordId = examRecordId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    // 如果有解析，添加getter/setter
    // public String getAnalysis() { return analysis; }
    // public void setAnalysis(String analysis) { this.analysis = analysis; }
} 