package com.example.gui;

import com.example.model.Student;
import com.example.model.Question;
import com.example.model.ExamRecord;
import com.example.model.StudentAnswerDetail;
import com.example.model.QuestionType;
import com.example.service.ExamService;
import com.example.dao.StudentAnswerDetailDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

public class StudentPanel extends JPanel {
    private MainFrame mainFrame; 
    private Student currentStudent;
    private ExamService examService;
    private StudentAnswerDetailDAO studentAnswerDetailDAO;

    private JLabel welcomeLabel;
    private List<Question> currentQuestions;
    private Map<Integer, String> studentAnswers;
    private int currentQuestionIndex;
    private JPanel mainContentPanel; // 用于切换学生界面的不同视图 (菜单/考试)
    private CardLayout studentCardLayout; // 用于在学生界面内部切换

    // 考试界面具体的组件
    private JPanel examPanel; // 整体考试面板，会被添加到mainContentPanel的某个卡片中
    private JPanel questionDisplayArea; // 题目显示区域
    private ButtonGroup answerGroup;
    private JButton prevButton, nextButton, submitButton;
    private JLabel questionCountLabel;
    private JLabel timerLabel; // 用于显示考试时间
    private Timer examTimer; // 考试计时器
    private int remainingTime; // 剩余时间，单位秒

    // 题目目录相关组件
    private JPanel questionDirectoryPanel; // 题目目录面板
    private List<JButton> questionButtons; // 存储题目按钮的列表

    // Panel names for student's internal card layout
    private static final String STUDENT_MENU_PANEL = "STUDENT_MENU";
    private static final String STUDENT_EXAM_PANEL = "STUDENT_EXAM";

    public StudentPanel(MainFrame mainFrame, Student student) {
        this.mainFrame = mainFrame;
        this.currentStudent = student;
        this.examService = mainFrame.getExamService();
        this.studentAnswerDetailDAO = mainFrame.getStudentAnswerDetailDAO();
        this.studentAnswers = new HashMap<>();
        this.currentQuestions = new ArrayList<>();
        this.questionButtons = new ArrayList<>();

        studentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(studentCardLayout);
        mainContentPanel.setBackground(new Color(230, 240, 250)); // Set background color to match login panel

        // 创建并添加学生主菜单面板
        mainContentPanel.add(createStudentMainMenuPanel(), STUDENT_MENU_PANEL);
        // 创建考试面板（但先不显示）
        this.examPanel = createExamPanelInternal(); // Renamed to avoid conflict
        mainContentPanel.add(this.examPanel, STUDENT_EXAM_PANEL); 

        setLayout(new BorderLayout());
        add(mainContentPanel, BorderLayout.CENTER);
        studentCardLayout.show(mainContentPanel, STUDENT_MENU_PANEL); // 默认显示学生主菜单
    }

    private JPanel createStudentMainMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(230, 240, 250)); // Set background color to match login panel
        
        JPanel topPanel = new JPanel(new BorderLayout()); // Change to BorderLayout
        topPanel.setBackground(new Color(230, 240, 250)); // Set background color to match login panel
        
        // Panel to hold welcome label for centering
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setOpaque(false);
        welcomeLabel = new JLabel("欢迎学生: " + currentStudent.getName() + "  ");
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        welcomePanel.add(welcomeLabel);
        
        topPanel.add(welcomePanel, BorderLayout.CENTER); // Add welcome panel to center

        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        logoutButton.addActionListener(e -> mainFrame.logout());
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel to hold logout button
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        
        topPanel.add(logoutPanel, BorderLayout.EAST); // Add logout button panel to east
        menuPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(230, 240, 250)); // Set background color to match login panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.ipadx = 30; //增加按钮宽度
        gbc.ipady = 10; //增加按钮高度
        
        JButton startExamButton = mainFrame.createMenuButton("开始考试", new Color(34, 139, 34));
        startExamButton.addActionListener(e -> showSubjectSelection());
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(startExamButton, gbc);

        JButton viewScoresButton = mainFrame.createMenuButton("查看成绩/错题回顾", new Color(30, 144, 255));
        viewScoresButton.addActionListener(e -> showExamHistory());
        gbc.gridx = 1; gbc.gridy = 0;
        centerPanel.add(viewScoresButton, gbc);
        menuPanel.add(centerPanel, BorderLayout.CENTER);
        
        return menuPanel;
    }

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
    
    private void startExam(String subject) {
        int questionCount = examService.getQuestionCount(subject);
        currentQuestions = examService.startExam(subject, questionCount);
    
        if (currentQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "该科目暂无题目或题目数量不足！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        studentAnswers.clear();
        currentQuestionIndex = 0;
        createQuestionDirectory(); // 创建题目目录
        displayQuestion(currentQuestions.get(currentQuestionIndex));
        updateQuestionButtonColors(); // 初始化按钮颜色
        studentCardLayout.show(mainContentPanel, STUDENT_EXAM_PANEL); // 切换到考试面板
        
        // 初始化并启动计时器
        remainingTime = currentQuestions.size() * 20; // 每道题20秒
        updateTimerLabel();
        if (examTimer != null && examTimer.isRunning()) {
            examTimer.stop();
        }
        examTimer = new Timer(1000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                remainingTime--;
                updateTimerLabel();
                if (remainingTime <= 0) {
                    examTimer.stop();
                    JOptionPane.showMessageDialog(StudentPanel.this, "考试时间到！系统将自动提交您的答案。", "时间到", JOptionPane.WARNING_MESSAGE);
                    submitExam();
                }
            }
        });
        examTimer.start();
    }
    

    

    
    private JPanel createExamPanelInternal() { // Renamed to avoid conflict
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(230, 240, 250)); // Set background color to match login panel
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        questionCountLabel = new JLabel();
        questionCountLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoPanel.add(questionCountLabel);

        timerLabel = new JLabel("考试时间：00:00");
        timerLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        infoPanel.add(Box.createHorizontalStrut(50)); // 添加一些间隔
        infoPanel.add(timerLabel);

        panel.add(infoPanel, BorderLayout.NORTH);
        
        questionDisplayArea = new JPanel();
        questionDisplayArea.setLayout(new BoxLayout(questionDisplayArea, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionDisplayArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 创建题目目录面板
        questionDirectoryPanel = new JPanel();
        questionDirectoryPanel.setLayout(new BoxLayout(questionDirectoryPanel, BoxLayout.Y_AXIS));
        questionDirectoryPanel.setBorder(BorderFactory.createTitledBorder("题目目录"));
        JScrollPane directoryScrollPane = new JScrollPane(questionDirectoryPanel);
        directoryScrollPane.setPreferredSize(new Dimension(150, 0)); // 设置目录面板的推荐宽度
        panel.add(directoryScrollPane, BorderLayout.EAST);
        
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

    private void displayQuestion(Question question) {
        questionDisplayArea.removeAll();
        
        JLabel typeLabel = new JLabel("题目类型：" + question.getType().getDescription());
        typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        questionDisplayArea.add(typeLabel);
        questionDisplayArea.add(Box.createVerticalStrut(10));
        
        JLabel questionLabel = new JLabel("<html><body style='width: 500px'>" + 
            question.getQuestionText() + "</body></html>");
        questionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        questionDisplayArea.add(questionLabel);
        questionDisplayArea.add(Box.createVerticalStrut(20));
        
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
        
        questionCountLabel.setText(String.format("第 %d/%d 题", currentQuestionIndex + 1, currentQuestions.size()));
        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < currentQuestions.size() - 1);

        updateQuestionButtonColors(); // 更新按钮颜色
        
        questionDisplayArea.revalidate();
        questionDisplayArea.repaint();
    }

    private void displaySingleChoiceQuestion(Question question) {
        answerGroup = new ButtonGroup(); // Re-initialize for each question
        JRadioButton[] options = new JRadioButton[4];
        String[] optionLetters = {"A", "B", "C", "D"};
        String[] optionTexts = { question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD() };
        
        for (int i = 0; i < 4; i++) {
            if (optionTexts[i] == null) continue; // Skip if option text is null
            options[i] = new JRadioButton(optionTexts[i]);
            options[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            options[i].setActionCommand(optionLetters[i]);
            answerGroup.add(options[i]);
            questionDisplayArea.add(options[i]);
            questionDisplayArea.add(Box.createVerticalStrut(10));
        }
        
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null && !previousAnswer.isEmpty()) {
            for (int i=0; i<4; i++) {
                 if(options[i] != null && options[i].getActionCommand().equals(previousAnswer)) {
                    options[i].setSelected(true);
                    break;
                }
            }
        }
    }

    private void displayMultipleChoiceQuestion(Question question) {
        JCheckBox[] options = new JCheckBox[4];
        String[] optionLetters = {"A", "B", "C", "D"};
        String[] optionTexts = { question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD() };

        for (int i = 0; i < 4; i++) {
            if (optionTexts[i] == null) continue;
            options[i] = new JCheckBox(optionTexts[i]);
            options[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            options[i].setActionCommand(optionLetters[i]);
            questionDisplayArea.add(options[i]);
            questionDisplayArea.add(Box.createVerticalStrut(10));
        }
        
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null && !previousAnswer.isEmpty()) {
            List<String> selectedLetters = Arrays.asList(previousAnswer.split(","));
            for (int i=0; i<4; i++) {
                if(options[i]!=null && selectedLetters.contains(options[i].getActionCommand())){
                    options[i].setSelected(true);
                }
            }
        }
    }

    private void displayFillBlankQuestion(Question question) {
        JTextField answerField = new JTextField(30);
        answerField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        answerField.setMaximumSize(new Dimension(400, 30));
        String previousAnswer = studentAnswers.get(question.getId());
        if (previousAnswer != null) {
            answerField.setText(previousAnswer);
        }
        questionDisplayArea.add(new JLabel("请输入答案:"));
        questionDisplayArea.add(answerField);
    }

    private void showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion(currentQuestions.get(currentQuestionIndex));
        }
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion(currentQuestions.get(currentQuestionIndex));
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
    }

    private void saveCurrentAnswer() {
        if (currentQuestions.isEmpty() || currentQuestionIndex < 0 || currentQuestionIndex >= currentQuestions.size()) {
            return; // Avoid errors if no questions or index out of bounds
        }
        Question currentQuestion = currentQuestions.get(currentQuestionIndex);
        String answer = "";
        
        switch (currentQuestion.getType()) {
            case SINGLE_CHOICE:
                if (answerGroup != null) { // Check if answerGroup is initialized
                    ButtonModel selectedModel = answerGroup.getSelection();
                    if (selectedModel != null) {
                        answer = selectedModel.getActionCommand();
                    }
                }
                break;
            case MULTIPLE_CHOICE:
                List<String> selectedAnswers = new ArrayList<>();
                for (Component comp : questionDisplayArea.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) comp;
                        if (checkBox.isSelected()) {
                            selectedAnswers.add(checkBox.getActionCommand());
                        }
                    }
                }
                answer = String.join(",", selectedAnswers);
                break;
            case FILL_BLANK:
                for (Component comp : questionDisplayArea.getComponents()) {
                    if (comp instanceof JTextField) {
                        JTextField textField = (JTextField) comp;
                        answer = textField.getText().trim();
                        break;
                    }
                }
                break;
        }
        studentAnswers.put(currentQuestion.getId(), answer);
        updateQuestionButtonColors(); // 更新按钮颜色
    }

    private void submitExam() {
        saveCurrentAnswer();
        ExamService.ExamResult examResult = examService.submitExam(
            currentStudent.getStudentId(),
            currentQuestions.get(0).getSubject(),
            currentQuestions, 
            studentAnswers
        );

        if (examResult.getExamRecordId() > 0) {
            JOptionPane.showMessageDialog(this,
                String.format("考试完成！得分：%d/%d (%.1f%%)",
                    examResult.getCorrectCount(), examResult.getTotalQuestions(), examResult.getPercentage()),
                "考试结果", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "提交考试失败或未能保存考试记录！", "错误", JOptionPane.ERROR_MESSAGE);
        }
        // 停止计时器
        if (examTimer != null && examTimer.isRunning()) {
            examTimer.stop();
        }
        studentCardLayout.show(mainContentPanel, STUDENT_MENU_PANEL); // 返回学生主菜单
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
    }
    private void showExamHistory() {
        List<ExamRecord> records = examService.getStudentExamHistory(currentStudent.getStudentId());
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this, "暂无考试记录！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] columnNames = {"科目", "得分", "总分", "百分比", "等级", "考试时间", "操作"};
        Object[][] data = new Object[records.size()][columnNames.length];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < records.size(); i++) {
            ExamRecord record = records.get(i);
            data[i][0] = record.getSubject();
            data[i][1] = record.getScore();
            data[i][2] = record.getTotalQuestions();
            data[i][3] = String.format("%.1f%%", record.getScorePercentage());
            data[i][4] = record.getScorePercentage() >= 90 ? "优秀" : record.getScorePercentage() >= 80 ? "良好" : record.getScorePercentage() >= 70 ? "中等" : record.getScorePercentage() >= 60 ? "及格" : "不及格";
            data[i][5] = sdf.format(record.getExamDate());
            data[i][6] = "查看详情";
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override public boolean isCellEditable(int row, int column) { return column == 6; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        table.setRowHeight(30);
        TableColumn actionColumn = table.getColumnModel().getColumn(6);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), table, records, this)); // Pass StudentPanel instance
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "考试历史记录", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showExamDetailDialog(ExamRecord record) { // Made public to be called by ButtonEditor
        try {
            List<StudentAnswerDetail> details = studentAnswerDetailDAO.getStudentAnswerDetailsByExamRecordId(record.getId());
            if (details.isEmpty()) {
                JOptionPane.showMessageDialog(this, "未能加载到该次考试的详细答题记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JDialog detailDialog = new JDialog(mainFrame, "考试详情 - " + record.getSubject(), true); 
            detailDialog.setSize(700, 500);
            detailDialog.setLocationRelativeTo(mainFrame);
            detailDialog.setLayout(new BorderLayout());

            // Display Exam Comment at the top of the dialog
            JTextArea commentDisplayArea = new JTextArea("管理员评语: " + record.getComment());
            commentDisplayArea.setLineWrap(true);
            commentDisplayArea.setWrapStyleWord(true);
            commentDisplayArea.setEditable(false);
            commentDisplayArea.setOpaque(true);
            commentDisplayArea.setBackground(new Color(245, 245, 220)); // Light yellow background for comments
            commentDisplayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            commentDisplayArea.setFont(new Font("微软雅黑", Font.ITALIC, 13));
            
            // Only add if there is a comment
            if (record.getComment() != null && !record.getComment().trim().isEmpty()) {
                detailDialog.add(commentDisplayArea, BorderLayout.NORTH);
            }

            JPanel questionsDisplayPanel = new JPanel();
            questionsDisplayPanel.setLayout(new BoxLayout(questionsDisplayPanel, BoxLayout.Y_AXIS));
            questionsDisplayPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            for (int i = 0; i < details.size(); i++) {
                StudentAnswerDetail detail = details.get(i);
                JPanel singleQuestionPanel = new JPanel(new BorderLayout(5,5));
                singleQuestionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
                JTextArea questionTextLabel = new JTextArea((i + 1) + ". " + detail.getQuestionText());
                questionTextLabel.setWrapStyleWord(true); questionTextLabel.setLineWrap(true);
                questionTextLabel.setEditable(false); questionTextLabel.setOpaque(false);
                questionTextLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
                singleQuestionPanel.add(questionTextLabel, BorderLayout.NORTH);
                JPanel answerPanel = new JPanel();
                answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
                answerPanel.setOpaque(false);
                if (detail.getQuestionType() == QuestionType.SINGLE_CHOICE || detail.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                    if (detail.getOptionA() != null) answerPanel.add(new JLabel("A. " + detail.getOptionA()));
                    if (detail.getOptionB() != null) answerPanel.add(new JLabel("B. " + detail.getOptionB()));
                    if (detail.getOptionC() != null) answerPanel.add(new JLabel("C. " + detail.getOptionC()));
                    if (detail.getOptionD() != null) answerPanel.add(new JLabel("D. " + detail.getOptionD()));
                    answerPanel.add(Box.createVerticalStrut(5));
                }
                String studentAnswerDisplay = formatAnswerWithOptions(detail, detail.getStudentAnswer());
                JLabel studentAnswerLabel = new JLabel("你的答案: " + studentAnswerDisplay);
                studentAnswerLabel.setForeground(detail.isCorrect() ? new Color(0,128,0) : Color.RED);
                answerPanel.add(studentAnswerLabel);
                String correctAnswerDisplay = formatAnswerWithOptions(detail, detail.getCorrectAnswer());
                JLabel correctAnswerLabel = new JLabel("正确答案: " + correctAnswerDisplay);
                answerPanel.add(correctAnswerLabel);
                singleQuestionPanel.add(answerPanel, BorderLayout.CENTER);
                questionsDisplayPanel.add(singleQuestionPanel);
            }
            JScrollPane scrollPane = new JScrollPane(questionsDisplayPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            detailDialog.add(scrollPane, BorderLayout.CENTER);

            // 确保在对话框完全显示和布局后滚动到顶部
            // 这一步必须在dialog.setVisible(true)之前执行，以确保在对话框显示时就处于顶部
            SwingUtilities.invokeLater(() -> {
                scrollPane.getVerticalScrollBar().setValue(0);
            });

            JButton closeButton = new JButton("关闭");
            closeButton.addActionListener(e -> detailDialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            detailDialog.add(buttonPanel, BorderLayout.SOUTH);
            detailDialog.setVisible(true);

        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, "加载考试详情时发生数据库错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String formatAnswerWithOptions(StudentAnswerDetail detail, String answerLetters) {
        if (answerLetters == null || answerLetters.isEmpty()) return "未作答";
        List<String> formattedAnswers = new ArrayList<>();
        String[] letters = answerLetters.split(",");
        for (String letter : letters) {
            String trimmedLetter = letter.trim();
            String optionText = "";
            if (detail.getQuestionType() == QuestionType.FILL_BLANK) {
                return answerLetters; // For fill-blank, just return the answer itself
            }
            switch (trimmedLetter) {
                case "A": optionText = detail.getOptionA(); break;
                case "B": optionText = detail.getOptionB(); break;
                case "C": optionText = detail.getOptionC(); break;
                case "D": optionText = detail.getOptionD(); break;
                default: optionText = trimmedLetter;
            }
            formattedAnswers.add(trimmedLetter + ". " + (optionText == null ? "" : optionText) );
        }
        return String.join(", ", formattedAnswers);
    }


    private void createQuestionDirectory() {
        questionDirectoryPanel.removeAll();
        questionButtons.clear();
        for (int i = 0; i < currentQuestions.size(); i++) {
            JButton questionButton = new JButton("题目 " + (i + 1));
            final int questionIdx = i;
            questionButton.addActionListener(e -> {
                saveCurrentAnswer(); // 保存当前题目答案
                currentQuestionIndex = questionIdx;
                displayQuestion(currentQuestions.get(currentQuestionIndex));
                updateQuestionButtonColors(); // 更新按钮颜色
            });
            questionDirectoryPanel.add(questionButton);
            questionButtons.add(questionButton);
        }
        questionDirectoryPanel.revalidate();
        questionDirectoryPanel.repaint();
    }

    private void updateQuestionButtonColors() {
        for (int i = 0; i < questionButtons.size(); i++) {
            JButton button = questionButtons.get(i);
            if (studentAnswers.containsKey(currentQuestions.get(i).getId()) && 
                studentAnswers.get(currentQuestions.get(i).getId()) != null && 
                !studentAnswers.get(currentQuestions.get(i).getId()).isEmpty()) {
                button.setBackground(Color.BLUE);
                button.setForeground(Color.WHITE); // 设置文字颜色以便在蓝色背景下可见
            } else {
                button.setBackground(null); // 恢复默认背景色
                button.setForeground(null); // 恢复默认前景色
            }
            // 高亮当前题目按钮
            if (i == currentQuestionIndex) {
                button.setBackground(Color.ORANGE); 
            } else if (!(studentAnswers.containsKey(currentQuestions.get(i).getId()) && 
                         studentAnswers.get(currentQuestions.get(i).getId()) != null && 
                         !studentAnswers.get(currentQuestions.get(i).getId()).isEmpty())) {
                // 如果不是当前题目，且未作答，则恢复默认颜色
                button.setBackground(null);
                button.setForeground(null);
            }
        }
    }

    private void updateTimerLabel() {
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        timerLabel.setText(String.format("考试时间：%02d:%02d", minutes, seconds));
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                                     boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        
        // Add these imports explicitly as they're in subpackages
        private JTable table;
        private List<ExamRecord> records;
        private StudentPanel studentPanel; // Reference to the parent panel

        public ButtonEditor(JCheckBox checkBox, JTable table, List<ExamRecord> records, StudentPanel studentPanel) {
            super(checkBox);
            this.table = table;
            this.records = records;
            this.studentPanel = studentPanel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
            this.label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.convertRowIndexToModel(table.getEditingRow()); // Use convertRowIndexToModel for sorting/filtering
                if (selectedRow >= 0 && selectedRow < records.size()) {
                    ExamRecord selectedRecord = records.get(selectedRow);
                    studentPanel.showExamDetailDialog(selectedRecord); // Call method on StudentPanel instance
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() { super.fireEditingStopped(); }
    }
}