package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.ExamRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 考试记录数据访问对象
 */
public class ExamRecordDAO {
    private DatabaseManager dbManager;

    public ExamRecordDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 保存考试记录
     */
    public boolean saveExamRecord(ExamRecord record) {
        String sql = "INSERT INTO exam_records (student_id, subject, score, total_questions) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, record.getStudentId());
            pstmt.setString(2, record.getSubject());
            pstmt.setInt(3, record.getScore());
            pstmt.setInt(4, record.getTotalQuestions());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取学生的所有考试记录
     */
    public List<ExamRecord> getExamRecordsByStudentId(String studentId) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE student_id = ? ORDER BY exam_date DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
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
     * 获取学生在特定科目的考试记录
     */
    public List<ExamRecord> getExamRecordsByStudentAndSubject(String studentId, String subject) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE student_id = ? AND subject = ? ORDER BY exam_date DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);
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
     * 获取学生在特定科目的最高分
     */
    public ExamRecord getBestScoreByStudentAndSubject(String studentId, String subject) {
        String sql = "SELECT * FROM exam_records WHERE student_id = ? AND subject = ? ORDER BY score DESC, exam_date DESC LIMIT 1";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new ExamRecord(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("subject"),
                    rs.getInt("score"),
                    rs.getInt("total_questions"),
                    rs.getTimestamp("exam_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 获取所有考试记录
     */
    public List<ExamRecord> getAllExamRecords() {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records ORDER BY exam_date DESC";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
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
     * 获取特定科目的所有考试记录
     */
    public List<ExamRecord> getExamRecordsBySubject(String subject) {
        List<ExamRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM exam_records WHERE subject = ? ORDER BY exam_date DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, subject);
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
     * 删除考试记录
     */
    public boolean deleteExamRecord(int id) {
        String sql = "DELETE FROM exam_records WHERE id = ?";
        
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
     * 获取学生的考试次数
     */
    public int getExamCountByStudent(String studentId) {
        String sql = "SELECT COUNT(*) FROM exam_records WHERE student_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
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
     * 获取学生的平均分
     */
    public double getAverageScoreByStudent(String studentId) {
        String sql = "SELECT AVG(CAST(score AS FLOAT) / total_questions * 100) FROM exam_records WHERE student_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
}