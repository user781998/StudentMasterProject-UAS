package dao;

import database.DatabaseConnector;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements IStudentDAO {

    @Override
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT student_id, name, email, photo_path, background_color
                FROM students
                ORDER BY student_id
                """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        }

        return students;
    }

    @Override
    public List<Student> getStudentsByIdRange(
            int minId, int maxId
    ) throws SQLException {

        List<Student> students = new ArrayList<>();

        String sql = """
                SELECT student_id, name, email, photo_path, background_color
                FROM students
                WHERE student_id BETWEEN ? AND ?
                ORDER BY student_id
                """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minId);
            stmt.setInt(2, maxId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        }

        return students;
    }

    @Override
    public boolean addStudent(Student student) throws SQLException {

        String sql = """
                INSERT INTO students
                (student_id, name, email, photo_path, background_color)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, student.getStudentId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhotoPath());
            stmt.setString(5, student.getBackgroundColor());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStudent(Student student) throws SQLException {

        String sql = """
                UPDATE students
                SET name = ?, email = ?, photo_path = ?,
                    background_color = ?
                WHERE student_id = ?
                """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhotoPath());
            stmt.setString(4, student.getBackgroundColor());
            stmt.setInt(5, student.getStudentId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteStudent(int studentId) throws SQLException {

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            return stmt.executeUpdate() > 0;
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("photo_path"),
                rs.getString("background_color")
        );
    }
}