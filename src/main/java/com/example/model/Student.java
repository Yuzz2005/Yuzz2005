package com.example.model;

/**
 * 学生实体类
 */
public class Student {
    private int id;
    private String studentId;
    private String name;
    private String password;

    public Student() {}

    public Student(String studentId, String name, String password) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
    }

    public Student(int id, String studentId, String name, String password) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                "}";
    }
}