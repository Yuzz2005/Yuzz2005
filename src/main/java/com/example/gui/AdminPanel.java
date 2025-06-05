package com.example.gui;

import com.example.model.Admin;
import com.example.model.Student; // Student model for addStudent
import com.example.model.Question;
import com.example.model.ExamRecord;
import com.example.model.Subject;
import com.example.model.QuestionType;
import com.example.dao.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminPanel extends JPanel {
    private MainFrame mainFrame; // 引用主框架
    private Admin currentAdmin;

    // DAOs (从 MainFrame 获取或直接实例化，取决于设计)
    private StudentDAO studentDAO;
    private QuestionDAO questionDAO;
    private ExamDAO examDAO;
    private SubjectDAO subjectDAO;

    // 管理员界面特有的组件 (从 ExamSystemGUI 移动过来)
    private JLabel adminWelcomeLabel;
    
    // Admin Panel's own CardLayout for managing its sub-panels
    private CardLayout adminCardLayout;
    private JPanel adminContentPanel; // Main content panel for admin section

    // Card names for admin section
    private static final String VIEW_ADMIN_MENU = "VIEW_ADMIN_MENU";
    private static final String VIEW_MANAGE_STUDENTS = "VIEW_MANAGE_STUDENTS";
    private static final String VIEW_MANAGE_QUESTIONS = "VIEW_MANAGE_QUESTIONS";
    private static final String VIEW_MANAGE_EXAMS = "VIEW_MANAGE_EXAMS";
    private static final String VIEW_MANAGE_SUBJECTS = "VIEW_MANAGE_SUBJECTS";

    // Components for Manage Students Panel (will be part of a card)
    private JTextField newStudentIdField;
    private JTextField newStudentNameField;
    private JPasswordField newStudentPasswordField;
    private JTable studentsTable;
    private JScrollPane studentsScrollPane;

    // Components for Manage Questions Panel
    private JTextField questionTextField;
    private JComboBox<QuestionType> typeComboBoxForQuestions;
    private JTextField optionAField, optionBField, optionCField, optionDField;
    private JTextField correctAnswerField;
    private JTextField fillBlankAnswerFieldForQuestions;
    private JComboBox<String> subjectComboBoxForQuestions;
    private JTable questionsTable;
    private JScrollPane questionsScrollPane;

    // Labels for question form fields
    private JLabel optionALabel, optionBLabel, optionCLabel, optionDLabel;
    private JLabel correctAnswerLabel;
    private JLabel fillBlankAnswerLabelForQuestions;

    // Components for Manage Exams Panel
    private JTable examRecordsTable;
    private JScrollPane examRecordsScrollPane;
    private JButton deleteExamRecordButton;

    // Components for Manage Subjects Panel
    private JTable subjectsTable;
    private JScrollPane subjectsScrollPane;
    private JTextField subjectNameField;
    private JTextField subjectNameFieldForNewSubject;


    public AdminPanel(MainFrame mainFrame, Admin admin) {
        this.mainFrame = mainFrame;
        this.currentAdmin = admin;
        this.studentDAO = mainFrame.getStudentDAO();
        this.questionDAO = mainFrame.getQuestionDAO();
        this.examDAO = mainFrame.getExamDAO();
        this.subjectDAO = mainFrame.getSubjectDAO();

        setLayout(new BorderLayout());

        adminCardLayout = new CardLayout();
        adminContentPanel = new JPanel(adminCardLayout);

        adminContentPanel.add(createAdminMenuPanel(), VIEW_ADMIN_MENU);
        adminContentPanel.add(createManageStudentsPanel(), VIEW_MANAGE_STUDENTS);
        adminContentPanel.add(createManageQuestionsPanel(), VIEW_MANAGE_QUESTIONS);
        adminContentPanel.add(createManageExamsPanel(), VIEW_MANAGE_EXAMS);
        adminContentPanel.add(createManageSubjectsPanel(), VIEW_MANAGE_SUBJECTS);
        // ... (add other panels when they are created)

        add(adminContentPanel, BorderLayout.CENTER);
        adminCardLayout.show(adminContentPanel, VIEW_ADMIN_MENU);
    }

    private JPanel createAdminMenuPanel() {
        JPanel adminMenuPanel = new JPanel(new BorderLayout());
        adminMenuPanel.setBackground(new Color(230, 240, 250));
    
        JPanel topPanel = new JPanel(new BorderLayout()); // Change to BorderLayout
        topPanel.setBackground(new Color(230, 240, 250));
        
        // Panel to hold welcome label for centering
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setOpaque(false);
        adminWelcomeLabel = new JLabel("欢迎管理员: " + currentAdmin.getUsername() + "  ");
        adminWelcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        welcomePanel.add(adminWelcomeLabel);
        
        topPanel.add(welcomePanel, BorderLayout.CENTER); // Add welcome panel to center
    
        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        logoutButton.addActionListener(e -> mainFrame.logout());
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel to hold logout button
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        
        topPanel.add(logoutPanel, BorderLayout.EAST); // Add logout button panel to east
        
        adminMenuPanel.add(topPanel, BorderLayout.NORTH);
    
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(230, 240, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Increased insets for more space
        gbc.ipadx = 40; gbc.ipady = 20; // Increased padding for larger buttons
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
            
        JButton manageStudentsButton = mainFrame.createMenuButton("管理学生", new Color(30, 144, 255));
        manageStudentsButton.addActionListener(e -> adminCardLayout.show(adminContentPanel, VIEW_MANAGE_STUDENTS));
        gbc.gridx = 0; gbc.gridy = 0; centerPanel.add(manageStudentsButton, gbc);

        JButton manageQuestionsButton = mainFrame.createMenuButton("管理题目", new Color(50, 205, 50));
        manageQuestionsButton.addActionListener(e -> showManageQuestionsPanel());
        gbc.gridx = 1; gbc.gridy = 0; centerPanel.add(manageQuestionsButton, gbc);

        JButton manageExamsButton = mainFrame.createMenuButton("管理考试记录", new Color(255, 140, 0));
        manageExamsButton.addActionListener(e -> showManageExamsPanel());
        gbc.gridx = 0; gbc.gridy = 1; centerPanel.add(manageExamsButton, gbc);

        JButton manageSubjectsButton = mainFrame.createMenuButton("管理科目", new Color(128, 0, 128));
        manageSubjectsButton.addActionListener(e -> showManageSubjectsPanel());
        gbc.gridx = 1; gbc.gridy = 1; centerPanel.add(manageSubjectsButton, gbc);

        adminMenuPanel.add(centerPanel, BorderLayout.CENTER);
        return adminMenuPanel;
    }

    private JPanel createManageStudentsPanel() {
        JPanel manageStudentsPanel = new JPanel(new BorderLayout(10,10));
        manageStudentsPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        manageStudentsPanel.setBackground(new Color(248, 248, 255)); // Changed background color

        // Title and Back Button Panel
        JPanel topNavPanel = new JPanel(new BorderLayout());
        topNavPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("管理学生账户", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        JButton backButton = new JButton("返回管理员主菜单");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        backButton.addActionListener(e -> adminCardLayout.show(adminContentPanel, VIEW_ADMIN_MENU));
        topNavPanel.add(backButton, BorderLayout.WEST);
        topNavPanel.add(titleLabel, BorderLayout.CENTER);
        manageStudentsPanel.add(topNavPanel, BorderLayout.NORTH);

        // Form Panel for adding students
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("添加新学生"));
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("学号:"), gbc);
        newStudentIdField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(newStudentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("姓名:"), gbc);
        newStudentNameField = new JTextField(20);
        gbc.gridx = 1; formPanel.add(newStudentNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("密码:"), gbc);
        newStudentPasswordField = new JPasswordField(20);
        gbc.gridx = 1; formPanel.add(newStudentPasswordField, gbc);

        JButton addStudentButton = new JButton("确认添加学生");
        addStudentButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addStudentButton.addActionListener(e -> addStudent());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 8, 8);
        formPanel.add(addStudentButton, gbc);
        
        // Center Panel with Table and Form
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Students Table
        studentsTable = new JTable();
        studentsScrollPane = new JScrollPane(studentsTable);
        loadStudentsTable(); // 加载学生列表
        centerPanel.add(studentsScrollPane, BorderLayout.CENTER);

        // Form Panel
        formPanel.setBorder(BorderFactory.createTitledBorder("添加新学生"));
        centerPanel.add(formPanel, BorderLayout.SOUTH);

        // Delete Button Panel
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deletePanel.setOpaque(false);
        JButton deleteStudentButton = new JButton("删除选中学生");
        deleteStudentButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        deleteStudentButton.addActionListener(e -> deleteStudent());
        deletePanel.add(deleteStudentButton);
        centerPanel.add(deletePanel, BorderLayout.NORTH);

        manageStudentsPanel.add(centerPanel, BorderLayout.CENTER);
        return manageStudentsPanel;
    }

    private void loadStudentsTable() {
        List<Student> students = studentDAO.getAllStudents();
        String[] columnNames = {"ID", "学号", "姓名"};
        Object[][] data = new Object[students.size()][columnNames.length];

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getId();
            data[i][1] = s.getStudentId();
            data[i][2] = s.getName();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 使表格不可编辑
            }
        };
        studentsTable.setModel(model);
        studentsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        studentsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
    }

    private void deleteStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的学生", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int studentId = (int) studentsTable.getValueAt(selectedRow, 0);
        String studentName = (String) studentsTable.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除学生：" + studentName + " 吗？\n删除后将无法恢复！",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentDAO.deleteStudent(studentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "学生删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadStudentsTable(); // 刷新学生列表
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请检查数据库连接", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addStudent() {
        String studentId = newStudentIdField.getText().trim();
        String name = newStudentNameField.getText().trim();
        String password = new String(newStudentPasswordField.getPassword()).trim();

        if (studentId.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "学号、姓名和密码都不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (studentDAO.findByStudentId(studentId) != null) {
            JOptionPane.showMessageDialog(this, "学号 " + studentId + " 已存在！", "添加失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Student newStudent = new Student(0, studentId, name, password);
        boolean success = studentDAO.addStudent(newStudent);
        if (success) {
            JOptionPane.showMessageDialog(this, "学生 " + name + " 添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            newStudentIdField.setText("");
            newStudentNameField.setText("");
            newStudentPasswordField.setText("");
            // 刷新学生列表
            loadStudentsTable();
        } else {
            JOptionPane.showMessageDialog(this, "学生添加失败，请检查日志或数据库连接。", "失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the panel for managing questions.
     */
    private JPanel createManageQuestionsPanel() {
        JPanel manageQuestionsPanel = new JPanel(new BorderLayout(10,10));
        manageQuestionsPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        manageQuestionsPanel.setBackground(new Color(250, 250, 240)); // Light yellow-ish background

        // Top: Title and Back Button
        JPanel topNavPanel = new JPanel(new BorderLayout());
        topNavPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("管理题目", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        JButton backButton = new JButton("返回管理员主菜单");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        backButton.addActionListener(e -> adminCardLayout.show(adminContentPanel, VIEW_ADMIN_MENU));
        topNavPanel.add(backButton, BorderLayout.WEST);
        topNavPanel.add(titleLabel, BorderLayout.CENTER);
        manageQuestionsPanel.add(topNavPanel, BorderLayout.NORTH);

        // Center: Filter and Table Panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);

        // Add subject filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("按科目筛选:"));
        JComboBox<String> subjectFilterComboBox = new JComboBox<>();
        subjectFilterComboBox.addItem("全部科目");
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            subjectFilterComboBox.addItem(subject.getName());
        }
        subjectFilterComboBox.addActionListener(e -> {
            String selectedSubject = (String) subjectFilterComboBox.getSelectedItem();
            if (selectedSubject != null) {
                loadQuestionsTable(selectedSubject.equals("全部科目") ? null : selectedSubject);
            }
        });
        filterPanel.add(subjectFilterComboBox);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Questions Table
        questionsTable = new JTable();
        questionsScrollPane = new JScrollPane(questionsTable);
        centerPanel.add(questionsScrollPane, BorderLayout.CENTER);
        
        manageQuestionsPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom: Form for adding/editing questions
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("题目详情/编辑"));
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int gridY = 0;
        // Type
        formPanel.add(new JLabel("题目类型:"), gbcData(gbc, 0, gridY, 1, 1));
        typeComboBoxForQuestions = new JComboBox<>(QuestionType.values());
        formPanel.add(typeComboBoxForQuestions, gbcData(gbc, 1, gridY++, 2, 1));
        // Question Text
        formPanel.add(new JLabel("题目内容:"), gbcData(gbc, 0, gridY, 1, 1));
        questionTextField = new JTextField(30);
        formPanel.add(questionTextField, gbcData(gbc, 1, gridY++, 2, 1));
        // Options
        optionAField = new JTextField(20);
        optionBField = new JTextField(20);
        optionCField = new JTextField(20);
        optionDField = new JTextField(20);

        optionALabel = new JLabel("选项A:");
        formPanel.add(optionALabel, gbcData(gbc, 0, gridY, 1, 1));
        formPanel.add(optionAField, gbcData(gbc, 1, gridY++, 2, 1));

        optionBLabel = new JLabel("选项B:");
        formPanel.add(optionBLabel, gbcData(gbc, 0, gridY, 1, 1));
        formPanel.add(optionBField, gbcData(gbc, 1, gridY++, 2, 1));

        optionCLabel = new JLabel("选项C:");
        formPanel.add(optionCLabel, gbcData(gbc, 0, gridY, 1, 1));
        formPanel.add(optionCField, gbcData(gbc, 1, gridY++, 2, 1));

        optionDLabel = new JLabel("选项D:");
        formPanel.add(optionDLabel, gbcData(gbc, 0, gridY, 1, 1));
        formPanel.add(optionDField, gbcData(gbc, 1, gridY++, 2, 1));

        // Correct Answer (for choice)
        correctAnswerLabel = new JLabel("选择题答案:");
        formPanel.add(correctAnswerLabel, gbcData(gbc, 0, gridY, 1, 1));
        correctAnswerField = new JTextField(10);
        formPanel.add(correctAnswerField, gbcData(gbc, 1, gridY++, 2, 1));

        // Fill Blank Answer
        fillBlankAnswerLabelForQuestions = new JLabel("填空题答案:");
        formPanel.add(fillBlankAnswerLabelForQuestions, gbcData(gbc, 0, gridY, 1, 1));
        fillBlankAnswerFieldForQuestions = new JTextField(20);
        fillBlankAnswerFieldForQuestions.setName("fillBlankAnswerField"); // Keep name for consistency if needed
        formPanel.add(fillBlankAnswerFieldForQuestions, gbcData(gbc, 1, gridY++, 2, 1));
        // Subject
        formPanel.add(new JLabel("科目:"), gbcData(gbc, 0, gridY, 1, 1));
        subjectComboBoxForQuestions = new JComboBox<>();
        formPanel.add(subjectComboBoxForQuestions, gbcData(gbc, 1, gridY++, 2, 1));

        // Action Buttons Panel
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addQuestionButton = new JButton("添加新题目");
        addQuestionButton.addActionListener(e -> showAddQuestionDialog());
        JButton updateQuestionButton = new JButton("更新选中题目");
        updateQuestionButton.addActionListener(e -> updateQuestion());
        JButton deleteQuestionButton = new JButton("删除选中题目");
        deleteQuestionButton.addActionListener(e -> deleteQuestion());
        
        actionButtonPanel.add(addQuestionButton);
        actionButtonPanel.add(updateQuestionButton);
        actionButtonPanel.add(deleteQuestionButton);
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(actionButtonPanel, gbc);

        manageQuestionsPanel.add(formPanel, BorderLayout.SOUTH);
        
        // Event listener for type ComboBox to show/hide relevant fields
        typeComboBoxForQuestions.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateQuestionFormFieldVisibility((QuestionType) e.getItem());
            }
        });

        // Event listener for table selection
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && questionsTable.getSelectedRow() != -1) {
                populateQuestionFormFromTable();
            }
        });

        updateQuestionFormFieldVisibility((QuestionType) typeComboBoxForQuestions.getSelectedItem()); // Initial setup
        updateSubjectComboBoxForQuestionManagement(); // Load subjects
        loadQuestionsTable(); // Load initial table data
        
        return manageQuestionsPanel;
    }

    // Helper for GridBagConstraints to reduce repetition
    private GridBagConstraints gbcData(GridBagConstraints gbc, int x, int y, int w, int h) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = w; gbc.gridheight = h;
        return gbc;
    }
    
    private void updateQuestionFormFieldVisibility(QuestionType selectedType) {
        boolean isChoice = selectedType == QuestionType.SINGLE_CHOICE || selectedType == QuestionType.MULTIPLE_CHOICE;
        boolean isFillBlank = selectedType == QuestionType.FILL_BLANK;

        optionAField.setVisible(isChoice);
        optionBField.setVisible(isChoice);
        optionCField.setVisible(isChoice);
        optionDField.setVisible(isChoice);
        correctAnswerField.setVisible(isChoice);

        optionALabel.setVisible(isChoice);
        optionBLabel.setVisible(isChoice);
        optionCLabel.setVisible(isChoice);
        optionDLabel.setVisible(isChoice);
        correctAnswerLabel.setVisible(isChoice);

        fillBlankAnswerFieldForQuestions.setVisible(isFillBlank);
        fillBlankAnswerLabelForQuestions.setVisible(isFillBlank);
        
        if(isChoice) {
            correctAnswerField.setToolTipText(selectedType == QuestionType.SINGLE_CHOICE ? "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B,C,D)");
        } else {
            correctAnswerField.setToolTipText(null);
        }
    }

    private void showManageQuestionsPanel() {
        updateSubjectComboBoxForQuestionManagement();
        loadQuestionsTable();
        adminCardLayout.show(adminContentPanel, VIEW_MANAGE_QUESTIONS);
    }

    private void updateSubjectComboBoxForQuestionManagement() {
        subjectComboBoxForQuestions.removeAllItems();
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            subjectComboBoxForQuestions.addItem(subject.getName());
        }
    }

    private void loadQuestionsTable() {
        loadQuestionsTable(null);
    }

    private void loadQuestionsTable(String subjectFilter) {
        List<Question> questions;
        if (subjectFilter == null || subjectFilter.isEmpty()) {
            questions = questionDAO.getAllQuestions();
        } else {
            questions = questionDAO.getAllQuestions().stream()
                .filter(q -> subjectFilter.equals(q.getSubject()))
                .toList();
        }
        
        String[] columnNames = {"ID", "类型", "题目", "科目"};
        Object[][] data = new Object[questions.size()][columnNames.length];

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            data[i][0] = q.getId();
            data[i][1] = q.getType() != null ? q.getType().getDescription() : "未知";
            data[i][2] = q.getQuestionText();
            data[i][3] = q.getSubject();
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        questionsTable.setModel(model);
        // Set column widths, etc. as needed
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(300);
    }

    private void populateQuestionFormFromTable() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        Question q = questionDAO.getQuestionById(questionId);
        if (q == null) return;

        questionTextField.setText(q.getQuestionText());
        subjectComboBoxForQuestions.setSelectedItem(q.getSubject());
        typeComboBoxForQuestions.setSelectedItem(q.getType());
        updateQuestionFormFieldVisibility(q.getType()); // Crucial to update visibility first

        if (q.getType() == QuestionType.SINGLE_CHOICE || q.getType() == QuestionType.MULTIPLE_CHOICE) {
            optionAField.setText(q.getOptionA());
            optionBField.setText(q.getOptionB());
            optionCField.setText(q.getOptionC());
            optionDField.setText(q.getOptionD());
            if (q.getType() == QuestionType.SINGLE_CHOICE) {
                correctAnswerField.setText(q.getCorrectAnswer());
            } else { // MULTIPLE_CHOICE
                correctAnswerField.setText(q.getCorrectAnswers() != null ? String.join(",", q.getCorrectAnswers()) : "");
            }
        } else if (q.getType() == QuestionType.FILL_BLANK) {
            fillBlankAnswerFieldForQuestions.setText(q.getFillBlankAnswer());
        }
    }
    
    private void clearQuestionForm() {
        questionTextField.setText("");
        optionAField.setText("");
        optionBField.setText("");
        optionCField.setText("");
        optionDField.setText("");
        correctAnswerField.setText("");
        fillBlankAnswerFieldForQuestions.setText("");
        if (subjectComboBoxForQuestions.getItemCount() > 0) {
            subjectComboBoxForQuestions.setSelectedIndex(0);
        }
        typeComboBoxForQuestions.setSelectedItem(QuestionType.SINGLE_CHOICE);
        updateQuestionFormFieldVisibility(QuestionType.SINGLE_CHOICE);
        questionsTable.clearSelection();
    }

    private void showAddQuestionDialog() {
        JDialog addDialog = new JDialog(mainFrame, "添加新题目", true); 
        addDialog.setSize(650, 550);
        addDialog.setMinimumSize(new Dimension(650, 550));
        addDialog.setLocationRelativeTo(mainFrame);
        addDialog.setLayout(new BorderLayout(10,10));
        addDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel dialogFormPanel = new JPanel(new GridBagLayout());
        dialogFormPanel.setBorder(BorderFactory.createTitledBorder("添加新题目"));
        dialogFormPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int gridY = 0;
        // Type
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(new JLabel("题目类型:"), gbc);
        JComboBox<QuestionType> dialogTypeComboBox = new JComboBox<>(QuestionType.values());
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogTypeComboBox, gbc);
        gridY++;

        // Subject
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(new JLabel("科目:"), gbc);
        JComboBox<String> dialogSubjectComboBox = new JComboBox<>();
        List<Subject> subjects = subjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            dialogSubjectComboBox.addItem(subject.getName());
        }
        if (dialogSubjectComboBox.getItemCount() > 0) {
            dialogSubjectComboBox.setSelectedIndex(0);
        }
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogSubjectComboBox, gbc);
        gridY++;

        // Question Text
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(new JLabel("题目内容:"), gbc);
        JTextField dialogQuestionTextField = new JTextField(30);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogQuestionTextField, gbc);
        gridY++;

        // Options
        JTextField dialogOptionA = new JTextField(20);
        JTextField dialogOptionB = new JTextField(20);
        JTextField dialogOptionC = new JTextField(20);
        JTextField dialogOptionD = new JTextField(20);
        JLabel dialogOptionALabel = new JLabel("选项A:");
        JLabel dialogOptionBLabel = new JLabel("选项B:");
        JLabel dialogOptionCLabel = new JLabel("选项C:");
        JLabel dialogOptionDLabel = new JLabel("选项D:");

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(dialogOptionALabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogOptionA, gbc);
        gridY++;

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(dialogOptionBLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogOptionB, gbc);
        gridY++;

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(dialogOptionCLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogOptionC, gbc);
        gridY++;

        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        dialogFormPanel.add(dialogOptionDLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogOptionD, gbc);
        gridY++;

        // Correct Answer (Choice)
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        JLabel dialogCorrectAnswerLabel = new JLabel("选择题答案:");
        dialogFormPanel.add(dialogCorrectAnswerLabel, gbc);
        JTextField dialogCorrectAnswerChoice = new JTextField(10);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogCorrectAnswerChoice, gbc);
        gridY++;

        // Fill Blank Answer
        gbc.gridx = 0; gbc.gridy = gridY; gbc.gridwidth = 1;
        JLabel dialogFillBlankAnswerLabel = new JLabel("填空题答案:");
        dialogFormPanel.add(dialogFillBlankAnswerLabel, gbc);
        JTextField dialogFillBlankAnswer = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2;
        dialogFormPanel.add(dialogFillBlankAnswer, gbc);
        gridY++;

        // --- Dynamic visibility logic for dialog ---
        Runnable updateDialogFieldsVisibility = () -> {
            QuestionType selected = (QuestionType) dialogTypeComboBox.getSelectedItem();
            boolean isChoice = selected == QuestionType.SINGLE_CHOICE || selected == QuestionType.MULTIPLE_CHOICE;
            boolean isFill = selected == QuestionType.FILL_BLANK;
            
            // 控制选项字段和标签的可见性
            dialogOptionA.setVisible(isChoice);
            dialogOptionB.setVisible(isChoice);
            dialogOptionC.setVisible(isChoice);
            dialogOptionD.setVisible(isChoice);
            dialogOptionALabel.setVisible(isChoice);
            dialogOptionBLabel.setVisible(isChoice);
            dialogOptionCLabel.setVisible(isChoice);
            dialogOptionDLabel.setVisible(isChoice);
            
            // 控制答案字段和标签的可见性
            dialogCorrectAnswerChoice.setVisible(isChoice);
            dialogCorrectAnswerLabel.setVisible(isChoice);
            dialogFillBlankAnswer.setVisible(isFill);
            dialogFillBlankAnswerLabel.setVisible(isFill);
            
            if (isChoice) {
                dialogCorrectAnswerChoice.setToolTipText(selected == QuestionType.SINGLE_CHOICE ? 
                    "单个答案字母 (A-D)" : "多个答案字母,逗号分隔 (A,B,C,D)");
            } else {
                dialogCorrectAnswerChoice.setToolTipText(null);
            }
        };
        dialogTypeComboBox.addActionListener(e -> updateDialogFieldsVisibility.run());
        updateDialogFieldsVisibility.run(); // Initial call
        // --- End dynamic visibility ---

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton confirmAddButton = new JButton("确认添加");
        confirmAddButton.setPreferredSize(new Dimension(100, 30));
        confirmAddButton.setBackground(new Color(70, 130, 180));
        confirmAddButton.setForeground(Color.WHITE);
        confirmAddButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("取消");
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setBackground(new Color(190, 190, 190));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        
        buttonPanel.add(confirmAddButton);
        buttonPanel.add(cancelButton);

        confirmAddButton.addActionListener(e -> {
            String questionText = dialogQuestionTextField.getText().trim();
            String subjectName = (String) dialogSubjectComboBox.getSelectedItem();
            QuestionType type = (QuestionType) dialogTypeComboBox.getSelectedItem();

            if (questionText.isEmpty() || subjectName == null) {
                JOptionPane.showMessageDialog(addDialog, "题目内容和科目不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Question newQuestion = new Question();
            newQuestion.setQuestionText(questionText);
            newQuestion.setSubject(subjectName);
            newQuestion.setType(type);

            if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
                String optA = dialogOptionA.getText().trim();
                String optB = dialogOptionB.getText().trim();
                String optC = dialogOptionC.getText().trim();
                String optD = dialogOptionD.getText().trim();
                String correctAns = dialogCorrectAnswerChoice.getText().trim().toUpperCase();

                if (optA.isEmpty() || optB.isEmpty() || (type == QuestionType.SINGLE_CHOICE && (optC.isEmpty() || optD.isEmpty())) || correctAns.isEmpty()) {
                     // For single choice, typically 4 options are expected. For multiple, C and D can be optional if not used.
                    if (type == QuestionType.SINGLE_CHOICE && (optC.isEmpty() || optD.isEmpty())){
                        JOptionPane.showMessageDialog(addDialog, "单选题至少需要A,B,C,D四个选项和答案！", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    } else if (type == QuestionType.MULTIPLE_CHOICE && (optA.isEmpty() || optB.isEmpty())) {
                         JOptionPane.showMessageDialog(addDialog, "多选题至少需要A,B两个选项和答案！", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                 newQuestion.setOptionA(optA);
                 newQuestion.setOptionB(optB);
                 newQuestion.setOptionC(optC.isEmpty() ? null : optC); // Store null if empty
                 newQuestion.setOptionD(optD.isEmpty() ? null : optD);


                if (type == QuestionType.SINGLE_CHOICE) {
                    if (!correctAns.matches("[A-D]") || correctAns.length() != 1) {
                        JOptionPane.showMessageDialog(addDialog, "单选题正确答案必须是单个字母 A, B, C 或 D。", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    newQuestion.setCorrectAnswer(correctAns);
                } else { // MULTIPLE_CHOICE
                    if (!correctAns.matches("([A-D](,)?)+") || Arrays.stream(correctAns.split(",")).anyMatch(s -> s.length() !=1) ) {
                        JOptionPane.showMessageDialog(addDialog, "多选题正确答案必须是 A, B, C, D 的组合(无重复)，用逗号分隔。", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                     List<String> answers = new ArrayList<>(Arrays.asList(correctAns.split(",")));
                     // Check for duplicates
                     if (answers.stream().distinct().count() != answers.size()){
                        JOptionPane.showMessageDialog(addDialog, "多选题答案不能包含重复选项。", "输入错误", JOptionPane.WARNING_MESSAGE);
                        return;
                     }
                    newQuestion.setCorrectAnswers(answers);
                }
            } else if (type == QuestionType.FILL_BLANK) {
                String fillAnswer = dialogFillBlankAnswer.getText().trim();
                if (fillAnswer.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "填空题答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                newQuestion.setFillBlankAnswer(fillAnswer);
            }

            boolean success = questionDAO.addQuestion(newQuestion);
            if (success) {
                JOptionPane.showMessageDialog(addDialog, "题目添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                addDialog.dispose();
                loadQuestionsTable(); // Refresh table in the main admin panel
                clearQuestionForm();  // Clear the form in the main admin panel
            } else {
                JOptionPane.showMessageDialog(addDialog, "题目添加失败，请检查输入或数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.add(dialogFormPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setVisible(true);
    }

    private void updateQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一个要更新的题目。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        Question qToUpdate = questionDAO.getQuestionById(questionId);
        if (qToUpdate == null) {
            JOptionPane.showMessageDialog(this, "无法找到要更新的题目 (ID: " + questionId + ")。", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Populate a Question object from the form fields
        String questionText = questionTextField.getText().trim();
        String subjectName = (String) subjectComboBoxForQuestions.getSelectedItem();
        QuestionType type = (QuestionType) typeComboBoxForQuestions.getSelectedItem();

        if (questionText.isEmpty() || subjectName == null || type == null) {
            JOptionPane.showMessageDialog(this, "题目内容、科目和类型都不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        qToUpdate.setQuestionText(questionText);
        qToUpdate.setSubject(subjectName);
        qToUpdate.setType(type);
        qToUpdate.setOptionA(null); qToUpdate.setOptionB(null); qToUpdate.setOptionC(null); qToUpdate.setOptionD(null); // Clear options first
        qToUpdate.setCorrectAnswer(null); qToUpdate.setCorrectAnswers(null); qToUpdate.setFillBlankAnswer(null); // Clear answers

        if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
            String optA = optionAField.getText().trim();
            String optB = optionBField.getText().trim();
            String optC = optionCField.getText().trim();
            String optD = optionDField.getText().trim();
            String correctAns = correctAnswerField.getText().trim().toUpperCase();

             if (optA.isEmpty() || optB.isEmpty() || (type == QuestionType.SINGLE_CHOICE && (optC.isEmpty() || optD.isEmpty())) || correctAns.isEmpty()) {
                if (type == QuestionType.SINGLE_CHOICE && (optC.isEmpty() || optD.isEmpty())){
                    JOptionPane.showMessageDialog(this, "单选题至少需要A,B,C,D四个选项和答案！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                } else if (type == QuestionType.MULTIPLE_CHOICE && (optA.isEmpty() || optB.isEmpty())) {
                     JOptionPane.showMessageDialog(this, "多选题至少需要A,B两个选项和答案！", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            qToUpdate.setOptionA(optA);
            qToUpdate.setOptionB(optB);
            qToUpdate.setOptionC(optC.isEmpty() ? null : optC);
            qToUpdate.setOptionD(optD.isEmpty() ? null : optD);

            if (type == QuestionType.SINGLE_CHOICE) {
                if (!correctAns.matches("[A-D]") || correctAns.length() != 1) {
                    JOptionPane.showMessageDialog(this, "单选题正确答案必须是单个字母 A, B, C 或 D。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                qToUpdate.setCorrectAnswer(correctAns);
            } else { // MULTIPLE_CHOICE
                 if (!correctAns.matches("([A-D](,)?)+") || Arrays.stream(correctAns.split(",")).anyMatch(s -> s.length() !=1) ) {
                    JOptionPane.showMessageDialog(this, "多选题正确答案必须是 A, B, C, D 的组合(无重复)，用逗号分隔。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                List<String> answers = new ArrayList<>(Arrays.asList(correctAns.split(",")));
                 if (answers.stream().distinct().count() != answers.size()){
                    JOptionPane.showMessageDialog(this, "多选题答案不能包含重复选项。", "输入错误", JOptionPane.WARNING_MESSAGE);
                    return;
                 }
                qToUpdate.setCorrectAnswers(answers);
            }
        } else if (type == QuestionType.FILL_BLANK) {
            String fillAnswer = fillBlankAnswerFieldForQuestions.getText().trim();
            if (fillAnswer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "填空题答案不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            qToUpdate.setFillBlankAnswer(fillAnswer);
        }

        boolean success = questionDAO.updateQuestion(qToUpdate);
        if (success) {
            JOptionPane.showMessageDialog(this, "题目 ID: " + qToUpdate.getId() + " 更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadQuestionsTable();
            clearQuestionForm();
        } else {
            JOptionPane.showMessageDialog(this, "题目更新失败。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteQuestion() {
        int selectedRow = questionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一个要删除的题目。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int questionId = (int) questionsTable.getValueAt(selectedRow, 0);
        String questionText = (String) questionsTable.getValueAt(selectedRow, 2); // Get text for confirmation

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除选中的题目吗？\nID: " + questionId + "\n题目: " + questionText,
                "确认删除题目", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = questionDAO.deleteQuestion(questionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "题目 ID: " + questionId + " 删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadQuestionsTable();
                clearQuestionForm(); // Clear form as the selected item is gone
            } else {
                JOptionPane.showMessageDialog(this, "题目删除失败。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Placeholder methods for other admin functionalities
    // 管理考试记录功能
    private JPanel createManageExamsPanel() {
        JPanel manageExamsPanel = new JPanel(new BorderLayout(10,10));
        manageExamsPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        manageExamsPanel.setBackground(new Color(240, 245, 250));

        // Title and Back Button Panel
        JPanel topNavPanel = new JPanel(new BorderLayout());
        topNavPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("管理考试记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        JButton backButton = new JButton("返回管理员主菜单");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        backButton.addActionListener(e -> adminCardLayout.show(adminContentPanel, VIEW_ADMIN_MENU));
        topNavPanel.add(backButton, BorderLayout.WEST);
        topNavPanel.add(titleLabel, BorderLayout.CENTER);
        manageExamsPanel.add(topNavPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "学生ID", "科目", "分数", "考试日期", "评语"};
        examRecordsTable = new JTable();
        examRecordsScrollPane = new JScrollPane(examRecordsTable);
        manageExamsPanel.add(examRecordsScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        deleteExamRecordButton = new JButton("删除考试记录");
        deleteExamRecordButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JButton addCommentButton = new JButton("添加/编辑评语"); // New button
        addCommentButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        buttonPanel.add(deleteExamRecordButton);
        buttonPanel.add(addCommentButton); // Add new button

        manageExamsPanel.add(buttonPanel, BorderLayout.SOUTH);

        deleteExamRecordButton.addActionListener(e -> deleteExamRecord());
        addCommentButton.addActionListener(e -> showCommentDialog()); // Action listener for new button

        return manageExamsPanel;
    }

    private void showManageExamsPanel() {
        loadExamRecordsTable();
        adminCardLayout.show(adminContentPanel, VIEW_MANAGE_EXAMS);
    }

    private void loadExamRecordsTable() {
        List<ExamRecord> records = mainFrame.getExamService().getAllExamRecords(); // Changed to get all records
        String[] columnNames = {"ID", "学生ID", "科目", "分数", "考试日期", "评语"};
        Object[][] data = new Object[records.size()][columnNames.length];

        for (int i = 0; i < records.size(); i++) {
            ExamRecord r = records.get(i);
            data[i][0] = r.getId();
            data[i][1] = r.getStudentId();
            data[i][2] = r.getSubject();
            data[i][3] = r.getScore();
            data[i][4] = r.getExamDate();
            data[i][5] = r.getComment(); // Populate comment column
        }

        examRecordsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        });
        // Set column widths for better display
        examRecordsTable.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        examRecordsTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Student ID
        examRecordsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Subject
        examRecordsTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Score
        examRecordsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Exam Date
        examRecordsTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Comment
    }

    private void deleteExamRecord() {
        int selectedRow = examRecordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的考试记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int recordId = (int) examRecordsTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的考试记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = mainFrame.getExamService().deleteExamRecord(recordId); // Changed to use ExamService
            if (success) {
                JOptionPane.showMessageDialog(this, "考试记录删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadExamRecordsTable();
            } else {
                JOptionPane.showMessageDialog(this, "考试记录删除失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 显示评语编辑对话框
     */
    private void showCommentDialog() {
        int selectedRow = examRecordsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要添加或编辑评语的考试记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int examRecordId = (int) examRecordsTable.getValueAt(selectedRow, 0);
        String currentComment = (String) examRecordsTable.getValueAt(selectedRow, 5);
        if (currentComment == null) {
            currentComment = "";
        }

        JTextArea commentArea = new JTextArea(currentComment, 5, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);

        int option = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "编辑评语（狠狠的恶评吧！）",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String newComment = commentArea.getText().trim();
            boolean success = mainFrame.getExamService().updateExamRecordComment(examRecordId, newComment);

            if (success) {
                JOptionPane.showMessageDialog(this, "评语保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadExamRecordsTable(); // Refresh the table to show the new comment
            } else {
                JOptionPane.showMessageDialog(this, "评语保存失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 管理科目功能
     private JPanel createManageSubjectsPanel() {
         JPanel manageSubjectsPanel = new JPanel(new BorderLayout(10, 10));
         manageSubjectsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
         manageSubjectsPanel.setBackground(new Color(240, 245, 250));

         // Title and Back Button Panel
         JPanel topNavPanel = new JPanel(new BorderLayout());
         topNavPanel.setOpaque(false);
         JLabel titleLabel = new JLabel("管理科目", SwingConstants.CENTER);
         titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
         JButton backButton = new JButton("返回管理员主菜单");
         backButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
         backButton.addActionListener(e -> adminCardLayout.show(adminContentPanel, VIEW_ADMIN_MENU));
         topNavPanel.add(backButton, BorderLayout.WEST);
         topNavPanel.add(titleLabel, BorderLayout.CENTER);
         manageSubjectsPanel.add(topNavPanel, BorderLayout.NORTH);

         String[] columnNames = {"ID", "科目名称"};
         subjectsTable = new JTable();
         subjectsScrollPane = new JScrollPane(subjectsTable);
         manageSubjectsPanel.add(subjectsScrollPane, BorderLayout.CENTER);

         JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
         JLabel subjectNameLabel = new JLabel("科目名称:");
         subjectNameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
         subjectNameField = new JTextField(20);
         subjectNameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
         JButton addSubjectButton = new JButton("添加科目");
         addSubjectButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
         JButton deleteSubjectButton = new JButton("删除科目");
         deleteSubjectButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));

         formPanel.add(subjectNameLabel);
         formPanel.add(subjectNameField);
         formPanel.add(addSubjectButton);
         formPanel.add(deleteSubjectButton);

         manageSubjectsPanel.add(formPanel, BorderLayout.SOUTH);

         addSubjectButton.addActionListener(e -> addSubject());
         deleteSubjectButton.addActionListener(e -> deleteSubject());

         return manageSubjectsPanel;
     }

     private void showManageSubjectsPanel() {
         loadSubjectsTable();
         adminCardLayout.show(adminContentPanel, VIEW_MANAGE_SUBJECTS);
     }

     private void loadSubjectsTable() {
         List<Subject> subjects = mainFrame.getSubjectDAO().getAllSubjects();
         String[] columnNames = {"ID", "科目名称"};
         Object[][] data = new Object[subjects.size()][2];

         for (int i = 0; i < subjects.size(); i++) {
             Subject s = subjects.get(i);
             data[i][0] = s.getId();
             data[i][1] = s.getName();
         }

         subjectsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
         // Set column widths for better display
         subjectsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
         subjectsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Subject Name
     }

     private void addSubject() {
         String subjectName = subjectNameField.getText().trim();
         if (subjectName.isEmpty()) {
             JOptionPane.showMessageDialog(this, "科目名称不能为空！", "输入错误", JOptionPane.WARNING_MESSAGE);
             return;
         }

         if (mainFrame.getSubjectDAO().findSubjectByName(subjectName) != null) {
             JOptionPane.showMessageDialog(this, "科目已存在！", "添加失败", JOptionPane.ERROR_MESSAGE);
             return;
         }

         boolean success = mainFrame.getSubjectDAO().addSubject(subjectName);
         if (success) {
             JOptionPane.showMessageDialog(this, "科目添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
             subjectNameField.setText("");
             loadSubjectsTable();
         } else {
             JOptionPane.showMessageDialog(this, "科目添加失败，请检查输入或数据库连接！", "失败", JOptionPane.ERROR_MESSAGE);
         }
     }

     private void deleteSubject() {
         int selectedRow = subjectsTable.getSelectedRow();
         if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "请选择要删除的科目。", "提示", JOptionPane.INFORMATION_MESSAGE);
             return;
         }

         int subjectId = (int) subjectsTable.getValueAt(selectedRow, 0);

         int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的科目吗？", "确认删除", JOptionPane.YES_NO_OPTION);
         if (confirm == JOptionPane.YES_OPTION) {
             boolean success = mainFrame.getSubjectDAO().deleteSubject(subjectId);
             if (success) {
                 JOptionPane.showMessageDialog(this, "科目删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                 loadSubjectsTable();
             } else {
                 JOptionPane.showMessageDialog(this, "科目删除失败，请检查数据库连接。", "错误", JOptionPane.ERROR_MESSAGE);
             }
         }
     }
 }