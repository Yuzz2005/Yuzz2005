package com.example.service;

import com.example.dao.QuestionDAO;
import com.example.dao.ExamRecordDAO;
import com.example.model.Question;
import com.example.model.ExamRecord;
import com.example.model.StudentAnswerDetail;
import com.example.dao.StudentAnswerDetailDAO;
import com.example.model.QuestionType;
import com.example.dao.ExamDAO;
import com.example.database.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 考试服务类
 * 负责考试相关的业务逻辑
 */
public class ExamService {
    private QuestionDAO questionDAO;
    private ExamRecordDAO examRecordDAO;
    private StudentAnswerDetailDAO studentAnswerDetailDAO;

    private static final Logger LOGGER = Logger.getLogger(ExamService.class.getName());

    public ExamService() {
        this.questionDAO = new QuestionDAO();
        this.examRecordDAO = new ExamRecordDAO();
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
        LOGGER.log(Level.INFO, "加载到题目数量：" + questions.size());
        return questions;
    }

    /**
     * 提交考试答案并计算分数
     * 同时保存详细的答题记录
     */
    public ExamResult submitExam(String studentId, String subject, 
                                List<Question> questions, Map<Integer, String> answers) {
        ExamResult examResult = null;
        DatabaseManager dbManager = DatabaseManager.getInstance();
        
        try {
            // 开始事务
            dbManager.beginTransaction();
            
            // 获取数据库连接
            Connection conn = dbManager.getConnection();
            ExamDAO examDAO = new ExamDAO(conn);
            StudentAnswerDetailDAO studentAnswerDetailDAO = new StudentAnswerDetailDAO(conn);

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
                // Create StudentAnswerDetail object
                StudentAnswerDetail detail = new StudentAnswerDetail(0, 
                                                                    question.getId(), 
                                                                    userAnswer,         // Store student answer (letter for single/multiple choice, text for fill-in-the-blank)
                                                                    isCorrect, 
                                                                    correctAnswerText,  // Store correct answer (letter for single/multiple choice, text for fill-in-the-blank)
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
            record.setExamDate(new java.sql.Timestamp(System.currentTimeMillis()));
            record.setTotalQuestions(totalQuestions);
            
            boolean recordSaved = examDAO.addExamRecord(record);
            
            if (!recordSaved || record.getId() <= 0) {
                LOGGER.log(Level.SEVERE, "保存考试记录失败");
                dbManager.rollbackTransaction();
                return null;
            }

            // 2. 保存详细答题记录
            for (StudentAnswerDetail detail : answerDetails) {
                detail.setExamRecordId(record.getId());
            }
            
            try {
                studentAnswerDetailDAO.saveAllStudentAnswerDetails(answerDetails);
                
                // 提交事务
                dbManager.commitTransaction();
                
                // 创建并返回考试结果
                examResult = new ExamResult(correctCount, totalQuestions, questions, answers, record.getId());
                LOGGER.log(Level.INFO, "考试提交成功，考试记录ID: " + record.getId());
                
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "保存答题详情失败: " + e.getMessage(), e);
                dbManager.rollbackTransaction();
                return null;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "提交考试时发生数据库错误: " + e.getMessage(), e);
            dbManager.rollbackTransaction();
        } finally {
            dbManager.closeConnection();
        }
        return examResult; // Return exam result
    }

    // Helper method, get option text by letter (if Question model does not provide)
    // This method might already exist in Question model or ExamSystemGUI as getOptionByLetter
    private String getOptionTextByLetter(Question question, String letter) {
        if (letter == null) return "";
        switch (letter) {
            case "A": return question.getOptionA();
            case "B": return question.getOptionB();
            case "C": return question.getOptionC();
            case "D": return question.getOptionD();
            default: return ""; // Or throw exception
        }
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
     * 获取所有考试记录
     */
    public List<ExamRecord> getAllExamRecords() {
        return examRecordDAO.getAllExamRecords();
    }

    /**
     * 更新考试记录的评语
     */
    public boolean updateExamRecordComment(int examRecordId, String comment) {
        return examRecordDAO.updateExamRecordComment(examRecordId, comment);
    }

    /**
     * 删除考试记录
     */
    public boolean deleteExamRecord(int recordId) {
        return examRecordDAO.deleteExamRecord(recordId);
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