package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.Question;
import com.example.model.QuestionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 题目数据访问对象
 */
public class QuestionDAO {
    private DatabaseManager dbManager;

    public QuestionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 获取所有题目
     */
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY id";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Question question = createQuestionFromResultSet(rs);
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }

    /**
     * 根据科目获取题目列表
     */
    public List<Question> getQuestionsBySubject(String subject) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE subject = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, subject);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = createQuestionFromResultSet(rs);
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return questions;
    }

    /**
     * 随机获取指定数量的题目
     */
    public List<Question> getRandomQuestions(String subject, int count) {
        List<Question> allQuestions = getQuestionsBySubject(subject);
        
        if (allQuestions.size() <= count) {
            Collections.shuffle(allQuestions);
            return allQuestions;
        }
        
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, count);
    }

    /**
     * 获取所有科目
     */
    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        String sql = "SELECT DISTINCT subject FROM questions ORDER BY subject";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                subjects.add(rs.getString("subject"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return subjects;
    }

    /**
     * 根据ID获取题目
     */
    public Question getQuestionById(int id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createQuestionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 添加新题目
     */
    public boolean addQuestion(Question question) {
        String sql = """
            INSERT INTO questions (
                question_text, option_a, option_b, option_c, option_d, 
                correct_answer, correct_answers, fill_blank_answer, 
                question_type, subject
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, question.getCorrectAnswer());
            pstmt.setString(7, question.getCorrectAnswers() != null ? 
                String.join(",", question.getCorrectAnswers()) : null);
            pstmt.setString(8, question.getFillBlankAnswer());
            pstmt.setString(9, question.getType().name());
            pstmt.setString(10, question.getSubject());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新题目
     */
    public boolean updateQuestion(Question question) {
        String sql = """
            UPDATE questions SET 
                question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, 
                correct_answer = ?, correct_answers = ?, fill_blank_answer = ?, 
                question_type = ?, subject = ? 
            WHERE id = ?
            """;
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, question.getQuestionText());
            pstmt.setString(2, question.getOptionA());
            pstmt.setString(3, question.getOptionB());
            pstmt.setString(4, question.getOptionC());
            pstmt.setString(5, question.getOptionD());
            pstmt.setString(6, question.getCorrectAnswer());
            pstmt.setString(7, question.getCorrectAnswers() != null ? 
                String.join(",", question.getCorrectAnswers()) : null);
            pstmt.setString(8, question.getFillBlankAnswer());
            pstmt.setString(9, question.getType().name());
            pstmt.setString(10, question.getSubject());
            pstmt.setInt(11, question.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除题目
     */
    public boolean deleteQuestion(int id) {
        String sql = "DELETE FROM questions WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取指定科目的题目数量
     */
    public int getQuestionCountBySubject(String subject) {
        String sql = "SELECT COUNT(*) FROM questions WHERE subject = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, subject);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * 从ResultSet创建Question对象
     */
    private Question createQuestionFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setOptionA(rs.getString("option_a"));
        question.setOptionB(rs.getString("option_b"));
        question.setOptionC(rs.getString("option_c"));
        question.setOptionD(rs.getString("option_d"));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setSubject(rs.getString("subject"));
        
        // 设置题目类型
        String typeStr = rs.getString("question_type");
        question.setType(QuestionType.valueOf(typeStr));
        
        // 根据题目类型设置相应的答案
        switch (question.getType()) {
            case MULTIPLE_CHOICE:
                String correctAnswersStr = rs.getString("correct_answers");
                if (correctAnswersStr != null) {
                    question.setCorrectAnswers(Arrays.asList(correctAnswersStr.split(",")));
                }
                break;
            case FILL_BLANK:
                question.setFillBlankAnswer(rs.getString("fill_blank_answer"));
                break;
        }
        
        return question;
    }
}