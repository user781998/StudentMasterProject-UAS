
package service;

import dao.IStudentDAO;
import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class StudentService {

    private final IStudentDAO studentDAO;

    // Dependency injection melalui constructor
    public StudentService(IStudentDAO studentDAO) {
        this.studentDAO = Objects.requireNonNull(studentDAO);
    }

    // Menampilkan semua mahasiswa
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    // Mengambil mahasiswa berdasarkan rentang ID
    public List<Student> getStudentsByIdRange(
            int minId, int maxId
    ) throws SQLException {

        if (minId <= 0 || maxId <= 0 || minId > maxId) {
            throw new IllegalArgumentException(
                    "Rentang Student ID tidak valid."
            );
        }

        return studentDAO.getStudentsByIdRange(minId, maxId);
    }

    // Menambahkan mahasiswa
    public boolean addStudent(Student student) throws SQLException {
        validateStudent(student);
        return studentDAO.addStudent(student);
    }

    // Mengubah data mahasiswa
    public boolean updateStudent(Student student) throws SQLException {
        validateStudent(student);
        return studentDAO.updateStudent(student);
    }

    // Menghapus mahasiswa
    public boolean deleteStudent(int studentId) throws SQLException {

        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "Student ID harus lebih dari nol."
            );
        }

        return studentDAO.deleteStudent(studentId);
    }

    // Validasi data sebelum dikirim ke database
    private void validateStudent(Student student) {

        Objects.requireNonNull(
                student,
                "Data mahasiswa tidak boleh kosong."
        );

        if (student.getStudentId() <= 0) {
            throw new IllegalArgumentException(
                    "Student ID harus lebih dari nol."
            );
        }

        if (student.getName() == null
                || student.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Nama mahasiswa wajib diisi."
            );
        }

        if (student.getEmail() == null
                || student.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Email mahasiswa wajib diisi."
            );
        }

        if (student.getPhotoPath() == null
                || student.getPhotoPath().isBlank()) {
            throw new IllegalArgumentException(
                    "Foto mahasiswa wajib dipilih."
            );
        }

        if (student.getBackgroundColor() == null
                || !student.getBackgroundColor()
                .matches("#[0-9A-Fa-f]{6}")) {
            throw new IllegalArgumentException(
                    "Warna latar harus menggunakan format #RRGGBB."
            );
        }
    }
}
