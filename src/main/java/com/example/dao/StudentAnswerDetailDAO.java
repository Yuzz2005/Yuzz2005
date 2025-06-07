package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.QuestionType;
import com.example.model.StudentAnswerDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for StudentAnswerDetail.
 * 负责学生单题作答详情的数据库操作。
 */
public class StudentAnswerDetailDAO {

    private static final Logger LOGGER = Logger.getLogger(StudentAnswerDetailDAO.class.getName());
    private static final int BATCH_SIZE = 100;
    private Connection connection;

    public StudentAnswerDetailDAO() {
        // Default constructor
        this.connection = null;
    }

    public StudentAnswerDetailDAO(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() throws SQLException {
        if (this.connection != null) {
            return this.connection;
        } else {
            return DatabaseManager.getInstance().getConnection();
        }
    }

    /**
     * 保存单条学生答题记录
     * @param detail 学生答题详情对象
     * @return 保存成功返回true，否则返回false
     */
    public boolean saveStudentAnswerDetail(StudentAnswerDetail detail) throws SQLException {
        String sql = "INSERT INTO student_answer_details (exam_record_id, question_id, student_answer, " +
                    "is_correct, correct_answer, question_text, option_a, option_b, option_c, option_d, question_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, detail.getExamRecordId());
            pstmt.setInt(2, detail.getQuestionId());
            pstmt.setString(3, detail.getStudentAnswer());
            pstmt.setBoolean(4, detail.isCorrect());
            pstmt.setString(5, detail.getCorrectAnswer());
            pstmt.setString(6, detail.getQuestionText());
            pstmt.setString(7, detail.getOptionA());
            pstmt.setString(8, detail.getOptionB());
            pstmt.setString(9, detail.getOptionC());
            pstmt.setString(10, detail.getOptionD());
            pstmt.setString(11, detail.getQuestionType() != null ? detail.getQuestionType().name() : null);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        detail.setId(generatedKeys.getInt(1));
                        LOGGER.log(Level.FINE, "Successfully saved answer detail with ID: " + detail.getId());
                        return true;
                    }
                }
            }
            LOGGER.log(Level.WARNING, "Failed to save answer detail for question ID: " + detail.getQuestionId());
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving answer detail: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 批量保存学生答题记录，使用批处理提高性能
     * @param details 学生答题详情列表
     * @throws SQLException 如果数据库操作失败
     */
    public void saveAllStudentAnswerDetails(List<StudentAnswerDetail> details) throws SQLException {
        if (details == null || details.isEmpty()) {
            LOGGER.log(Level.WARNING, "No answer details to save");
            return;
        }

        String sql = "INSERT INTO student_answer_details (exam_record_id, question_id, student_answer, " +
                    "is_correct, correct_answer, question_text, option_a, option_b, option_c, option_d, question_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int count = 0;
            
            for (StudentAnswerDetail detail : details) {
                pstmt.setInt(1, detail.getExamRecordId());
                pstmt.setInt(2, detail.getQuestionId());
                pstmt.setString(3, detail.getStudentAnswer());
                pstmt.setBoolean(4, detail.isCorrect());
                pstmt.setString(5, detail.getCorrectAnswer());
                pstmt.setString(6, detail.getQuestionText());
                pstmt.setString(7, detail.getOptionA());
                pstmt.setString(8, detail.getOptionB());
                pstmt.setString(9, detail.getOptionC());
                pstmt.setString(10, detail.getOptionD());
                pstmt.setString(11, detail.getQuestionType() != null ? detail.getQuestionType().name() : null);
                
                pstmt.addBatch();
                count++;
                
                // 每BATCH_SIZE条记录执行一次批处理
                if (count % BATCH_SIZE == 0) {
                    pstmt.executeBatch();
                    LOGGER.log(Level.FINE, "Executed batch of " + BATCH_SIZE + " records");
                }
            }
            
            // 执行剩余的批处理
            if (count % BATCH_SIZE != 0) {
                pstmt.executeBatch();
                LOGGER.log(Level.FINE, "Executed final batch of " + (count % BATCH_SIZE) + " records");
            }
            
            LOGGER.log(Level.INFO, "Successfully saved " + details.size() + " answer details");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving answer details batch: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据考试记录ID获取学生的所有答题详情
     * @param examRecordId 考试记录ID
     * @return 学生答题详情列表
     * @throws SQLException 如果数据库操作失败
     */
    public List<StudentAnswerDetail> getStudentAnswerDetailsByExamRecordId(int examRecordId) throws SQLException {
        List<StudentAnswerDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM student_answer_details WHERE exam_record_id = ?";
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setInt(1, examRecordId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StudentAnswerDetail detail = new StudentAnswerDetail();
                    try {
                        detail.setId(rs.getInt("id"));
                        detail.setExamRecordId(rs.getInt("exam_record_id"));
                        detail.setQuestionId(rs.getInt("question_id"));
                        detail.setStudentAnswer(rs.getString("student_answer"));
                        detail.setCorrect(rs.getBoolean("is_correct"));
                        detail.setCorrectAnswer(rs.getString("correct_answer"));
                        detail.setQuestionText(rs.getString("question_text"));
                        detail.setOptionA(rs.getString("option_a"));
                        detail.setOptionB(rs.getString("option_b"));
                        detail.setOptionC(rs.getString("option_c"));
                        detail.setOptionD(rs.getString("option_d"));
                        
                        String questionTypeStr = rs.getString("question_type");
                        if (questionTypeStr != null) {
                            try {
                                detail.setQuestionType(QuestionType.valueOf(questionTypeStr));
                            } catch (IllegalArgumentException e) {
                                LOGGER.log(Level.WARNING, 
                                    "Invalid question type found in database: " + questionTypeStr, e);
                                detail.setQuestionType(null);
                            }
                        }
                        
                        details.add(detail);
                        LOGGER.log(Level.FINE, "Loaded answer detail ID: " + detail.getId());
                        
                    } catch (SQLException e) {
                        LOGGER.log(Level.WARNING, 
                            "Error processing result set row for exam record ID: " + examRecordId, e);
                        // 继续处理下一行，而不是完全失败
                    }
                }
            }
            
            LOGGER.log(Level.INFO, "Successfully loaded " + details.size() + 
                " answer details for exam record ID: " + examRecordId);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, 
                "Error retrieving answer details for exam record ID: " + examRecordId, e);
            throw e;
        }
        
        return details;
    }
} 