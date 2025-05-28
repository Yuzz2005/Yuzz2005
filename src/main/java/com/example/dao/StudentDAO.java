package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问对象
 */
public class StudentDAO {
    private DatabaseManager dbManager;

    public StudentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 验证学生登录
     */
    public Student validateLogin(String studentId, String password) {
        String sql = "SELECT * FROM students WHERE student_id = ? AND password = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据学号查找学生
     */
    public Student findByStudentId(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加新学生
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getPassword());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // 打印更详细的错误信息
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新学生信息
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, password = ? WHERE student_id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getPassword());
            pstmt.setString(3, student.getStudentId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取所有学生
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("password")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    /**
     * 删除学生
     */
    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}