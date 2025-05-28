package com.example.dao;

import com.example.model.ExamRecord;
import com.example.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    private DatabaseManager dbManager;

    public ExamDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 添加考试记录
     *
     * @param record 考试记录对象
     * @return 是否成功添加
     */
    public boolean addExamRecord(ExamRecord record) {
        String sql = "INSERT INTO exam_records (student_id, subject, score, exam_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(record.getStudentId()));
            pstmt.setString(2, record.getSubject());
            pstmt.setInt(3, record.getScore());
            pstmt.setTimestamp(4, new Timestamp(record.getExamDate().getTime()));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据学生ID获取考试记录
     *
     * @param studentId 学生ID
     * @return 考试记录列表
     */
    public List<ExamRecord> getExamRecordsByStudentId(int studentId) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE student_id = ? ORDER BY exam_date DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(studentId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ExamRecord record = new ExamRecord(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("exam_date")
                );
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 获取所有考试记录
     *
     * @return 所有考试记录列表
     */
    public List<ExamRecord> getAllExamRecords() {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records ORDER BY exam_date DESC";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ExamRecord record = new ExamRecord(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("exam_date")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 删除考试记录
     *
     * @param recordId 考试记录ID
     * @return 是否成功删除
     */
    public boolean deleteExamRecord(int recordId) {
        String sql = "DELETE FROM exam_records WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recordId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据学生ID和科目获取最佳成绩记录
     *
     * @param studentId 学生ID
     * @param subject 科目
     * @return 最佳成绩记录，如果没有则返回null
     */
    public ExamRecord getBestScoreByStudentAndSubject(String studentId, String subject) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE student_id = ? ORDER BY exam_date DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ExamRecord record = new ExamRecord(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("exam_date")
                );
                // Assuming only one best score record is needed
                return record;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no record found
    }

    /**
     * 根据学生ID获取平均分
     *
     * @param studentId 学生ID
     * @return 平均分
     */
    public double getAverageScoreByStudent(String studentId) {
        String sql = "SELECT AVG(score) AS average_score FROM exam_records WHERE student_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("average_score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if no records found or error occurred
    }

    /**
     * 保存考试记录
     * @param record 考试记录对象
     * @return 是否保存成功
     */
    public boolean saveExamRecord(ExamRecord record) {
        String sql = "INSERT INTO exam_records (student_id, subject, score, total_questions, exam_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, record.getStudentId());
            pstmt.setString(2, record.getSubject());
            pstmt.setInt(3, record.getScore());
            pstmt.setInt(4, record.getTotalQuestions());
            pstmt.setTimestamp(5, new Timestamp(record.getExamDate().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取学生的考试历史记录
     * @param studentId 学生ID
     * @return 考试记录列表
     */
    public List<ExamRecord> getExamHistory(String studentId) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE student_id = ? ORDER BY exam_date DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ExamRecord record = new ExamRecord();
                record.setId(rs.getInt("id"));
                record.setStudentId(rs.getString("student_id"));
                record.setSubject(rs.getString("subject"));
                record.setScore(rs.getInt("score"));
                record.setTotalQuestions(rs.getInt("total_questions"));
                record.setExamDate(rs.getTimestamp("exam_date"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }
}