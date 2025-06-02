package com.example.gui;

import com.example.dao.*;
import com.example.model.*;
import com.example.service.ExamService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.List; // 确保 List 被导入

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // DAOs and Services - MainFrame 将管理这些实例并传递给子面板
    private StudentDAO studentDAO;
    private AdminDAO adminDAO;
    private QuestionDAO questionDAO;
    private ExamDAO examDAO;
    private SubjectDAO subjectDAO;
    private StudentAnswerDetailDAO studentAnswerDetailDAO;
    private ExamService examService;
    
    // 当前登录用户
    private Student currentStudent;
    private Admin currentAdmin;

    // 登录界面组件 (从 ExamSystemGUI 移动过来)
    private JTextField studentIdField;
    private JPasswordField passwordField;
    private JComboBox<String> loginTypeComboBox;

    // 面板常量，用于CardLayout导航
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String STUDENT_PANEL = "STUDENT";
    public static final String ADMIN_PANEL = "ADMIN";

    public MainFrame() {
        // 初始化 DAOs 和 Services
        this.studentDAO = new StudentDAO();
        this.adminDAO = new AdminDAO();
        this.questionDAO = new QuestionDAO();
        this.examDAO = new ExamDAO();
        this.subjectDAO = new SubjectDAO();
        this.studentAnswerDetailDAO = new StudentAnswerDetailDAO();
        this.examService = new ExamService(); // ExamService 内部会初始化它自己的DAO依赖

        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("在线考试系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), LOGIN_PANEL);
        // StudentPanel 和 AdminPanel 将在登录成功后动态创建和添加

        add(mainPanel);
        cardLayout.show(mainPanel, LOGIN_PANEL); // 默认显示登录界面
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(230, 240, 250));
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        JLabel titleLabel = new JLabel("在线考试系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 30, 15);
        loginPanel.add(titleLabel, gbc);
    
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(10, 15, 10, 5);
        JLabel typeLabel = new JLabel("登录类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(typeLabel, gbc);
    
        String[] loginTypes = {"学生", "管理员"};
        loginTypeComboBox = new JComboBox<>(loginTypes);
        loginTypeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15);
        loginPanel.add(loginTypeComboBox, gbc);
    
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(10, 15, 10, 5);
        JLabel idLabel = new JLabel("学号:");
        idLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(idLabel, gbc);
    
        studentIdField = new JTextField(20);
        studentIdField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15);
        loginPanel.add(studentIdField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 10, 5);
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(passwordLabel, gbc);
    
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15);
        loginPanel.add(passwordField, gbc);
    
        JButton loginButton = new JButton("登录");
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.addActionListener(e -> performLogin());
    
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        loginPanel.add(loginButton, gbc);

        // 添加“还没有账号？立即注册”文本和注册按钮
        JLabel registerPromptLabel = new JLabel("还没有账号？");
        registerPromptLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 15, 15, 0);
        loginPanel.add(registerPromptLabel, gbc);

        JButton registerButton = new JButton("立即注册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setForeground(new Color(0, 102, 204));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> showRegisterDialog()); // 添加注册对话框的动作监听器

        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 15);
        loginPanel.add(registerButton, gbc);

        return loginPanel; // 返回创建的JPanel
    }

    private void performLogin() {
        String loginType = (String) loginTypeComboBox.getSelectedItem();
        String id = studentIdField.getText();
        String password = new String(passwordField.getPassword());

        if ("学生".equals(loginType)) {
            Student student = studentDAO.validateLogin(id, password);
            if (student != null) {
                setCurrentStudent(student);
                StudentPanel studentPanel = new StudentPanel(this, student);
                mainPanel.add(studentPanel, STUDENT_PANEL);
                cardLayout.show(mainPanel, STUDENT_PANEL);
            } else {
                JOptionPane.showMessageDialog(this, "学号或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("管理员".equals(loginType)) {
            Admin admin = adminDAO.validateLogin(id, password);
            if (admin != null) {
                setCurrentAdmin(admin);
                AdminPanel adminPanel = new AdminPanel(this, admin);
                mainPanel.add(adminPanel, ADMIN_PANEL);
                cardLayout.show(mainPanel, ADMIN_PANEL);
                studentIdField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void logout() { // 改为 public，以便子面板调用
        setCurrentStudent(null);
        setCurrentAdmin(null);
        // 移除旧的学生/管理员面板，以防重复添加或状态混乱 (可选，但推荐)
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof StudentPanel || comp instanceof AdminPanel) {
                mainPanel.remove(comp);
            }
        }
        cardLayout.show(mainPanel, LOGIN_PANEL);
        studentIdField.setText(""); // 清空登录界面的输入框
        passwordField.setText("");
        loginTypeComboBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    // Getter 方法，以便子面板可以访问 CardLayout 和 Services/DAOs
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "学生注册", true);
        registerDialog.setSize(400, 300);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 学号
        gbc.gridx = 0; gbc.gridy = 0;
        registerDialog.add(new JLabel("学号:"), gbc);
        JTextField regStudentIdField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        registerDialog.add(regStudentIdField, gbc);

        // 姓名
        gbc.gridx = 0; gbc.gridy = 1;
        registerDialog.add(new JLabel("姓名:"), gbc);
        JTextField regNameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        registerDialog.add(regNameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 2;
        registerDialog.add(new JLabel("密码:"), gbc);
        JPasswordField regPasswordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        registerDialog.add(regPasswordField, gbc);

        // 确认密码
        gbc.gridx = 0; gbc.gridy = 3;
        registerDialog.add(new JLabel("确认密码:"), gbc);
        JPasswordField regConfirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        registerDialog.add(regConfirmPasswordField, gbc);

        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(e -> {
            String studentId = regStudentIdField.getText();
            String name = regNameField.getText();
            String password = new String(regPasswordField.getPassword());
            String confirmPassword = new String(regConfirmPasswordField.getPassword());

            if (studentId.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "所有字段都不能为空！", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "两次输入的密码不一致！", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 检查学号是否已存在
            if (studentDAO.findByStudentId(studentId) != null) {
                JOptionPane.showMessageDialog(registerDialog, "学号已存在，请更换！", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student newStudent = new Student(studentId, name, password);
            if (studentDAO.addStudent(newStudent)) {
                JOptionPane.showMessageDialog(registerDialog, "注册成功！请使用学号和密码登录。", "注册成功", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose(); // 关闭注册对话框
            } else {
                JOptionPane.showMessageDialog(registerDialog, "注册失败，请稍后再试。", "注册失败", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerDialog.add(registerButton, gbc);

        registerDialog.setVisible(true);
    }

    // 辅助方法，用于创建统一风格的菜单按钮
    public JButton createMenuButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    // Getters for DAOs and Services
    public StudentDAO getStudentDAO() {
        return studentDAO;
    }

    public AdminDAO getAdminDAO() {
        return adminDAO;
    }

    public QuestionDAO getQuestionDAO() {
        return questionDAO;
    }

    public ExamDAO getExamDAO() {
        return examDAO;
    }

    public SubjectDAO getSubjectDAO() {
        return subjectDAO;
    }

    public StudentAnswerDetailDAO getStudentAnswerDetailDAO() {
        return studentAnswerDetailDAO;
    }

    public ExamService getExamService() {
        return examService;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(Admin currentAdmin) {
        this.currentAdmin = currentAdmin;
    }
}
