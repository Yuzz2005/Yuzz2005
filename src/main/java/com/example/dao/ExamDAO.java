package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.ExamRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamDAO {
    private static final Logger LOGGER = Logger.getLogger(ExamDAO.class.getName());
    private Connection connection;

    public ExamDAO() {
        // Default constructor, uses DatabaseManager to get connection
        this.connection = null; // Will get connection on demand
    }

    public ExamDAO(Connection connection) {
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
     * 添加考试记录
     *
     * @param record 考试记录对象
     * @return 是否成功添加
     * @throws SQLException 如果数据库操作失败
     */
    public boolean addExamRecord(ExamRecord record) throws SQLException {
        String sql = "INSERT INTO exam_records (student_id, subject, score, exam_date, total_questions) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        LOGGER.log(Level.FINE, "Attempting to add exam record for student: " + record.getStudentId());
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, String.valueOf(record.getStudentId()));
            pstmt.setString(2, record.getSubject());
            pstmt.setInt(3, record.getScore());
            pstmt.setTimestamp(4, new Timestamp(record.getExamDate().getTime()));
            pstmt.setInt(5, record.getTotalQuestions());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                        LOGGER.log(Level.INFO, "Successfully added exam record with ID: " + record.getId());
                        return true;
                    }
                }
            }
            
            LOGGER.log(Level.WARNING, "Failed to add exam record for student: " + record.getStudentId());
            return false;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding exam record: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据学生ID获取考试记录
     *
     * @param studentId 学生ID
     * @return 考试记录列表
     * @throws SQLException 如果数据库操作失败
     */
    public List<ExamRecord> getExamRecordsByStudentId(String studentId) throws SQLException {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT id, student_id, subject, score, total_questions, exam_date, comment " +
                    "FROM exam_records WHERE student_id = ? ORDER BY exam_date DESC";

        LOGGER.log(Level.FINE, "Retrieving exam records for student: " + studentId);

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        ExamRecord record = new ExamRecord(
                            rs.getInt("id"),
                            rs.getString("student_id"),
                            rs.getString("subject"),
                            rs.getInt("score"),
                            rs.getInt("total_questions"),
                            rs.getTimestamp("exam_date"),
                            rs.getString("comment")
                        );
                        records.add(record);
                        LOGGER.log(Level.FINE, "Loaded exam record ID: " + record.getId());
                    } catch (SQLException e) {
                        LOGGER.log(Level.WARNING, 
                            "Error processing exam record row for student: " + studentId, e);
                        // 继续处理下一行，而不是完全失败
                    }
                }
            }
            LOGGER.log(Level.INFO, "Successfully loaded " + records.size() + 
                " exam records for student: " + studentId);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, 
                "Error retrieving exam records for student: " + studentId, e);
            throw e;
        }

        return records;
    }

    /**
     * 获取所有考试记录
     *
     * @return 所有考试记录列表
     * @throws SQLException 如果数据库操作失败
     */
    public List<ExamRecord> getAllExamRecords() throws SQLException {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT id, student_id, subject, score, total_questions, exam_date, comment " +
                    "FROM exam_records ORDER BY exam_date DESC";

        LOGGER.log(Level.FINE, "Retrieving all exam records");

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                try {
                    ExamRecord record = new ExamRecord(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("exam_date"),
                        rs.getString("comment")
                    );
                    records.add(record);
                    LOGGER.log(Level.FINE, "Loaded exam record ID: " + record.getId());
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error processing exam record row", e);
                    // 继续处理下一行，而不是完全失败
                }
            }

            LOGGER.log(Level.INFO, "Successfully loaded " + records.size() + " exam records");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all exam records", e);
            throw e;
        }

        return records;
    }

    /**
     * 删除考试记录
     *
     * @param recordId 考试记录ID
     * @return 是否成功删除
     * @throws SQLException 如果数据库操作失败
     */
    public boolean deleteExamRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM exam_records WHERE id = ?";
        
        LOGGER.log(Level.FINE, "Attempting to delete exam record with ID: " + recordId);
        
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, recordId);

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.log(Level.INFO, "Successfully deleted exam record with ID: " + recordId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No exam record found with ID: " + recordId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting exam record with ID: " + recordId, e);
            throw e;
        }
    }

    /**
     * 根据学生ID和科目获取最佳成绩记录
     *
     * @param studentId 学生ID
     * @param subject 科目
     * @return 最佳成绩记录，如果没有则返回null
     * @throws SQLException 如果数据库操作失败
     */
    public ExamRecord getBestScoreByStudentAndSubject(String studentId, String subject) throws SQLException {
        String sql = "SELECT id, student_id, subject, score, total_questions, exam_date, comment " +
                    "FROM exam_records WHERE student_id = ? AND subject = ? " +
                    "ORDER BY score DESC, exam_date DESC LIMIT 1";

        LOGGER.log(Level.FINE, "Retrieving best score for student: " + studentId + ", subject: " + subject);

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ExamRecord record = new ExamRecord(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("exam_date"),
                        rs.getString("comment")
                    );
                    
                    LOGGER.log(Level.INFO, "Found best score record with ID: " + record.getId() + 
                        " for student: " + studentId + ", subject: " + subject);
                    return record;
                } else {
                    LOGGER.log(Level.INFO, "No exam records found for student: " + studentId + 
                        ", subject: " + subject);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving best score for student: " + 
                studentId + ", subject: " + subject, e);
            throw e;
        }
    }

    /**
     * 根据学生ID获取平均分
     *
     * @param studentId 学生ID
     * @return 平均分，如果没有记录则返回0.0
     * @throws SQLException 如果数据库操作失败
     */
    public double getAverageScoreByStudent(String studentId) throws SQLException {
        String sql = "SELECT AVG(score) AS average_score FROM exam_records WHERE student_id = ?";
        
        LOGGER.log(Level.FINE, "Calculating average score for student: " + studentId);

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {

            pstmt.setString(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double averageScore = rs.getDouble("average_score");
                    if (!rs.wasNull()) {
                        LOGGER.log(Level.INFO, "Average score for student " + studentId + ": " + averageScore);
                        return averageScore;
                    }
                }
            }

            LOGGER.log(Level.INFO, "No exam records found for student: " + studentId);
            return 0.0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average score for student: " + studentId, e);
            throw e;
        }
    }
}