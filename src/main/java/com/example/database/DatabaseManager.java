package com.example.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类
 * 负责数据库连接、表创建和基本操作
 */
public class DatabaseManager {
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/exam_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Yzznb6782!";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        System.out.println("DatabaseManager: Initializing database..."); // Added logging
        initDatabase();
        System.out.println("DatabaseManager: Database initialization finished."); // Added logging
    }

    private void initDatabase() {
        try {
            System.out.println("DatabaseManager: Attempting to get connection..."); // Added logging
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("DatabaseManager: Connection obtained successfully."); // Added logging
            System.out.println("DatabaseManager: Tables created."); // Added logging
            insertSampleData();
            createTables();
            System.out.println("DatabaseManager: Sample data inserted."); // Added logging
        } catch (SQLException e) {
            System.err.println("DatabaseManager: Error during initialization: " + e.getMessage()); // Added logging
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            System.out.println("DatabaseManager: Creating new instance."); // Added logging
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * 创建数据库表
     */
    private void createTables() throws SQLException {
        // 创建学生表
        String createStudentTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INT PRIMARY KEY AUTO_INCREMENT,
                student_id VARCHAR(50) UNIQUE NOT NULL,
                name VARCHAR(100) NOT NULL,
                password VARCHAR(100) NOT NULL
            )
            """;

        // 创建题目表
        String createQuestionTable = """
            CREATE TABLE IF NOT EXISTS questions (
                id INT PRIMARY KEY AUTO_INCREMENT,
                question_text TEXT NOT NULL,
                option_a VARCHAR(200),
                option_b VARCHAR(200),
                option_c VARCHAR(200),
                option_d VARCHAR(200),
                correct_answer VARCHAR(200),
                correct_answers TEXT,
                fill_blank_answer TEXT,
                question_type VARCHAR(20) NOT NULL,
                subject VARCHAR(50) NOT NULL
            )
            """;

        // 创建考试记录表
        String createExamRecordTable = """
            CREATE TABLE IF NOT EXISTS exam_records (
                id INT PRIMARY KEY AUTO_INCREMENT,
                student_id VARCHAR(50) NOT NULL,
                subject VARCHAR(50) NOT NULL,
                score INT NOT NULL,
                total_questions INT NOT NULL,
                exam_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(student_id)
            )
              """;

        // 创建管理员表
        String createAdminTable = """
            CREATE TABLE IF NOT EXISTS admins (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL
            )
            """;

            String createStudentAnswerDetailsTable = """
                create table student_answer_details
(
    id             int auto_increment
        primary key,
    exam_record_id int         null,
    question_id    int         null,
    student_answer text        null,
    is_correct     tinyint(1)  null,
    correct_answer text        null,
    question_text  text        null,
    option_a       text        null,
    option_b       text        null,
    option_c       text        null,
    option_d       text        null,
    question_type  varchar(50) null,
    constraint student_answer_details_ibfk_1
        foreign key (exam_record_id) references exam_records (id)
            on delete cascade,
    constraint student_answer_details_ibfk_2
        foreign key (question_id) references questions (id)
            on delete set null
)
                )
                """;

                String createSubjectsTable = """
                    create table subjects
(
    id   int auto_increment
        primary key,
    name varchar(255) not null,
    constraint name
        unique (name)
)
                    """;

        Statement stmt = connection.createStatement();
        stmt.execute(createStudentTable);
        stmt.execute(createQuestionTable);
        stmt.execute(createExamRecordTable);
        stmt.execute(createAdminTable);
        stmt.execute(createStudentAnswerDetailsTable);
        stmt.execute(createSubjectsTable);
        stmt.close();
    }

    /**
     * 插入示例数据
     */
    private void insertSampleData() throws SQLException {
        // 检查管理员表是否已有数据
        String checkAdminData = "SELECT COUNT(*) FROM admins";
        Statement stmtAdmin = connection.createStatement();
        ResultSet rsAdmin = stmtAdmin.executeQuery(checkAdminData);
        rsAdmin.next();
        boolean adminExists = rsAdmin.getInt(1) > 0;
        rsAdmin.close();
        stmtAdmin.close();

        // 如果管理员表没有数据，插入示例管理员
        if (!adminExists) {
            String insertAdmin = "INSERT INTO admins (username, password) VALUES (?, ?)";
            PreparedStatement pstmtAdmin = connection.prepareStatement(insertAdmin);
            pstmtAdmin.setString(1, "admin");
            pstmtAdmin.setString(2, "admin123"); // 请在实际应用中加密密码
            pstmtAdmin.executeUpdate();
            pstmtAdmin.close();
        }

        // 检查学生表是否已有数据，如果没有则插入示例学生和题目
        String checkStudentData = "SELECT COUNT(*) FROM students";
        Statement stmtStudent = connection.createStatement();
        ResultSet rsStudent = stmtStudent.executeQuery(checkStudentData);
        rsStudent.next();
        boolean studentExists = rsStudent.getInt(1) > 0;
        rsStudent.close();
        stmtStudent.close();

        if (!studentExists) {
            // 插入示例题目
            insertSampleQuestions();
        }

    }

    /**
     * 插入示例题目
     */
    private void insertSampleQuestions() throws SQLException {
        String insertQuestion = """
            INSERT INTO questions (
                question_text, option_a, option_b, option_c, option_d, 
                correct_answer, correct_answers, fill_blank_answer, 
                question_type, subject
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        PreparedStatement pstmt = connection.prepareStatement(insertQuestion);

        // Java单选题
        String[][] javaSingleChoiceQuestions = {
            {"Java中哪个关键字用于继承？", "extends", "implements", "inherit", "super", "A", null, null, "SINGLE_CHOICE", "Java"},
            {"Java中String类是否可变？", "可变", "不可变", "取决于JVM", "取决于编译器", "B", null, null, "SINGLE_CHOICE", "Java"},
            {"Java中哪个集合类是线程安全的？", "ArrayList", "HashMap", "Vector", "LinkedList", "C", null, null, "SINGLE_CHOICE", "Java"}
        };

        // Java多选题
        String[][] javaMultipleChoiceQuestions = {
            {"以下哪些是Java的基本数据类型？", "int", "String", "boolean", "char", null, "A,C,D", null, "MULTIPLE_CHOICE", "Java"},
            {"以下哪些是Java的访问修饰符？", "public", "private", "protected", "default", null, "A,B,C,D", null, "MULTIPLE_CHOICE", "Java"}
        };

        // Java填空题
        String[][] javaFillBlankQuestions = {
            {"Java中用于创建新线程的类是_____。", null, null, null, null, null, null, "Thread", "FILL_BLANK", "Java"},
            {"Java中用于处理异常的机制是_____。", null, null, null, null, null, null, "try-catch", "FILL_BLANK", "Java"}
        };

        // 插入Java单选题
        for (String[] question : javaSingleChoiceQuestions) {
            for (int i = 0; i < question.length; i++) {
                pstmt.setString(i + 1, question[i]);
            }
            pstmt.executeUpdate();
        }

        // 插入Java多选题
        for (String[] question : javaMultipleChoiceQuestions) {
            for (int i = 0; i < question.length; i++) {
                pstmt.setString(i + 1, question[i]);
            }
            pstmt.executeUpdate();
        }

        // 插入Java填空题
        for (String[] question : javaFillBlankQuestions) {
            for (int i = 0; i < question.length; i++) {
                pstmt.setString(i + 1, question[i]);
            }
            pstmt.executeUpdate();
        }

        pstmt.close();
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() {
        try {
            System.out.println("DatabaseManager: getConnection() called. Connection is null: " + (connection == null) + ", isClosed: " + (connection != null && connection.isClosed())); // Added logging
            if (connection == null || connection.isClosed()) {
                System.out.println("DatabaseManager: Connection is null or closed. Re-initializing..."); // Added logging
                initDatabase(); // Re-initialize connection if it's null or closed
                System.out.println("DatabaseManager: Re-initialization finished. New connection is null: " + (connection == null) + ", isClosed: " + (connection != null && connection.isClosed())); // Added logging
            }
        } catch (SQLException e) {
            System.err.println("DatabaseManager: Error checking connection state: " + e.getMessage()); // Added logging
            e.printStackTrace();
            // Handle exception, maybe return null or throw a runtime exception
        }
        System.out.println("DatabaseManager: Returning connection."); // Added logging
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection() {
        try {
            System.out.println("DatabaseManager: closeConnection() called. Connection is null: " + (connection == null) + ", isClosed: " + (connection != null && connection.isClosed())); // Added logging
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("DatabaseManager: Connection closed successfully."); // Added logging
            }
        } catch (SQLException e) {
            System.err.println("DatabaseManager: Error closing connection: " + e.getMessage()); // Added logging
            e.printStackTrace();
        }
    }
}