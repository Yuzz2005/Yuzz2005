package com.example.service;

import com.example.dao.QuestionDAO;
import com.example.dao.ExamRecordDAO;
import com.example.model.Question;
import com.example.model.ExamRecord;
import com.example.model.StudentAnswerDetail;
import com.example.dao.StudentAnswerDetailDAO;
import com.example.model.QuestionType;
import com.example.dao.ExamDAO;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 考试服务类
 * 负责考试相关的业务逻辑
 */
public class ExamService {
    private QuestionDAO questionDAO;
    private ExamRecordDAO examRecordDAO;
    private StudentAnswerDetailDAO studentAnswerDetailDAO;
    private ExamDAO examDAO;

    public ExamService() {
        this.questionDAO = new QuestionDAO();
        this.examDAO = new ExamDAO();
        this.studentAnswerDetailDAO = new StudentAnswerDetailDAO();
    }

    /**
     * 获取可用的科目列表
     */
    public List<String> getAvailableSubjects() {
        return questionDAO.getAllSubjects();
    }

    /**
     * 获取指定科目的题目数量
     */
    public int getQuestionCount(String subject) {
        return questionDAO.getQuestionCountBySubject(subject);
    }

    /**
     * 开始考试 - 获取随机题目
     */
    public List<Question> startExam(String subject, int questionCount) {
        List<Question> questions = questionDAO.getRandomQuestions(subject, questionCount);
        System.out.println("加载到题目数量：" + questions.size());
        return questions;
    }

    /**
     * 提交考试答案并计算分数
     * 同时保存详细的答题记录
     */
    public ExamResult submitExam(String studentId, String subject, 
                                List<Question> questions, Map<Integer, String> answers) {
        int correctCount = 0;
        int totalQuestions = questions.size();
        List<StudentAnswerDetail> answerDetails = new ArrayList<>();
        
        // 计算正确答案数量并准备详细记录
        for (Question question : questions) {
            String userAnswer = answers.get(question.getId());
            if (userAnswer == null) userAnswer = "";
            boolean isCorrect = false;
            String correctAnswerText = "";

            switch (question.getType()) {
                case SINGLE_CHOICE:
                    correctAnswerText = question.getCorrectAnswer();
                    if (userAnswer.equals(correctAnswerText)) {
                        isCorrect = true;
                        correctCount++;
                    }
                    break;
                case MULTIPLE_CHOICE:
                    List<String> correctOptionLetters = question.getCorrectAnswers();
                    List<String> studentSelectedOptionLetters = new ArrayList<>();
                    if (userAnswer != null && !userAnswer.isEmpty()) {
                        studentSelectedOptionLetters.addAll(Arrays.asList(userAnswer.split(",")));
                    }
                    
                    List<String> sortedCorrectOptions = correctOptionLetters.stream().map(String::trim).sorted().collect(java.util.stream.Collectors.toList());
                    List<String> sortedStudentOptions = studentSelectedOptionLetters.stream().map(String::trim).sorted().collect(java.util.stream.Collectors.toList());

                    if (sortedStudentOptions.equals(sortedCorrectOptions)) {
                        isCorrect = true;
                        correctCount++;
                    }
                    correctAnswerText = String.join(",", correctOptionLetters);
                    break;
                case FILL_BLANK:
                    correctAnswerText = question.getFillBlankAnswer();
                    if (userAnswer.trim().equalsIgnoreCase(correctAnswerText == null ? "" : correctAnswerText.trim())) {
                        isCorrect = true;
                correctCount++;
            }
                    break;
            }
            // 创建 StudentAnswerDetail 对象
            StudentAnswerDetail detail = new StudentAnswerDetail(0, 
                                                                question.getId(), 
                                                                userAnswer,         // 存储学生答案（单选/多选时为字母，填空为文本）
                                                                isCorrect, 
                                                                correctAnswerText,  // 存储正确答案（单选/多选时为字母，填空为文本）
                                                                question.getQuestionText(),
                                                                question.getType(),
                                                                question.getOptionA(),
                                                                question.getOptionB(),
                                                                question.getOptionC(),
                                                                question.getOptionD()
                                                                );
            answerDetails.add(detail);
        }
        
        // 1. 保存考试记录
        ExamRecord record = new ExamRecord(studentId, subject, correctCount, totalQuestions);
        record.setExamDate(new java.sql.Timestamp(new java.util.Date().getTime())); // 设置考试日期
        
        boolean recordSaved = examDAO.saveExamRecord(record); // record 对象在保存后会获得ID
        
        if (recordSaved && record.getId() > 0) { // 确保记录已保存且获得了ID
            // 2. 为每条详细记录设置 examRecordId 并保存
            for (StudentAnswerDetail detail : answerDetails) {
                detail.setExamRecordId(record.getId());
            }
            studentAnswerDetailDAO.saveAllStudentAnswerDetails(answerDetails);
        } else {
            // 处理保存考试记录失败的情况，可能需要回滚或记录错误
            System.err.println("保存考试记录失败，详细答题记录未保存。");
            // 可以考虑抛出异常
        }
        
        // 返回考试结果
        return new ExamResult(correctCount, totalQuestions, questions, answers, record.getId()); // 传递 recordId
    }

    // 辅助方法，根据选项字母获取选项文本 (如果 Question 模型没有提供)
    // 这个方法可能在 Question 模型或者 ExamSystemGUI 中已经有了类似的 getOptionByLetter
    private String getOptionTextByLetter(Question question, String letter) {
        if (letter == null) return "";
        switch (letter) {
            case "A": return question.getOptionA();
            case "B": return question.getOptionB();
            case "C": return question.getOptionC();
            case "D": return question.getOptionD();
            default: return ""; // 或者抛出异常
        }
    }

    /**
     * 获取学生的考试历史
     */
    public List<ExamRecord> getStudentExamHistory(String studentId) {
        return examDAO.getExamHistory(studentId); // 改用 examDAO
    }

    /**
     * 获取学生在特定科目的最佳成绩
     */
    public ExamRecord getBestScore(String studentId, String subject) {
        return examRecordDAO.getBestScoreByStudentAndSubject(studentId, subject);
    }

    /**
     * 获取学生的平均分
     */
    public double getAverageScore(String studentId) {
        return examRecordDAO.getAverageScoreByStudent(studentId);
    }

    /**
     * 考试结果内部类
     */
    public static class ExamResult {
        private int correctCount;
        private int totalQuestions;
        private double percentage;
        private List<Question> questions;
        private Map<Integer, String> studentAnswers;
        private int examRecordId;
        
        public ExamResult(int correctCount, int totalQuestions, 
                         List<Question> questions, Map<Integer, String> studentAnswers, int examRecordId) {
            this.correctCount = correctCount;
            this.totalQuestions = totalQuestions;
            this.percentage = totalQuestions > 0 ? (double) correctCount / totalQuestions * 100 : 0;
            this.questions = questions;
            this.studentAnswers = studentAnswers;
            this.examRecordId = examRecordId;
        }
        
        // Getters
        public int getCorrectCount() {
            return correctCount;
        }
        
        public int getTotalQuestions() {
            return totalQuestions;
        }
        
        public double getPercentage() {
            return percentage;
        }
        
        public List<Question> getQuestions() {
            return questions;
        }
        
        public Map<Integer, String> getStudentAnswers() {
            return studentAnswers;
        }
        
        public int getExamRecordId() {
            return examRecordId;
        }
        
        public String getGrade() {
            if (percentage >= 90) return "优秀";
            else if (percentage >= 80) return "良好";
            else if (percentage >= 70) return "中等";
            else if (percentage >= 60) return "及格";
            else return "不及格";
        }
        
        public boolean isPassed() {
            return percentage >= 60;
        }
    }
}