package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 科目数据访问对象
 */
public class SubjectDAO {
    private DatabaseManager dbManager;

    public SubjectDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 添加新科目
     */
    public boolean addSubject(String subjectName) {
        String sql = "INSERT INTO subjects (name) VALUES (?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subjectName);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除科目
     */
    public boolean deleteSubject(int subjectId) {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subjectId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取所有科目
     */
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Subject subject = new Subject(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                subjects.add(subject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    /**
     * 根据科目名称查找科目
     */
    public Subject findSubjectByName(String subjectName) {
        String sql = "SELECT * FROM subjects WHERE name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subjectName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Subject(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}