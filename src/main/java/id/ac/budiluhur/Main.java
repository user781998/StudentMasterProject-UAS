package id.ac.budiluhur;

import dao.StudentDAO;
import service.StudentService;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                // Membuat akses database
                StudentDAO studentDAO = new StudentDAO();

                // Membuat layanan mahasiswa
                StudentService studentService =
                        new StudentService(studentDAO);

                // Membuka tampilan aplikasi
                MainFrame mainFrame =
                        new MainFrame(studentService);

                mainFrame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Aplikasi gagal dijalankan:\n"
                                + e.getMessage(),
                        "Application Error",
                        JOptionPane.ERROR_MESSAGE
                );

                e.printStackTrace();
            }
        });
    }
}