package com.example.service;

import com.example.dao.QuestionDAO;
import com.example.dao.ExamRecordDAO;
import com.example.model.Question;
import com.example.model.ExamRecord;

import java.util.List;
import java.util.Map;

/**
 * 考试服务类
 * 负责考试相关的业务逻辑
 */
public class ExamService {
    private QuestionDAO questionDAO;
    private ExamRecordDAO examRecordDAO;

    public ExamService() {
        this.questionDAO = new QuestionDAO();
        this.examRecordDAO = new ExamRecordDAO();
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
     */
    public ExamResult submitExam(String studentId, String subject, 
                                List<Question> questions, Map<Integer, String> answers) {
        int correctCount = 0;
        int totalQuestions = questions.size();
        
        // 计算正确答案数量
        for (Question question : questions) {
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null && studentAnswer.equals(question.getCorrectAnswer())) {
                correctCount++;
            }
        }
        
        // 保存考试记录
        ExamRecord record = new ExamRecord(studentId, subject, correctCount, totalQuestions);
        examRecordDAO.saveExamRecord(record);
        
        // 返回考试结果
        return new ExamResult(correctCount, totalQuestions, questions, answers);
    }

    /**
     * 获取学生的考试历史
     */
    public List<ExamRecord> getStudentExamHistory(String studentId) {
        return examRecordDAO.getExamRecordsByStudentId(studentId);
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
        
        public ExamResult(int correctCount, int totalQuestions, 
                         List<Question> questions, Map<Integer, String> studentAnswers) {
            this.correctCount = correctCount;
            this.totalQuestions = totalQuestions;
            this.percentage = totalQuestions > 0 ? (double) correctCount / totalQuestions * 100 : 0;
            this.questions = questions;
            this.studentAnswers = studentAnswers;
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