package dao;

import model.Student;
import java.sql.SQLException;
import java.util.List;

public interface IStudentDAO {

    List<Student> getAllStudents() throws SQLException;

    List<Student> getStudentsByIdRange(
            int minId, int maxId
    ) throws SQLException;

    boolean addStudent(Student student) throws SQLException;

    boolean updateStudent(Student student) throws SQLException;

    boolean deleteStudent(int studentId) throws SQLException;
}