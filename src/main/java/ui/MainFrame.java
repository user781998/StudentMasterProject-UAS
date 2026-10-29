
package ui;

import model.Student;
import service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import report.ReportGenerator;

public class MainFrame extends JFrame {

    private static final Color NAVY = new Color(28, 42, 78);

    private static final String[] COLOR_PALETTE = {
            "#E8F1FF",
            "#E7FFF4",
            "#FFF0E6",
            "#F3E8FF",
            "#FFF9CC",
            "#DDF5F7",
            "#FCE7F3",
            "#E8EDFF",
            "#EAF7E5",
            "#FFE5E5"
    };

    private final StudentService studentService;

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField photoPathField = new JTextField();
    private final JTextField colorField = new JTextField();

    private final JLabel photoPreview =
            new JLabel("Pilih foto mahasiswa", SwingConstants.CENTER);

    private final JPanel colorSwatch = new JPanel();
    private final JLabel statusLabel = new JLabel("Memuat data...");

    private final JButton addButton = new JButton("Tambah");
    private final JButton updateButton = new JButton("Ubah");
    private final JButton deleteButton = new JButton("Hapus");
    private final JButton clearButton = new JButton("Bersihkan");
    private final JButton choosePhotoButton = new JButton("Pilih Foto");
    private final JButton chooseColorButton = new JButton("Pilih Warna");
    private final JButton refreshButton = new JButton("Muat Ulang");
    private final JButton reportButton = new JButton("Laporan PDF");

    private final DefaultTableModel tableModel;
    private final JTable studentTable;

    private Integer editingStudentId = null;

    public MainFrame(StudentService studentService) {
        super("Student Master Project");

        this.studentService = Objects.requireNonNull(studentService);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setSize(1200, 760);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(
                new Object[]{
                        "Foto",
                        "Student ID",
                        "Nama",
                        "Email",
                        "Warna"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);

        configureTable();

        JPanel rootPanel = new JPanel(new BorderLayout(16, 16));
        rootPanel.setBackground(new Color(244, 247, 252));
        rootPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        rootPanel.add(buildHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(16, 16));
        contentPanel.setOpaque(false);

        JScrollPane formScrollPane =
                new JScrollPane(buildFormPanel());

        formScrollPane.setPreferredSize(new Dimension(340, 0));
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        contentPanel.add(formScrollPane, BorderLayout.WEST);
        contentPanel.add(buildTablePanel(), BorderLayout.CENTER);

        rootPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(rootPanel);

        connectEvents();
        refreshTable();
        clearForm();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("STUDENT MASTER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel subtitle = new JLabel(
                "Manajemen data mahasiswa • CRUD Database"
        );
        subtitle.setForeground(new Color(220, 229, 245));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        header.add(titles, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(224, 230, 240)
                ),
                new EmptyBorder(18, 16, 18, 16)
        ));

        JLabel heading = new JLabel("Data Mahasiswa");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(NAVY);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(18));

        addInputField(panel, "Student ID", idField);
        addInputField(panel, "Nama Lengkap", nameField);
        addInputField(panel, "Email", emailField);
        addInputField(panel, "Lokasi Foto", photoPathField);
        photoPathField.setEditable(false);

        choosePhotoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(choosePhotoButton);
        panel.add(Box.createVerticalStrut(12));

        photoPreview.setPreferredSize(new Dimension(200, 125));
        photoPreview.setMinimumSize(new Dimension(150, 100));
        photoPreview.setMaximumSize(new Dimension(280, 145));
        photoPreview.setOpaque(true);
        photoPreview.setBackground(new Color(245, 247, 251));
        photoPreview.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 235)
                )
        );
        photoPreview.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(photoPreview);
        panel.add(Box.createVerticalStrut(15));

        colorField.setEditable(false);
        addInputField(panel, "Warna Latar", colorField);

        colorSwatch.setPreferredSize(new Dimension(30, 28));
        colorSwatch.setBorder(
                BorderFactory.createLineBorder(Color.GRAY)
        );

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        colorPanel.setBackground(Color.WHITE);
        colorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorPanel.add(colorSwatch);
        colorPanel.add(chooseColorButton);

        panel.add(colorPanel);
        panel.add(Box.createVerticalStrut(22));

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setBackground(Color.WHITE);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 90
        ));

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        panel.add(buttons);

        return panel;
    }

    private void addInputField(
            JPanel parent,
            String labelText,
            JTextField field
    ) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 62
        ));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(65, 75, 95));

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 32
        ));

        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createVerticalStrut(10));
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(224, 230, 240)
                ),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Daftar Mahasiswa");
        heading.setForeground(NAVY);
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));

        refreshButton.setToolTipText("Muat ulang data dari database");
        reportButton.setToolTipText("Buat laporan PDF mahasiswa");

        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        actionPanel.setBackground(Color.WHITE);

        actionPanel.add(reportButton);
        actionPanel.add(refreshButton);

        top.add(heading, BorderLayout.WEST);
        top.add(actionPanel, BorderLayout.EAST);

        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 238)
                )
        );

        statusLabel.setForeground(new Color(80, 90, 110));
        statusLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        panel.add(top, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void configureTable() {
        studentTable.setRowHeight(58);
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        studentTable.setAutoCreateRowSorter(true);
        studentTable.setFillsViewportHeight(true);
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(224, 229, 238));
        studentTable.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        studentTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        studentTable.getTableHeader().setBackground(
                new Color(235, 240, 249)
        );

        for (int column = 1; column < 5; column++) {
            studentTable.getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(new StudentRowRenderer());
        }

        studentTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(new PhotoRenderer());

        studentTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(70);

        studentTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(90);

        studentTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(155);

        studentTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(200);

        studentTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(90);
    }

    private void connectEvents() {
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());

        choosePhotoButton.addActionListener(e -> choosePhoto());
        chooseColorButton.addActionListener(e -> chooseColor());
        refreshButton.addActionListener(e -> refreshTable());
        reportButton.addActionListener(e -> generateReport());

        studentTable.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        loadSelectedStudent();
                    }
                });
    }

    private void addStudent() {
        if (editingStudentId != null) {
            showError(
                    "Klik Bersihkan sebelum menambahkan mahasiswa baru."
            );
            return;
        }

        try {
            Student student = readStudentFromForm();

            ensureUniqueAppearance(student, -1);

            if (studentService.addStudent(student)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data mahasiswa berhasil ditambahkan."
                );

                refreshTable();
                clearForm();
            }

        } catch (SQLException | IOException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateStudent() {
        if (editingStudentId == null) {
            showError(
                    "Pilih baris mahasiswa yang ingin diubah terlebih dahulu."
            );
            return;
        }

        try {
            Student student = readStudentFromForm();

            ensureUniqueAppearance(student, editingStudentId);

            if (studentService.updateStudent(student)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data mahasiswa berhasil diperbarui."
                );

                refreshTable();
                clearForm();
            } else {
                showError("Data tidak ditemukan atau tidak berubah.");
            }

        } catch (SQLException | IOException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteStudent() {
        if (editingStudentId == null) {
            showError(
                    "Pilih baris mahasiswa yang ingin dihapus terlebih dahulu."
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Hapus mahasiswa dengan ID " + editingStudentId + "?",
                "Konfirmasi Penghapusan",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (studentService.deleteStudent(editingStudentId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data mahasiswa berhasil dihapus."
                );

                refreshTable();
                clearForm();
            } else {
                showError("Data mahasiswa tidak ditemukan.");
            }

        } catch (SQLException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void generateReport() {

        JTextField minField = new JTextField("1001");
        JTextField maxField = new JTextField("1010");

        JPanel rangePanel = new JPanel(
                new GridLayout(2, 2, 8, 8)
        );

        rangePanel.add(new JLabel("Student ID minimum:"));
        rangePanel.add(minField);

        rangePanel.add(new JLabel("Student ID maksimum:"));
        rangePanel.add(maxField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                rangePanel,
                "Rentang Student ID untuk Laporan",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int minId = Integer.parseInt(
                    minField.getText().trim()
            );

            int maxId = Integer.parseInt(
                    maxField.getText().trim()
            );

            if (minId <= 0 || maxId < minId) {
                throw new IllegalArgumentException(
                        "Rentang Student ID tidak valid."
                );
            }

            Path pdfPath = ReportGenerator.generatePdf(
                    minId, maxId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Laporan PDF berhasil dibuat:\n" + pdfPath,
                    "Laporan Berhasil",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfPath.toFile());
            }

        } catch (NumberFormatException ex) {
            showError(
                    "Student ID harus diisi dengan angka."
            );

        } catch (Exception ex) {
            showError(
                    "Gagal membuat laporan PDF:\n"
                            + ex.getMessage()
            );

            ex.printStackTrace();
        }
    }

    private Student readStudentFromForm() {
        int studentId = Integer.parseInt(
                idField.getText().trim()
        );

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String photoPath = photoPathField.getText().trim();
        String backgroundColor = colorField.getText().trim();

        return new Student(
                studentId,
                name,
                email,
                photoPath,
                backgroundColor
        );
    }

    private void ensureUniqueAppearance(
            Student candidate,
            int excludedStudentId
    ) throws SQLException, IOException {

        Path candidatePhoto = resolvePhotoPath(
                candidate.getPhotoPath()
        );

        if (!Files.isRegularFile(candidatePhoto)) {
            throw new IllegalArgumentException(
                    "File foto tidak ditemukan. Pilih foto terlebih dahulu."
            );
        }

        List<Student> existingStudents =
                studentService.getAllStudents();

        for (Student existing : existingStudents) {
            if (existing.getStudentId() == excludedStudentId) {
                continue;
            }

            if (existing.getBackgroundColor()
                    .equalsIgnoreCase(candidate.getBackgroundColor())) {
                throw new IllegalArgumentException(
                        "Gunakan warna latar yang berbeda untuk setiap mahasiswa."
                );
            }

            Path existingPhoto = resolvePhotoPath(
                    existing.getPhotoPath()
            );

            if (Files.isRegularFile(existingPhoto)
                    && Files.mismatch(candidatePhoto, existingPhoto) == -1) {
                throw new IllegalArgumentException(
                        "Gunakan foto yang berbeda untuk setiap mahasiswa."
                );
            }
        }
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Mahasiswa");

        chooser.setFileFilter(new FileNameExtensionFilter(
                "File gambar (JPG, JPEG, PNG, GIF)",
                "jpg", "jpeg", "png", "gif"
        ));

        if (chooser.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();

        try {
            int excludedId = editingStudentId == null
                    ? -1
                    : editingStudentId;

            if (isPhotoAlreadyUsed(
                    selectedFile.toPath(), excludedId)) {
                showError(
                        "Foto tersebut sudah digunakan mahasiswa lain. Pilih foto berbeda."
                );
                return;
            }

            Path photoDirectory = Path.of("student-photos");
            Files.createDirectories(photoDirectory);

            String rawId = idField.getText().trim();

            String safeId = rawId.matches("[A-Za-z0-9_-]+")
                    ? rawId
                    : "new";

            String originalName = selectedFile.getName();
            int dot = originalName.lastIndexOf('.');
            String extension = dot >= 0
                    ? originalName.substring(dot)
                    : ".png";

            Path destination = photoDirectory.resolve(
                    "student_" + safeId + "_"
                            + System.currentTimeMillis()
                            + extension
            );

            Files.copy(
                    selectedFile.toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String storedPath = destination.toString()
                    .replace('\\', '/');

            photoPathField.setText(storedPath);
            showPhotoPreview(storedPath);

        } catch (SQLException | IOException ex) {
            showError("Gagal memproses foto: " + ex.getMessage());
        }
    }

    private boolean isPhotoAlreadyUsed(
            Path candidatePhoto,
            int excludedStudentId
    ) throws SQLException, IOException {

        for (Student student : studentService.getAllStudents()) {
            if (student.getStudentId() == excludedStudentId) {
                continue;
            }

            Path existingPhoto = resolvePhotoPath(
                    student.getPhotoPath()
            );

            if (Files.isRegularFile(existingPhoto)
                    && Files.mismatch(
                    candidatePhoto, existingPhoto
            ) == -1) {
                return true;
            }
        }

        return false;
    }

    private void chooseColor() {
        Color initialColor = colorFromHex(colorField.getText());

        Color chosenColor = JColorChooser.showDialog(
                this,
                "Pilih Warna Latar Mahasiswa",
                initialColor
        );

        if (chosenColor != null) {
            colorField.setText(colorToHex(chosenColor));
            colorSwatch.setBackground(chosenColor);
        }
    }

    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow = studentTable.convertRowIndexToModel(
                selectedRow
        );

        editingStudentId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 1).toString()
        );

        idField.setText(editingStudentId.toString());
        idField.setEditable(false);

        nameField.setText(
                tableModel.getValueAt(modelRow, 2).toString()
        );

        emailField.setText(
                tableModel.getValueAt(modelRow, 3).toString()
        );

        String photoPath =
                tableModel.getValueAt(modelRow, 0).toString();

        photoPathField.setText(photoPath);

        String color =
                tableModel.getValueAt(modelRow, 4).toString();

        colorField.setText(color);
        colorSwatch.setBackground(colorFromHex(color));

        showPhotoPreview(photoPath);
    }

    private void refreshTable() {
        try {
            List<Student> students =
                    studentService.getAllStudents();

            tableModel.setRowCount(0);

            for (Student student : students) {
                tableModel.addRow(new Object[]{
                        student.getPhotoPath(),
                        student.getStudentId(),
                        student.getName(),
                        student.getEmail(),
                        student.getBackgroundColor()
                });
            }

            statusLabel.setText(
                    "Total mahasiswa: " + students.size()
            );

        } catch (SQLException ex) {
            statusLabel.setText("Gagal memuat data.");
            showError("Gagal memuat data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        studentTable.clearSelection();

        editingStudentId = null;

        idField.setEditable(true);
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        photoPathField.setText("");

        colorField.setText(suggestUnusedColor());
        colorSwatch.setBackground(
                colorFromHex(colorField.getText())
        );

        photoPreview.setIcon(null);
        photoPreview.setText("Pilih foto mahasiswa");
    }

    private String suggestUnusedColor() {
        Set<String> usedColors = new HashSet<>();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object value = tableModel.getValueAt(row, 4);

            if (value != null) {
                usedColors.add(value.toString().toUpperCase());
            }
        }

        for (String color : COLOR_PALETTE) {
            if (!usedColors.contains(color.toUpperCase())) {
                return color;
            }
        }

        for (int attempt = 0; attempt < 100; attempt++) {
            Color randomColor = new Color(
                    (int) (Math.random() * 0x1000000)
            );

            String hex = colorToHex(randomColor);

            if (!usedColors.contains(hex)) {
                return hex;
            }
        }

        return "#EAF2FF";
    }

    private void showPhotoPreview(String photoPath) {
        Path imagePath = resolvePhotoPath(photoPath);

        if (!Files.isRegularFile(imagePath)) {
            photoPreview.setIcon(null);
            photoPreview.setText("Foto tidak tersedia");
            return;
        }

        ImageIcon sourceIcon = new ImageIcon(imagePath.toString());

        Image scaledImage = sourceIcon.getImage()
                .getScaledInstance(180, 115, Image.SCALE_SMOOTH);

        photoPreview.setText("");
        photoPreview.setIcon(new ImageIcon(scaledImage));
    }

    private Path resolvePhotoPath(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            return Path.of("__photo_not_selected__");
        }

        return Path.of(photoPath);
    }

    private Color colorFromHex(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException | NullPointerException ex) {
            return Color.WHITE;
        }
    }

    private String colorToHex(Color color) {
        return String.format(
                "#%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message == null ? "Terjadi kesalahan." : message,
                "Informasi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private Color getRowColor(JTable table, int viewRow) {
        int modelRow = table.convertRowIndexToModel(viewRow);

        Object value = table.getModel().getValueAt(modelRow, 4);

        return colorFromHex(
                value == null ? "#FFFFFF" : value.toString()
        );
    }

    private void applyRowStyle(
            JLabel label,
            JTable table,
            int row,
            boolean selected
    ) {
        label.setOpaque(true);

        if (selected) {
            label.setBackground(table.getSelectionBackground());
            label.setForeground(table.getSelectionForeground());
        } else {
            label.setBackground(getRowColor(table, row));
            label.setForeground(new Color(40, 48, 65));
        }

        label.setBorder(new EmptyBorder(4, 8, 4, 8));
    }

    private class StudentRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            setHorizontalAlignment(
                    column == 1
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT
            );

            applyRowStyle(this, table, row, isSelected);

            return this;
        }
    }

    private class PhotoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
            );

            setHorizontalAlignment(SwingConstants.CENTER);
            applyRowStyle(this, table, row, isSelected);

            setIcon(null);
            setText("Tanpa foto");

            if (value != null) {
                Path imagePath = resolvePhotoPath(value.toString());

                if (Files.isRegularFile(imagePath)) {
                    ImageIcon sourceIcon =
                            new ImageIcon(imagePath.toString());

                    Image scaledImage = sourceIcon.getImage()
                            .getScaledInstance(
                                    44, 44, Image.SCALE_SMOOTH
                            );

                    setIcon(new ImageIcon(scaledImage));
                    setText("");
                }
            }

            return this;
        }
    }
}
