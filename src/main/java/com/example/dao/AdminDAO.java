package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 管理员数据访问对象
 */
public class AdminDAO {
    private DatabaseManager dbManager;

    public AdminDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 验证管理员登录
     */
    public Admin validateLogin(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 可以根据需要添加其他管理员相关的数据库操作方法
}