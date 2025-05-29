package com.example.dao;

import com.example.database.DatabaseManager;
import com.example.model.QuestionType;
import com.example.model.StudentAnswerDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for StudentAnswerDetail.
 * 负责学生单题作答详情的数据库操作。
 */
public class StudentAnswerDetailDAO {

    private DatabaseManager dbManager;

    public StudentAnswerDetailDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * 保存单条学生答题记录
     * @param detail 学生答题详情对象
     * @return 保存成功返回true，否则返回false
     */
    public boolean saveStudentAnswerDetail(StudentAnswerDetail detail) {
        String sql = "INSERT INTO student_answer_details (exam_record_id, question_id, student_answer, is_correct, correct_answer, question_text, option_a, option_b, option_c, option_d, question_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, detail.getExamRecordId());
            pstmt.setInt(2, detail.getQuestionId());
            pstmt.setString(3, detail.getStudentAnswer());
            pstmt.setBoolean(4, detail.isCorrect());
            pstmt.setString(5, detail.getCorrectAnswer());
            pstmt.setString(6, detail.getQuestionText());
            pstmt.setString(7, detail.getOptionA());
            pstmt.setString(8, detail.getOptionB());
            pstmt.setString(9, detail.getOptionC());
            pstmt.setString(10, detail.getOptionD());
            pstmt.setString(11, detail.getQuestionType() != null ? detail.getQuestionType().name() : null);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        detail.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量保存学生答题记录
     * @param details 学生答题详情列表
     * @return 全部保存成功返回true，否则返回false
     */
    public boolean saveAllStudentAnswerDetails(List<StudentAnswerDetail> details) {
        // 注意：对于某些数据库，更有效的方法是使用批处理 (batch update)
        // 这里为了简单起见，逐条保存，但在生产环境中应考虑优化
        for (StudentAnswerDetail detail : details) {
            if (!saveStudentAnswerDetail(detail)) {
                return false; // 如果有任何一条保存失败，则返回false
            }
        }
        return true;
    }

    /**
     * 根据考试记录ID获取学生的所有答题详情
     * @param examRecordId 考试记录ID
     * @return 学生答题详情列表
     */
    public List<StudentAnswerDetail> getStudentAnswerDetailsByExamRecordId(int examRecordId) {
        List<StudentAnswerDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM student_answer_details WHERE exam_record_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, examRecordId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StudentAnswerDetail detail = new StudentAnswerDetail();
                detail.setId(rs.getInt("id"));
                detail.setExamRecordId(rs.getInt("exam_record_id"));
                detail.setQuestionId(rs.getInt("question_id"));
                detail.setStudentAnswer(rs.getString("student_answer"));
                detail.setCorrect(rs.getBoolean("is_correct"));
                detail.setCorrectAnswer(rs.getString("correct_answer"));
                detail.setQuestionText(rs.getString("question_text"));
                detail.setOptionA(rs.getString("option_a"));
                detail.setOptionB(rs.getString("option_b"));
                detail.setOptionC(rs.getString("option_c"));
                detail.setOptionD(rs.getString("option_d"));
                String questionTypeStr = rs.getString("question_type");
                if (questionTypeStr != null) {
                    try {
                        detail.setQuestionType(QuestionType.valueOf(questionTypeStr));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace(); // 或者记录日志，处理无法识别的类型
                        detail.setQuestionType(null); 
                    }
                } else {
                    detail.setQuestionType(null);
                }
                // 如果有解析字段，在这里获取并设置
                // detail.setAnalysis(rs.getString("analysis"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 在生产环境中，这里应该抛出自定义异常或更好地处理错误
        }
        return details;
    }
} 