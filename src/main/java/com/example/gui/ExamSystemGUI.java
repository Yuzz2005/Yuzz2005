package com.example.gui;

import com.example.dao.StudentDAO;
import com.example.model.Student;
import com.example.model.Question;
import com.example.model.ExamRecord;
import com.example.model.QuestionType;
import com.example.service.ExamService;
import com.example.dao.AdminDAO;
import com.example.model.Admin;
import com.example.dao.QuestionDAO;
import com.example.dao.ExamDAO;
import com.example.dao.SubjectDAO;
import com.example.model.Subject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * 考试系统主界面
 */
public class ExamSystemGUI extends JFrame {
    private StudentDAO studentDAO;
    private ExamService examService;
    private Student currentStudent;
    private AdminDAO adminDAO;
    private Admin currentAdmin;
    private QuestionDAO questionDAO;
    private ExamDAO examDAO;
    private SubjectDAO subjectDAO;

    // 界面组件
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // 登录界面组件
    private JTextField studentIdField;
    private JPasswordField passwordField;
    private JComboBox<String> loginTypeComboBox;
    
    // 主菜单界面组件
    private JLabel welcomeLabel;
    
    // 考试界面组件
    private List<Question> currentQuestions;
    private Map<Integer, String> studentAnswers;
    private int currentQuestionIndex;
    private JPanel questionPanel;
    private ButtonGroup answerGroup;
    private JRadioButton optionA, optionB, optionC, optionD;
    private JButton prevButton, nextButton, submitButton;
    private JLabel questionCountLabel;
    
    // 管理员主菜单组件
    private JLabel adminWelcomeLabel;

    // 管理学生界面组件
    private JTextField newStudentIdField;
    private JTextField newStudentNameField;
    private JPasswordField newStudentPasswordField;

    // 管理题目界面组件
    private JTable questionsTable;
    private JScrollPane questionsScrollPane;
    private JTextField questionTextField;
    private JTextField optionAField;
    private JTextField optionBField;
    private JTextField optionCField;
    private JTextField optionDField;
    private JTextField correctAnswerField;
    private JComboBox<String> subjectComboBox;
    private JButton addQuestionButton;
    private JButton updateQuestionButton;
    private JButton deleteQuestionButton;

    // 管理考试记录界面组件
    private JTable examRecordsTable;
    private JScrollPane examRecordsScrollPane;
    private JButton deleteExamRecordButton;

    // 管理科目界面组件
    private JTable subjectsTable;
    private JScrollPane subjectsScrollPane;
    private JTextField subjectNameField;

    public ExamSystemGUI() {
        this.studentDAO = new StudentDAO();
        this.examService = new ExamService();
        this.studentAnswers = new HashMap<>();
        this.adminDAO = new AdminDAO();
        this.questionDAO = new QuestionDAO();
        this.examDAO = new ExamDAO();
        this.subjectDAO = new SubjectDAO();

        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("在线考试系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // 创建各个界面
        createLoginPanel();
        createMainMenuPanel();
        createExamPanel();
        createResultPanel();
        createHistoryPanel();
        createAdminMenuPanel();
        createManageStudentsPanel();
        createManageQuestionsPanel();
        createManageExamsPanel();
        createManageSubjectsPanel(); // 添加管理科目面板的创建
        // 在initializeGUI方法中
mainPanel.add(createExamPanel(), "EXAM");

        add(mainPanel);
        
        // 显示登录界面
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    /**
     * 创建登录界面
     */
    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(230, 240, 250)); // 浅蓝色背景
    
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // 增加间距
        gbc.anchor = GridBagConstraints.CENTER; // 居中对齐
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
    
        // 标题
        JLabel titleLabel = new JLabel("在线考试系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 30)); // 增大字体
        titleLabel.setForeground(new Color(0, 51, 102)); // 深蓝色字体
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 30, 15); // 标题上下间距
        loginPanel.add(titleLabel, gbc);
    
        // 登录类型选择
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(10, 15, 10, 5); // 调整间距
        JLabel typeLabel = new JLabel("登录类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(typeLabel, gbc);
    
        String[] loginTypes = {"学生", "管理员"};
        loginTypeComboBox = new JComboBox<>(loginTypes);
        loginTypeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15); // 调整间距
        loginPanel.add(loginTypeComboBox, gbc);
    
        // 学号/用户名输入
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(10, 15, 10, 5); // 调整间距
        JLabel idLabel = new JLabel("学号:");
        idLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(idLabel, gbc);
    
        studentIdField = new JTextField(20); // 增加宽度
        studentIdField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15); // 调整间距
        loginPanel.add(studentIdField, gbc);
    
        // 密码输入
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 10, 5); // 调整间距
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        loginPanel.add(passwordLabel, gbc);
    
        passwordField = new JPasswordField(20); // 增加宽度
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 5, 10, 15); // 调整间距
        loginPanel.add(passwordField, gbc);
    
        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setBackground(new Color(0, 102, 204)); // 蓝色按钮
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 18)); // 增大字体
        loginButton.setFocusPainted(false); // 移除焦点框
        loginButton.setBorderPainted(false); // 移除边框
        loginButton.setOpaque(true);
        loginButton.addActionListener(e -> performLogin());
    
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15); // 调整间距
        loginPanel.add(loginButton, gbc);
    
        mainPanel.add(loginPanel, "LOGIN");
    }
    
    /**
     * 创建主菜单界面
     */
    private void createMainMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(248, 248, 255));
        
        // 顶部欢迎信息
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(248, 248, 255));
        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(welcomeLabel);
        
        JButton logoutButton = new JButton("退出登录");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
        
        menuPanel.add(topPanel, BorderLayout.NORTH);
        
        // 中央功能按钮
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // 开始考试按钮
        JButton startExamButton = createMenuButton("开始考试", new Color(34, 139, 34));
        startExamButton.addActionListener(e -> showSubjectSelection());
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(startExamButton, gbc);
        
        // 查看成绩按钮
        JButton viewScoresButton = createMenuButton("查看成绩", new Color(30, 144, 255));
        viewScoresButton.addActionListener(e -> showExamHistory());
        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(viewScoresButton, gbc);
        
        menuPanel.add(centerPanel, BorderLayout.CENTER);
        
        mainPanel.add(menuPanel, "MENU");
    }
    
    /**
     * 创建管理员主菜单界面
     */
    private void createAdminMenuPanel() {
        JPanel adminMenuPanel = new JPanel(new BorderLayout());
        adminMenuPanel.setBackground(new Color(248, 248, 255));
    
        // 顶部欢迎信息
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(248, 248, 255));
        adminWelcomeLabel = new JLabel();
        adminWelcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(adminWelcomeLabel);
    
        JButton logoutButton = new JButton("退出登录");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
    
        adminMenuPanel.add(topPanel, BorderLayout.NORTH);
    
        // 中央功能按钮
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
    
        // 管理学生按钮
        JButton manageStudentsButton = createMenuButton("管理学生", new Color(30, 144, 255));
        manageStudentsButton.addActionListener(e -> cardLayout.show(mainPanel, "MANAGE_STUDENTS"));
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(manageStudentsButton, gbc);
    
        // 管理题目按钮
        JButton manageQuestionsButton = createMenuButton("管理题目", new Color(50, 205, 50));
        manageQuestionsButton.addActionListener(e -> showManageQuestionsPanel());
        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(manageQuestionsButton, gbc);
    
        // 管理考试按钮
        JButton manageExamsButton = createMenuButton("管理考试", new Color(255, 140, 0));
        manageExamsButton.addActionListener(e -> showManageExamsPanel());
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(manageExamsButton, gbc);
    
        // 管理科目按钮
        JButton manageSubjectsButton = createMenuButton("管理科目", new Color(128, 0, 128)); // 紫色按钮
        manageSubjectsButton.addActionListener(e -> showManageSubjectsPanel());
        gbc.gridx = 1; gbc.gridy = 1;
        centerPanel.add(manageSubjectsButton, gbc);

        adminMenuPanel.add(centerPanel, BorderLayout.CENTER);
    
        mainPanel.add(adminMenuPanel, "ADMIN_MENU");
    }
    
    /**
     * 创建考试面板
     */
    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 顶部信息面板
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        questionCountLabel = new JLabel();
        questionCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoPanel.add(questionCountLabel);
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // 题目显示区域
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 底部按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        prevButton = new JButton("上一题");
        nextButton = new JButton("下一题");
        submitButton = new JButton("提交答案");
        
        prevButton.addActionListener(e -> showPreviousQuestion());
        nextButton.addActionListener(e -> showNextQuestion());
        submitButton.addActionListener(e -> submitExam());
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(submitButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * 显示题目
     */
    private void displayQuestion(Question question) {
        questionPanel.removeAll();
        
        // 题目类型和文本
        JLabel typeLabel = new JLabel("题目类型：" + question.getType().getDescription());
        typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        questionPanel.add(typeLabel);
        questionPanel.add(Box.createVerticalStrut(10));
        
        JLabel questionLabel = new JLabel("<html><body style='width: 500px'>" + 
            question.getQuestionText() + "</body></html>");
        questionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        questionPanel.add(questionLabel);
        questionPanel.add(Box.createVerticalStrut(20));
        
        // 根据题目类型显示不同的答案输入方式
        switch (question.getType()) {
            case SINGLE_CHOICE:
                displaySingleChoiceQuestion(question);
                break;
            case MULTIPLE_CHOICE:
                displayMultipleChoiceQuestion(question);
                break;
            case FILL_BLANK:
                displayFillBlankQuestion(question);
                break;
        }
        
        // 更新题目计数
        questionCountLabel.setText(String.format("第 %d/%d 题", currentQuestionIndex + 1, currentQuestions.size()));
        
        // 更新按钮状态
        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < currentQuestions.size() - 1);
        
        questionPanel.revalidate();
        questionPanel.repaint();
    }

    /**
     * 显示单选题
     */
    private void displaySingleChoiceQuestion(Question question) {
        ButtonGroup group = new ButtonGroup();
        JRadioButton[] options = new JRadioButton[4];
        
        String[] optionTexts = {
            question.getOptionA(),
            question.getOptionB(),
            question.getOptionC(),
            question.getOptionD()
        };
        
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton(optionTexts[i]);
            options[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            group.add(options[i]);
            questionPanel.add(options[i]);
            questionPanel.add(Box.createVerticalStrut(10));
        }
        
        // 恢复之前的答案
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null) {
            for (JRadioButton option : options) {
                if (option.getText().equals(previousAnswer)) {
                    option.setSelected(true);
                    break;
                }
            }
        }
    }

    /**
     * 显示多选题
     */
    private void displayMultipleChoiceQuestion(Question question) {
        JCheckBox[] options = new JCheckBox[4];
        
        String[] optionTexts = {
            question.getOptionA(),
            question.getOptionB(),
            question.getOptionC(),
            question.getOptionD()
        };
        
        for (int i = 0; i < 4; i++) {
            options[i] = new JCheckBox(optionTexts[i]);
            options[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            questionPanel.add(options[i]);
            questionPanel.add(Box.createVerticalStrut(10));
        }
        
        // 恢复之前的答案
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null) {
            String[] selectedOptions = previousAnswer.split(",");
            for (String selected : selectedOptions) {
                for (JCheckBox option : options) {
                    if (option.getText().equals(selected.trim())) {
                        option.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 显示填空题
     */
    private void displayFillBlankQuestion(Question question) {
        JTextField answerField = new JTextField(30);
        answerField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        answerField.setMaximumSize(new Dimension(400, 30));
        
        // 恢复之前的答案
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null) {
            answerField.setText(previousAnswer);
        }
        
        questionPanel.add(answerField);
    }

    /**
     * 显示上一题
     */
    private void showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion(currentQuestions.get(currentQuestionIndex));
        }
    }

    /**
     * 显示下一题
     */
    private void showNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion(currentQuestions.get(currentQuestionIndex));
        }
    }

    /**
     * 保存当前题目的答案
     */
    private void saveCurrentAnswer() {
        Question currentQuestion = currentQuestions.get(currentQuestionIndex);
        String answer = "";
        
        switch (currentQuestion.getType()) {
            case SINGLE_CHOICE:
                for (Component comp : questionPanel.getComponents()) {
                    if (comp instanceof JRadioButton) {
                        JRadioButton radio = (JRadioButton) comp;
                        if (radio.isSelected()) {
                            answer = radio.getText();
                            break;
                        }
                    }
                }
                break;
                
            case MULTIPLE_CHOICE:
                List<String> selectedAnswers = new ArrayList<>();
                for (Component comp : questionPanel.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) comp;
                        if (checkBox.isSelected()) {
                            selectedAnswers.add(checkBox.getText());
                        }
                    }
                }
                answer = String.join(",", selectedAnswers);
                break;
                
            case FILL_BLANK:
                for (Component comp : questionPanel.getComponents()) {
                    if (comp instanceof JTextField) {
                        JTextField textField = (JTextField) comp;
                        answer = textField.getText().trim();
                        break;
                    }
                }
                break;
        }
        
        studentAnswers.put(currentQuestion.getId(), answer);
    }

    /**
     * 提交考试
     */
    private void submitExam() {
        saveCurrentAnswer(); // 先保存当前题目的答案
        int score = 0;
        int totalQuestions = currentQuestions.size();
        for (Question question : currentQuestions) {
            String userAnswer = studentAnswers.get(question.getId());
            if (userAnswer == null) userAnswer = "";
            switch (question.getType()) {
                case SINGLE_CHOICE:
                    String correctOption = getOptionByLetter(question, question.getCorrectAnswer());
                    if (userAnswer.equals(correctOption)) {
                        score++;
                    }
                    break;
                case MULTIPLE_CHOICE:
                    List<String> correctAnswers = question.getCorrectAnswers();
                    List<String> userAnswers = Arrays.asList(userAnswer.split(","));
                    if (userAnswers.size() == correctAnswers.size() && userAnswers.containsAll(correctAnswers)) {
                        score++;
                    }
                    break;
                case FILL_BLANK:
                    if (userAnswer.trim().equalsIgnoreCase(question.getFillBlankAnswer() == null ? "" : question.getFillBlankAnswer().trim())) {
                        score++;
                    }
                    break;
            }
        }
        // 保存考试记录
        ExamRecord record = new ExamRecord(
            currentStudent.getStudentId(),
            currentQuestions.get(0).getSubject(),
            score,
            totalQuestions
        );
        record.setExamDate(new java.sql.Timestamp(new java.util.Date().getTime()));
        if (examDAO.saveExamRecord(record)) {
            JOptionPane.showMessageDialog(this,
                String.format("考试完成！得分：%d/%d (%.1f%%)",
                    score, totalQuestions, (double)score/totalQuestions*100),
                "考试结果",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "保存考试记录失败！",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
        // 返回主界面
        cardLayout.show(mainPanel, "MENU");
    }
    
    /**
     * 执行登录
     */
    private void performLogin() {
        String loginType = (String) loginTypeComboBox.getSelectedItem();
        String id = studentIdField.getText();
        String password = new String(passwordField.getPassword());

        if ("学生".equals(loginType)) {
            currentStudent = studentDAO.validateLogin(id, password);
            if (currentStudent != null) {
                welcomeLabel.setText("欢迎学生: " + currentStudent.getName());
                cardLayout.show(mainPanel, "MENU");
            } else {
                JOptionPane.showMessageDialog(this, "学号或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("管理员".equals(loginType)) {
            currentAdmin = adminDAO.validateLogin(id, password);
            if (currentAdmin != null) {
                adminWelcomeLabel.setText("欢迎管理员: " + currentAdmin.getUsername());
                cardLayout.show(mainPanel, "ADMIN_MENU");
                // 清空登录表单
                studentIdField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 退出登录
     */
    private void logout() {
        currentStudent = null;
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    /**
 * 显示科目选择对话框并开始考试
     */
    private void showSubjectSelection() {
        List<String> subjects = examService.getAvailableSubjects();
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "暂无可用的考试科目！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] subjectArray = subjects.toArray(new String[0]);
        String selectedSubject = (String) JOptionPane.showInputDialog(
            this,
            "请选择考试科目:",
            "选择科目",
            JOptionPane.QUESTION_MESSAGE,
            null,
            subjectArray,
            subjectArray[0]
        );
        
        if (selectedSubject != null) {
            startExam(selectedSubject);
        }
    }
    
    /**
     * 开始考试
     */
    private void startExam(String subject) {
        int questionCount = examService.getQuestionCount(subject);
    System.out.println("科目总题数：" + questionCount); // 添加日志
        
    currentQuestions = examService.startExam(subject, questionCount);
    System.out.println("加载到题目数：" + currentQuestions.size()); // 添加日志
    
        if (currentQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "该科目暂无题目！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
    // 重置考试状态
        studentAnswers.clear();
        currentQuestionIndex = 0;
        
    // 显示第一题
    displayQuestion(currentQuestions.get(currentQuestionIndex));
    
    // 切换到考试面板
    System.out.println("准备切换到考试面板"); // 添加日志
        cardLayout.show(mainPanel, "EXAM");
    System.out.println("已切换到考试面板"); // 添加日志
    }
    
    /**
     * 显示考试历史
     */
    private void showExamHistory() {
        List<ExamRecord> records = examService.getStudentExamHistory(currentStudent.getStudentId());
        
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "暂无考试记录！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 创建表格显示历史记录
        String[] columnNames = {"科目", "得分", "总分", "百分比", "等级", "考试时间"};
        Object[][] data = new Object[records.size()][6];
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < records.size(); i++) {
            ExamRecord record = records.get(i);
            data[i][0] = record.getSubject();
            data[i][1] = record.getScore();
            data[i][2] = record.getTotalQuestions();
            data[i][3] = String.format("%.1f%%", record.getScorePercentage());
            
            double percentage = record.getScorePercentage();
            if (percentage >= 90) data[i][4] = "优秀";
            else if (percentage >= 80) data[i][4] = "良好";
            else if (percentage >= 70) data[i][4] = "中等";
            else if (percentage >= 60) data[i][4] = "及格";
            else data[i][4] = "不及格";
            
            data[i][5] = sdf.format(record.getExamDate());
        }
        
        JTable table = new JTable(data, columnNames);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "考试历史记录",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ExamSystemGUI().setVisible(true);
        });
    }

    /**
     * 创建管理学生界面
     */
    private void createManageStudentsPanel() {
        JPanel manageStudentsPanel = new JPanel(new GridBagLayout());
        manageStudentsPanel.setBackground(new Color(240, 255, 240)); // 浅绿色背景

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 顶部面板（包含返回按钮和标题）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 255, 240));

        // 返回按钮
        JButton backButton = new JButton("返回管理员菜单");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN_MENU"));
        topPanel.add(backButton, BorderLayout.WEST);

        // 标题
        JLabel titleLabel = new JLabel("管理学生", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 0)); // 深绿色字体
        topPanel.add(titleLabel, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 10, 20, 10);
        manageStudentsPanel.add(topPanel, gbc);

        // 输入字段
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.gridx = 0; gbc.gridy = 1;
        manageStudentsPanel.add(new JLabel("学号:"), gbc);
        newStudentIdField = new JTextField(20);
        gbc.gridx = 1;
        manageStudentsPanel.add(newStudentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        manageStudentsPanel.add(new JLabel("姓名:"), gbc);
        newStudentNameField = new JTextField(20);
        gbc.gridx = 1;
        manageStudentsPanel.add(newStudentNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        manageStudentsPanel.add(new JLabel("密码:"), gbc);
        newStudentPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        manageStudentsPanel.add(newStudentPasswordField, gbc);

        // 添加学生按钮
        JButton addStudentButton = new JButton("添加学生");
        addStudentButton.addActionListener(e -> addStudent());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        manageStudentsPanel.add(addStudentButton, gbc);

        mainPanel.add(manageStudentsPanel, "MANAGE_STUDENTS");
    }

    /**
     * 添加学生逻辑
     */
    private void addStudent() {
        String studentId = newStudentIdField.getText().trim();
        String name = newStudentNameField.getText().trim();
        String password = new String(newStudentPasswordField.getPassword()).trim();

        if (studentId.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "学号、姓名和密码都不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查学号是否已存在
        if (studentDAO.findByStudentId(studentId) != null) {
            JOptionPane.showMessageDialog(this, "学号已存在！", "添加失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student newStudent = new Student(0, studentId, name, password); // ID 会在数据库中自增
        boolean success = studentDAO.addStudent(newStudent);

        if (success) {
            JOptionPane.showMessageDialog(this, "学生添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            // 清空输入框
            newStudentIdField.setText("");
            newStudentNameField.setText("");
            newStudentPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "学生添加失败，请检查输入或数据库连接！", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 创建管理题目界面
     */
    private void createManageQuestionsPanel() {
        JPanel manageQuestionsPanel = new JPanel(new BorderLayout());
        manageQuestionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionsTable = new JTable(); 
        questionsScrollPane = new JScrollPane(questionsTable);
        manageQuestionsPanel.add(questionsScrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel typeLabel = new JLabel("题目类型:");
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(typeLabel, gbc);
        JComboBox<QuestionType> typeComboBox = new JComboBox<>(QuestionType.values());
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; formPanel.add(typeComboBox, gbc);
        gbc.gridwidth = 1;

        JLabel questionLabel = new JLabel("题目内容:");
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(questionLabel, gbc);
        questionTextField = new JTextField(30);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; formPanel.add(questionTextField, gbc);
        gbc.gridwidth = 1;

        String[] optionLabelTexts = {"选项A:", "选项B:", "选项C:", "选项D:"};
        JLabel[] optionJLabels = new JLabel[optionLabelTexts.length]; // Array to store JLabels

        optionAField = new JTextField(20);
        optionBField = new JTextField(20);
        optionCField = new JTextField(20);
        optionDField = new JTextField(20);
        JTextField[] localOptionFields = {optionAField, optionBField, optionCField, optionDField};

        for (int i = 0; i < optionLabelTexts.length; i++) {
            optionJLabels[i] = new JLabel(optionLabelTexts[i]); // Create and store JLabel
            gbc.gridx = 0; gbc.gridy = 2 + i; formPanel.add(optionJLabels[i], gbc);
            gbc.gridx = 1; gbc.gridy = 2 + i; gbc.gridwidth = 2; formPanel.add(localOptionFields[i], gbc);
            gbc.gridwidth = 1;
        }

        JLabel correctAnswerLabel = new JLabel("正确答案:");
        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(correctAnswerLabel, gbc);
        correctAnswerField = new JTextField(10); 
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2; formPanel.add(correctAnswerField, gbc);
        gbc.gridwidth = 1;

        JLabel fillBlankAnswerLabel = new JLabel("填空答案:");
        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(fillBlankAnswerLabel, gbc);
        JTextField fillBlankAnswerField = new JTextField(20);
        fillBlankAnswerField.setName("fillBlankAnswerField");
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2; formPanel.add(fillBlankAnswerField, gbc);
        gbc.gridwidth = 1;

        JLabel subjectLabel = new JLabel("科目:");
        gbc.gridx = 0; gbc.gridy = 8; formPanel.add(subjectLabel, gbc);
        
        // 创建科目下拉框并填充数据
        subjectComboBox = new JComboBox<>();
        updateSubjectComboBox(); // 更新科目下拉框的选项
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2; formPanel.add(subjectComboBox, gbc);
        gbc.gridwidth = 1;
        
        typeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                QuestionType selectedType = (QuestionType) e.getItem();
                boolean isChoice = selectedType == QuestionType.SINGLE_CHOICE || selectedType == QuestionType.MULTIPLE_CHOICE;
                boolean isFillBlank = selectedType == QuestionType.FILL_BLANK;

                for (int i = 0; i < localOptionFields.length; i++) {
                    localOptionFields[i].setVisible(isChoice);
                    optionJLabels[i].setVisible(isChoice); // Use the stored JLabel array
                }
                correctAnswerLabel.setVisible(isChoice);
                correctAnswerField.setVisible(isChoice);
                
                fillBlankAnswerLabel.setVisible(isFillBlank);
                fillBlankAnswerField.setVisible(isFillBlank);
                
                if(isChoice) {
                    correctAnswerField.setToolTipText(selectedType == QuestionType.SINGLE_CHOICE ? "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B)");
                }
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addQuestionButton = new JButton("添加题目");
        updateQuestionButton = new JButton("更新题目");
        deleteQuestionButton = new JButton("删除题目");
        JButton backButton = new JButton("返回管理员菜单");

        buttonPanel.add(addQuestionButton);
        buttonPanel.add(updateQuestionButton);
        buttonPanel.add(deleteQuestionButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 3; 
        gbc.anchor = GridBagConstraints.CENTER; 
        formPanel.add(buttonPanel, gbc);

        manageQuestionsPanel.add(formPanel, BorderLayout.SOUTH);

        addQuestionButton.addActionListener(e -> showAddQuestionDialog());
        updateQuestionButton.addActionListener(e -> updateQuestion((QuestionType) typeComboBox.getSelectedItem(), fillBlankAnswerField));
        deleteQuestionButton.addActionListener(e -> deleteQuestion());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN_MENU"));

        questionsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && questionsTable.getSelectedRow() != -1) {
                populateQuestionForm(typeComboBox, fillBlankAnswerField);
            }
        });
        
        typeComboBox.setSelectedItem(QuestionType.SINGLE_CHOICE); 
        QuestionType initialType = (QuestionType) typeComboBox.getSelectedItem();
        boolean isChoiceInitial = initialType == QuestionType.SINGLE_CHOICE || initialType == QuestionType.MULTIPLE_CHOICE;
        boolean isFillBlankInitial = initialType == QuestionType.FILL_BLANK;

        for (int i = 0; i < localOptionFields.length; i++) {
            localOptionFields[i].setVisible(isChoiceInitial);
            optionJLabels[i].setVisible(isChoiceInitial); // Use the stored JLabel array for initialization
        }
        correctAnswerLabel.setVisible(isChoiceInitial);
        correctAnswerField.setVisible(isChoiceInitial);
        if(isChoiceInitial) {
            correctAnswerField.setToolTipText(initialType == QuestionType.SINGLE_CHOICE ? "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B)");
        }

        fillBlankAnswerLabel.setVisible(isFillBlankInitial);
        fillBlankAnswerField.setVisible(isFillBlankInitial);

        mainPanel.add(manageQuestionsPanel, "MANAGE_QUESTIONS");
    }

    /**
     * 更新科目下拉框的选项
     */
    private void updateSubjectComboBox() {
        subjectComboBox.removeAllItems();
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            subjectComboBox.addItem(subject.getName());
        }
    }

    /**
     * 填充表单以便编辑选中的题目
     */
    private void populateQuestionForm(JComboBox<QuestionType> typeComboBox, JTextField fillBlankAnswerField) {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        Question question = questionDAO.getQuestionById(questionId);

        if (question == null) {
            JOptionPane.showMessageDialog(this, "无法加载题目信息。", "错误", JOptionPane.ERROR_MESSAGE);
            clearQuestionForm(typeComboBox, fillBlankAnswerField);
            return;
        }

        questionTextField.setText(question.getQuestionText());
        subjectComboBox.setSelectedItem(question.getSubject()); // 设置科目下拉框的选中项
        typeComboBox.setSelectedItem(question.getType());

        // Listener will handle visibility, now just set values
        if (question.getType() == QuestionType.SINGLE_CHOICE || question.getType() == QuestionType.MULTIPLE_CHOICE) {
            optionAField.setText(question.getOptionA());
            optionBField.setText(question.getOptionB());
            optionCField.setText(question.getOptionC());
            optionDField.setText(question.getOptionD());
            if (question.getType() == QuestionType.SINGLE_CHOICE) {
                correctAnswerField.setText(question.getCorrectAnswer());
            } else { // MULTIPLE_CHOICE
                correctAnswerField.setText(question.getCorrectAnswers() != null ? String.join(",", question.getCorrectAnswers()) : "");
            }
        } else if (question.getType() == QuestionType.FILL_BLANK) {
            fillBlankAnswerField.setText(question.getFillBlankAnswer());
        }
    }
    
    /**
     * 清空题目表单
     */
    private void clearQuestionForm(JComboBox<QuestionType> typeComboBox, JTextField fillBlankAnswerField) {
        questionTextField.setText("");
        optionAField.setText("");
        optionBField.setText("");
        optionCField.setText("");
        optionDField.setText("");
        correctAnswerField.setText("");
        if (subjectComboBox.getItemCount() > 0) {
            subjectComboBox.setSelectedIndex(0); // 重置为第一个选项
        }
        fillBlankAnswerField.setText("");
        typeComboBox.setSelectedItem(QuestionType.SINGLE_CHOICE);
        questionsTable.clearSelection();
    }

    /**
     * 显示添加题目的对话框
     */
    private void showAddQuestionDialog() {
        JDialog addDialog = new JDialog(this, "添加新题目", true); // true 表示模态对话框
        addDialog.setSize(600, 500);
        addDialog.setLocationRelativeTo(this);
        addDialog.setLayout(new BorderLayout());

        JPanel dialogFormPanel = new JPanel(new GridBagLayout());
        dialogFormPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 题目类型
        JLabel DtypeLabel = new JLabel("题目类型:");
        gbc.gridx = 0; gbc.gridy = 0; dialogFormPanel.add(DtypeLabel, gbc);
        JComboBox<QuestionType> DtypeComboBox = new JComboBox<>(QuestionType.values());
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2; dialogFormPanel.add(DtypeComboBox, gbc);
        gbc.gridwidth = 1;

        // 题目内容
        JLabel DquestionLabel = new JLabel("题目内容:");
        gbc.gridx = 0; gbc.gridy = 1; dialogFormPanel.add(DquestionLabel, gbc);
        JTextField DquestionTextField = new JTextField(30);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; dialogFormPanel.add(DquestionTextField, gbc);
        gbc.gridwidth = 1;

        // 选项字段和标签
        JLabel[] DoptionJLabels = new JLabel[4];
        JTextField[] DlocalOptionFields = new JTextField[4];
        String[] DoptionLabelTexts = {"选项A:", "选项B:", "选项C:", "选项D:"};
        for (int i = 0; i < DoptionLabelTexts.length; i++) {
            DoptionJLabels[i] = new JLabel(DoptionLabelTexts[i]);
            DlocalOptionFields[i] = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 2 + i; dialogFormPanel.add(DoptionJLabels[i], gbc);
            gbc.gridx = 1; gbc.gridy = 2 + i; gbc.gridwidth = 2; dialogFormPanel.add(DlocalOptionFields[i], gbc);
            gbc.gridwidth = 1;
        }

        // 正确答案
        JLabel DcorrectAnswerLabel = new JLabel("正确答案:");
        gbc.gridx = 0; gbc.gridy = 6; dialogFormPanel.add(DcorrectAnswerLabel, gbc);
        JTextField DcorrectAnswerField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2; dialogFormPanel.add(DcorrectAnswerField, gbc);
        gbc.gridwidth = 1;

        // 填空答案
        JLabel DfillBlankAnswerLabel = new JLabel("填空答案:");
        gbc.gridx = 0; gbc.gridy = 7; dialogFormPanel.add(DfillBlankAnswerLabel, gbc);
        JTextField DfillBlankAnswerField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 7; gbc.gridwidth = 2; dialogFormPanel.add(DfillBlankAnswerField, gbc);
        gbc.gridwidth = 1;

        // 科目
        JLabel DsubjectLabel = new JLabel("科目:");
        gbc.gridx = 0; gbc.gridy = 8; dialogFormPanel.add(DsubjectLabel, gbc);
        JComboBox<String> DsubjectComboBox = new JComboBox<>();
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            DsubjectComboBox.addItem(subject.getName());
        }
        gbc.gridx = 1; gbc.gridy = 8; gbc.gridwidth = 2; dialogFormPanel.add(DsubjectComboBox, gbc);
        gbc.gridwidth = 1;

        // 根据题目类型显示/隐藏字段的逻辑
        DtypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                QuestionType selectedType = (QuestionType) e.getItem();
                boolean isChoice = selectedType == QuestionType.SINGLE_CHOICE || selectedType == QuestionType.MULTIPLE_CHOICE;
                boolean isFillBlank = selectedType == QuestionType.FILL_BLANK;

                for (int i = 0; i < DlocalOptionFields.length; i++) {
                    DlocalOptionFields[i].setVisible(isChoice);
                    DoptionJLabels[i].setVisible(isChoice);
                }
                DcorrectAnswerLabel.setVisible(isChoice);
                DcorrectAnswerField.setVisible(isChoice);
                DfillBlankAnswerLabel.setVisible(isFillBlank);
                DfillBlankAnswerField.setVisible(isFillBlank);

                if (isChoice) {
                    DcorrectAnswerField.setToolTipText(selectedType == QuestionType.SINGLE_CHOICE ? "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B)");
                }
            }
        });
        // 初始化时触发一次，以确保基于默认选择正确设置了可见性
        DtypeComboBox.setSelectedItem(QuestionType.SINGLE_CHOICE);
        // Manually trigger the listener logic for the initial state
        QuestionType initialType = (QuestionType) DtypeComboBox.getSelectedItem();
        boolean isChoiceInitial = initialType == QuestionType.SINGLE_CHOICE || initialType == QuestionType.MULTIPLE_CHOICE;
        boolean isFillBlankInitial = initialType == QuestionType.FILL_BLANK;
        for (int i = 0; i < DlocalOptionFields.length; i++) {
            DlocalOptionFields[i].setVisible(isChoiceInitial);
            DoptionJLabels[i].setVisible(isChoiceInitial);
        }
        DcorrectAnswerLabel.setVisible(isChoiceInitial);
        DcorrectAnswerField.setVisible(isChoiceInitial);
        if(isChoiceInitial) {
            DcorrectAnswerField.setToolTipText(initialType == QuestionType.SINGLE_CHOICE ? "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B)");
        }
        DfillBlankAnswerLabel.setVisible(isFillBlankInitial);
        DfillBlankAnswerField.setVisible(isFillBlankInitial);


        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmAddButton = new JButton("确认添加");
        JButton cancelButton = new JButton("取消");
        buttonPanel.add(confirmAddButton);
        buttonPanel.add(cancelButton);

        confirmAddButton.addActionListener(e -> {
            String questionText = DquestionTextField.getText().trim();
            String subjectName = (String) DsubjectComboBox.getSelectedItem();
            QuestionType type = (QuestionType) DtypeComboBox.getSelectedItem();

            if (questionText.isEmpty() || subjectName == null) {
                JOptionPane.showMessageDialog(addDialog, "题目内容和科目不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Question newQuestion = new Question();
            newQuestion.setQuestionText(questionText);
            newQuestion.setSubject(subjectName);
            newQuestion.setType(type);

            if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
                String optionA = DlocalOptionFields[0].getText().trim();
                String optionB = DlocalOptionFields[1].getText().trim();
                String optionC = DlocalOptionFields[2].getText().trim();
                String optionD = DlocalOptionFields[3].getText().trim();
                String correctAnswerText = DcorrectAnswerField.getText().trim();

                if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || correctAnswerText.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "选择题的选项和答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newQuestion.setOptionA(optionA);
                newQuestion.setOptionB(optionB);
                newQuestion.setOptionC(optionC);
                newQuestion.setOptionD(optionD);

                if (type == QuestionType.SINGLE_CHOICE) {
                    if (!correctAnswerText.matches("[A-D]")) {
                        JOptionPane.showMessageDialog(addDialog, "单选题正确答案必须是 A, B, C 或 D。", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    newQuestion.setCorrectAnswer(correctAnswerText);
                } else { // MULTIPLE_CHOICE
                    if (!correctAnswerText.matches("([A-D](,)?)+")) {
                        JOptionPane.showMessageDialog(addDialog, "多选题正确答案必须是 A, B, C, D 的组合，用逗号分隔。", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    newQuestion.setCorrectAnswers(Arrays.asList(correctAnswerText.split(",")));
                }
            } else if (type == QuestionType.FILL_BLANK) {
                String fillAnswer = DfillBlankAnswerField.getText().trim();
                if (fillAnswer.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "填空题答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newQuestion.setFillBlankAnswer(fillAnswer);
            }

            boolean success = questionDAO.addQuestion(newQuestion);
            if (success) {
                JOptionPane.showMessageDialog(addDialog, "题目添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                addDialog.dispose(); // 关闭对话框
                loadQuestionsTable(); // 刷新主界面的表格
                // 清空主界面的编辑表单
                JPanel mainFormPanel = (JPanel) questionTextField.getParent(); 
                JComboBox<QuestionType> mainTypeComboBox = null;
                JTextField mainFillBlankAnswerField = null;
                // 确保在 createManageQuestionsPanel 中 fillBlankAnswerField 被设置了 name
                for(Component comp : mainFormPanel.getComponents()){
                    if(comp instanceof JComboBox) {
                        // 确保这是正确的类型组合框
                        if (((JComboBox<?>)comp).getItemCount() > 0 && ((JComboBox<?>)comp).getItemAt(0) instanceof QuestionType) {
                            mainTypeComboBox = (JComboBox<QuestionType>) comp;
                        }
                    }
                    // 通过name查找JTextField，确保name已在创建时设置
                    if(comp instanceof JTextField && "fillBlankAnswerField".equals(comp.getName())){
                        mainFillBlankAnswerField = (JTextField) comp;
                    }
                }
                if(mainTypeComboBox != null){ // mainFillBlankAnswerField 可能是 null，clearQuestionForm 应能处理
                    clearQuestionForm(mainTypeComboBox, mainFillBlankAnswerField);
                }
            } else {
                JOptionPane.showMessageDialog(addDialog, "题目添加失败，请检查输入或数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.add(dialogFormPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }

    /**
     * 创建管理考试记录界面
     */
    private void createManageExamsPanel() {
        JPanel manageExamsPanel = new JPanel(new BorderLayout());
        manageExamsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("管理考试记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        manageExamsPanel.add(titleLabel, BorderLayout.NORTH);

        // Table to display exam records
        String[] columnNames = {"ID", "学生ID", "科目", "分数", "考试日期"};
        examRecordsTable = new JTable(); // Data model will be set later
        examRecordsScrollPane = new JScrollPane(examRecordsTable);
        manageExamsPanel.add(examRecordsScrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        deleteExamRecordButton = new JButton("删除考试记录");
        JButton backButton = new JButton("返回管理员菜单");

        buttonPanel.add(deleteExamRecordButton);
        buttonPanel.add(backButton);

        manageExamsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        deleteExamRecordButton.addActionListener(e -> deleteExamRecord());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN_MENU"));

        mainPanel.add(manageExamsPanel, "MANAGE_EXAMS");
    }

    /**
     * 显示管理考试记录面板并加载数据
     */
    private void showManageExamsPanel() {
        loadExamRecordsTable();
        cardLayout.show(mainPanel, "MANAGE_EXAMS");
    }

    /**
     * 从数据库加载考试记录到表格
     */
    private void loadExamRecordsTable() {
        List<ExamRecord> records = examDAO.getAllExamRecords();
        String[] columnNames = {"ID", "学生ID", "科目", "分数", "考试日期"};
        Object[][] data = new Object[records.size()][5];

        for (int i = 0; i < records.size(); i++) {
            ExamRecord r = records.get(i);
            data[i][0] = r.getId();
            data[i][1] = r.getStudentId();
            data[i][2] = r.getSubject();
            data[i][3] = r.getScore();
            data[i][4] = r.getExamDate();
        }

        examRecordsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    /**
     * 删除选中的考试记录
     */
    private void deleteExamRecord() {
        int selectedRow = examRecordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的考试记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int recordId = (int) examRecordsTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的考试记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = examDAO.deleteExamRecord(recordId);
            if (success) {
                JOptionPane.showMessageDialog(this, "考试记录删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadExamRecordsTable(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "考试记录删除失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 创建管理科目界面
     */
    private void createManageSubjectsPanel() {
        JPanel manageSubjectsPanel = new JPanel(new BorderLayout());
        manageSubjectsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("管理科目", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        manageSubjectsPanel.add(titleLabel, BorderLayout.NORTH);

        // Table to display subjects
        String[] columnNames = {"ID", "科目名称"};
        subjectsTable = new JTable(); // Data model will be set later
        subjectsScrollPane = new JScrollPane(subjectsTable);
        manageSubjectsPanel.add(subjectsScrollPane, BorderLayout.CENTER);

        // Input form for adding subjects
        JPanel formPanel = new JPanel(new FlowLayout());
        JLabel subjectNameLabel = new JLabel("科目名称:");
        subjectNameField = new JTextField(20);
        JButton addSubjectButton = new JButton("添加科目");
        JButton deleteSubjectButton = new JButton("删除科目");

        formPanel.add(subjectNameLabel);
        formPanel.add(subjectNameField);
        formPanel.add(addSubjectButton);
        formPanel.add(deleteSubjectButton);

        manageSubjectsPanel.add(formPanel, BorderLayout.SOUTH);

        // 返回按钮
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("返回管理员菜单");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "ADMIN_MENU"));
        backButtonPanel.add(backButton);
        manageSubjectsPanel.add(backButtonPanel, BorderLayout.NORTH);

        // Add action listeners (implementation will follow)
        addSubjectButton.addActionListener(e -> addSubject());
        deleteSubjectButton.addActionListener(e -> deleteSubject());

        mainPanel.add(manageSubjectsPanel, "MANAGE_SUBJECTS");
    }

    /**
     * 显示管理科目面板并加载数据
     */
    private void showManageSubjectsPanel() {
        loadSubjectsTable();
        cardLayout.show(mainPanel, "MANAGE_SUBJECTS");
    }

    /**
     * 从数据库加载科目到表格
     */
    private void loadSubjectsTable() {
        List<Subject> subjects = subjectDAO.getAllSubjects();
        String[] columnNames = {"ID", "科目名称"};
        Object[][] data = new Object[subjects.size()][2];

        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            data[i][0] = s.getId();
            data[i][1] = s.getName();
        }

        subjectsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    /**
     * 添加科目逻辑
     */
    private void addSubject() {
        String subjectName = subjectNameField.getText().trim();
        if (subjectName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "科目名称不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查科目是否已存在
        if (subjectDAO.findSubjectByName(subjectName) != null) {
            JOptionPane.showMessageDialog(this, "科目已存在！", "添加失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = subjectDAO.addSubject(subjectName);
        if (success) {
            JOptionPane.showMessageDialog(this, "科目添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            subjectNameField.setText("");
            loadSubjectsTable(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "科目添加失败，请检查输入或数据库连接！", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除科目逻辑
     */
    private void deleteSubject() {
        int selectedRow = subjectsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的科目。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int subjectId = (int) subjectsTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的科目吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = subjectDAO.deleteSubject(subjectId);
            if (success) {
                JOptionPane.showMessageDialog(this, "科目删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadSubjectsTable(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "科目删除失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 创建菜单按钮
     */
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        return button;
    }

    /**
     * 创建考试结果面板
     */
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 结果标题
        JLabel titleLabel = new JLabel("考试结果", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 结果内容
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // 分数
        JLabel scoreLabel = new JLabel("得分：0分", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        contentPanel.add(scoreLabel);
        
        // 正确率
        JLabel accuracyLabel = new JLabel("正确率：0%", SwingConstants.CENTER);
        accuracyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        contentPanel.add(accuracyLabel);
        
        // 用时
        JLabel timeLabel = new JLabel("用时：0分钟", SwingConstants.CENTER);
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        contentPanel.add(timeLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // 返回按钮
        JButton backButton = createMenuButton("返回", new Color(51, 122, 183));
        backButton.addActionListener(e -> showMainPanel());
        panel.add(backButton, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * 创建历史记录面板
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel("考试历史记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 历史记录表格
        String[] columnNames = {"考试日期", "科目", "得分", "正确率", "用时"};
        Object[][] data = {}; // 从数据库获取数据
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 返回按钮
        JButton backButton = createMenuButton("返回", new Color(51, 122, 183));
        backButton.addActionListener(e -> showMainPanel());
        panel.add(backButton, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * 显示主面板
     */
    private void showMainPanel() {
        cardLayout.show(mainPanel, "main");
    }

    /**
     * 从数据库加载题目到表格
     */
    private void loadQuestionsTable() {
        List<Question> questions = questionDAO.getAllQuestions();
        String[] columnNames = {"ID", "题目类型", "题目内容", "所属科目"}; // 将"操作"改为"所属科目"
        Object[][] data = new Object[questions.size()][4];

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            data[i][0] = q.getId();
            if (q.getType() != null) {
                data[i][1] = q.getType().getDescription();
            } else {
                data[i][1] = "未知类型";
            }
            data[i][2] = q.getQuestionText();
            data[i][3] = q.getSubject(); // 显示题目所属科目
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置表格不可编辑
            }
        };
        questionsTable.setModel(model);
        
        // 设置表格的选择模式为单行选择
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 添加行点击事件监听器
        questionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 确保只触发一次
                int selectedRow = questionsTable.getSelectedRow();
                if (selectedRow != -1) {
                    int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
                    Question question = questionDAO.getQuestionById(questionId);
                    if (question != null) {
                        // 获取表单组件
                        JPanel formPanel = (JPanel) questionTextField.getParent();
                        JComboBox<QuestionType> typeComboBox = null;
                        JTextField fillBlankAnswerField = null;
                        
                        // 查找所需的组件
                        for (Component comp : formPanel.getComponents()) {
                            if (comp instanceof JComboBox && ((JComboBox<?>) comp).getItemAt(0) instanceof QuestionType) {
                                typeComboBox = (JComboBox<QuestionType>) comp;
                            }
                            if (comp instanceof JTextField && "fillBlankAnswerField".equals(comp.getName())) {
                                fillBlankAnswerField = (JTextField) comp;
                            }
                        }
                        
                        // 填充表单
                        if (typeComboBox != null) {
                            populateQuestionForm(typeComboBox, fillBlankAnswerField);
                        }
                    }
                }
            }
        });
        
        // 设置列宽
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID列
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 题目类型列
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(300); // 题目内容列
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // 所属科目列
    }

    /**
     * 根据选项字母获取选项文本
     */
    private String getOptionByLetter(Question question, String letter) {
        switch (letter) {
            case "A": return question.getOptionA();
            case "B": return question.getOptionB();
            case "C": return question.getOptionC();
            case "D": return question.getOptionD();
            default: return "";
        }
    }

    /**
     * 显示管理题目面板并加载数据
     */
    private void showManageQuestionsPanel() {
        updateSubjectComboBox(); // 在显示面板时更新科目下拉框
        loadQuestionsTable();
        cardLayout.show(mainPanel, "MANAGE_QUESTIONS");
    }

    /**
     * 删除选中的题目
     */
    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的题目。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的题目吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = questionDAO.deleteQuestion(questionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "题目删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                // Correctly get typeComboBox and fillBlankAnswerField to pass to clearQuestionForm
                JPanel formPanel = (JPanel) questionTextField.getParent(); 
                JComboBox<QuestionType> typeComboBox = null;
                JTextField fillBlankAnswerField = null;
                // Iterate to find the components, assuming their relative order or use more robust lookup
                for (Component comp : formPanel.getComponents()) {
                    if (comp instanceof JComboBox) { // Could be more specific if other JComboBoxes exist
                        typeComboBox = (JComboBox<QuestionType>) comp;
                    }
                    if (comp.getName() != null && comp.getName().equals("fillBlankAnswerField")) { // Assuming you set a name
                        fillBlankAnswerField = (JTextField) comp;
                    }
                }
                 if (typeComboBox != null) { // fillBlankAnswerField can be null if not found by name strategy
                    clearQuestionForm(typeComboBox, fillBlankAnswerField); 
                 }
                loadQuestionsTable(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "题目删除失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 更新选中的题目
     */
    private void updateQuestion(QuestionType type, JTextField fillBlankAnswerField) {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要更新的题目。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        Question questionToUpdate = questionDAO.getQuestionById(questionId); 
        if(questionToUpdate == null){
            JOptionPane.showMessageDialog(this, "无法找到要更新的题目。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String questionText = questionTextField.getText().trim();
        String subject = (String) subjectComboBox.getSelectedItem(); 

        if (questionText.isEmpty() || subject == null) {
            JOptionPane.showMessageDialog(this, "题目内容和科目不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        questionToUpdate.setQuestionText(questionText);
        questionToUpdate.setSubject(subject);
        questionToUpdate.setType(type);
        questionToUpdate.setOptionA(null);
        questionToUpdate.setOptionB(null);
        questionToUpdate.setOptionC(null);
        questionToUpdate.setOptionD(null);
        questionToUpdate.setCorrectAnswer(null);
        questionToUpdate.setCorrectAnswers(null);
        questionToUpdate.setFillBlankAnswer(null);

        if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
            String optionA = optionAField.getText().trim();
            String optionB = optionBField.getText().trim();
            String optionC = optionCField.getText().trim();
            String optionD = optionDField.getText().trim();
            String correctAnswerText = correctAnswerField.getText().trim();

            if (optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty() || correctAnswerText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "选择题的选项和答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            questionToUpdate.setOptionA(optionA);
            questionToUpdate.setOptionB(optionB);
            questionToUpdate.setOptionC(optionC);
            questionToUpdate.setOptionD(optionD);

            if (type == QuestionType.SINGLE_CHOICE) {
                if (!correctAnswerText.matches("[A-D]")) {
                    JOptionPane.showMessageDialog(this, "单选题正确答案必须是 A, B, C 或 D。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                questionToUpdate.setCorrectAnswer(correctAnswerText);
            } else { // MULTIPLE_CHOICE
                if (!correctAnswerText.matches("([A-D](,)?)+")) {
                    JOptionPane.showMessageDialog(this, "多选题正确答案必须是 A, B, C, D 的组合，用逗号分隔。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                questionToUpdate.setCorrectAnswers(Arrays.asList(correctAnswerText.split(",")));
            }
        } else if (type == QuestionType.FILL_BLANK) {
            String fillAnswer = fillBlankAnswerField.getText().trim();
            if (fillAnswer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "填空题答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            questionToUpdate.setFillBlankAnswer(fillAnswer);
        }

        boolean success = questionDAO.updateQuestion(questionToUpdate);

        if (success) {
            JOptionPane.showMessageDialog(this, "题目更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            // Pass the JComboBox and JTextField to clearQuestionForm
            // Correctly get typeComboBox and fillBlankAnswerField for the main form
            JPanel formPanel = (JPanel) questionTextField.getParent(); 
            JComboBox<QuestionType> typeComboBoxInForm = null;
            JTextField fillBlankAnswerFieldInForm = null;
            for (Component comp : formPanel.getComponents()) {
                if (comp instanceof JComboBox && ((JComboBox<?>) comp).getItemCount() > 0 && ((JComboBox<?>) comp).getItemAt(0) instanceof QuestionType) {
                    typeComboBoxInForm = (JComboBox<QuestionType>) comp;
                }
                if (comp instanceof JTextField && "fillBlankAnswerField".equals(comp.getName())) {
                    fillBlankAnswerFieldInForm = (JTextField) comp;
                }
            }
            if (typeComboBoxInForm != null) {
                 clearQuestionForm(typeComboBoxInForm, fillBlankAnswerFieldInForm);
            }
            loadQuestionsTable(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "题目更新失败，请检查输入或数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}