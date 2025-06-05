package com.example;

import com.example.database.DatabaseManager;
import com.example.gui.MainFrame;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 考试系统启动类
 */
public class ExamSystemMain {
    public static void main(String[] args) {
        // 设置 Look and Feel
        try {
            // 设置 FlatLaf Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExamSystemMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        // 初始化数据库，确保只在程序启动时执行一次
        DatabaseManager.getInstance().initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            MainFrame gui = new MainFrame();
            gui.setVisible(true);

            // 添加窗口监听器，在窗口关闭时关闭数据库连接
            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    DatabaseManager.getInstance().closeConnection();
                    System.out.println("Database connection closed.");
                    System.exit(0);
                }
            });
        });
    }
}