
package report;

import database.DatabaseConnector;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.design.JRDesignSection;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class ReportGenerator {

    private static final Color NAVY =
            new Color(28, 42, 78);

    private static final Color GOLD =
            new Color(229, 184, 68);

    private ReportGenerator() {
    }

    /**
     * Menghasilkan laporan PDF mahasiswa berdasarkan rentang ID.
     */
    public static Path generatePdf(
            int minId,
            int maxId
    ) throws SQLException, JRException, IOException {

        if (minId <= 0 || maxId < minId) {
            throw new IllegalArgumentException(
                    "Rentang Student ID tidak valid."
            );
        }

        JasperDesign design = createReportDesign();

        JasperReport jasperReport =
                JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("MIN_ID", minId);
        parameters.put("MAX_ID", maxId);
        parameters.put("LOGO_IMAGE", createLogo());

        Path outputDirectory = Path.of("reports");
        Files.createDirectories(outputDirectory);

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );

        Path outputFile = outputDirectory.resolve(
                "StudentReport_" + timestamp + ".pdf"
        );

        try (Connection connection =
                     DatabaseConnector.getConnection()) {

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            connection
                    );

            JasperExportManager.exportReportToPdfFile(
                    jasperPrint,
                    outputFile.toAbsolutePath().toString()
            );
        }

        return outputFile.toAbsolutePath();
    }

    private static JasperDesign createReportDesign()
            throws JRException {

        JasperDesign design = new JasperDesign();

        design.setName("StudentMasterReport");
        design.setPageWidth(595);
        design.setPageHeight(842);
        design.setLeftMargin(36);
        design.setRightMargin(36);
        design.setTopMargin(30);
        design.setBottomMargin(30);
        design.setColumnWidth(523);

        // Parameter rentang ID.
        addParameter(design, "MIN_ID", Integer.class);
        addParameter(design, "MAX_ID", Integer.class);
        addParameter(design, "LOGO_IMAGE", Image.class);

        // Field yang diambil dari query SQL.
        addField(design, "student_id", Integer.class);
        addField(design, "name", String.class);
        addField(design, "email", String.class);

        // Query SQL laporan: filter wajib berdasarkan rentang ID.
        JRDesignQuery query = new JRDesignQuery();
        query.setText(
                "SELECT student_id, name, email " +
                        "FROM students " +
                        "WHERE student_id BETWEEN $P{MIN_ID} AND $P{MAX_ID} " +
                        "ORDER BY student_id"
        );

        design.setQuery(query);

        // Header laporan dengan logo.
        JRDesignBand pageHeader = new JRDesignBand();
        pageHeader.setHeight(60);

        JRDesignImage logo = new JRDesignImage(design);
        logo.setX(0);
        logo.setY(3);
        logo.setWidth(48);
        logo.setHeight(48);
        logo.setExpression(
                new JRDesignExpression("$P{LOGO_IMAGE}")
        );

        pageHeader.addElement(logo);

        pageHeader.addElement(staticText(
                design, 60, 4, 420, 27,
                "STUDENT MASTER",
                Color.WHITE, NAVY, true, 19
        ));

        pageHeader.addElement(staticText(
                design, 60, 31, 450, 20,
                "Laporan Data Mahasiswa",
                Color.WHITE, NAVY, false, 11
        ));

        design.setPageHeader(pageHeader);

        // Judul kolom.
        JRDesignBand columnHeader = new JRDesignBand();
        columnHeader.setHeight(25);

        columnHeader.addElement(staticText(
                design, 0, 0, 75, 25,
                "Student ID",
                Color.WHITE, NAVY, true, 10
        ));

        columnHeader.addElement(staticText(
                design, 75, 0, 180, 25,
                "Nama Mahasiswa",
                Color.WHITE, NAVY, true, 10
        ));

        columnHeader.addElement(staticText(
                design, 255, 0, 268, 25,
                "Email",
                Color.WHITE, NAVY, true, 10
        ));

        design.setColumnHeader(columnHeader);

        // Baris data.
        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(25);

        detail.addElement(textField(
                design, 0, 0, 75, 25,
                "$F{student_id}", Color.WHITE,
                HorizontalTextAlignEnum.CENTER
        ));

        detail.addElement(textField(
                design, 75, 0, 180, 25,
                "$F{name}", new Color(248, 250, 254),
                HorizontalTextAlignEnum.LEFT
        ));

        detail.addElement(textField(
                design, 255, 0, 268, 25,
                "$F{email}", Color.WHITE,
                HorizontalTextAlignEnum.LEFT
        ));

        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        // Footer dengan halaman sekarang dan jumlah halaman.
        JRDesignBand footer = new JRDesignBand();
        footer.setHeight(28);

        JRDesignStaticText footerLabel = staticText(
                design, 0, 4, 310, 18,
                "Student Master Project",
                new Color(85, 95, 115),
                Color.WHITE, false, 9
        );

        footer.addElement(footerLabel);

        JRDesignTextField currentPage =
                new JRDesignTextField(design);

        currentPage.setX(350);
        currentPage.setY(4);
        currentPage.setWidth(125);
        currentPage.setHeight(18);
        currentPage.setFontName("SansSerif");
        currentPage.setFontSize(9f);
        currentPage.setHorizontalTextAlign(
                HorizontalTextAlignEnum.RIGHT
        );
        currentPage.setExpression(
                new JRDesignExpression(
                        "\"Halaman \" + $V{PAGE_NUMBER} + \" dari \""
                )
        );

        footer.addElement(currentPage);

        JRDesignTextField totalPages =
                new JRDesignTextField(design);

        totalPages.setX(475);
        totalPages.setY(4);
        totalPages.setWidth(48);
        totalPages.setHeight(18);
        totalPages.setFontName("SansSerif");
        totalPages.setFontSize(9f);
        totalPages.setExpression(
                new JRDesignExpression("$V{PAGE_NUMBER}")
        );
        totalPages.setEvaluationTime(
                EvaluationTimeEnum.REPORT
        );

        footer.addElement(totalPages);

        design.setPageFooter(footer);

        // Pesan jika rentang ID tidak menghasilkan data.
        design.setWhenNoDataType(
                WhenNoDataTypeEnum.NO_DATA_SECTION
        );

        JRDesignBand noData = new JRDesignBand();
        noData.setHeight(40);

        noData.addElement(staticText(
                design, 0, 5, 523, 25,
                "Tidak ada mahasiswa pada rentang Student ID ini.",
                new Color(80, 90, 110),
                Color.WHITE, false, 11
        ));

        design.setNoData(noData);

        return design;
    }

    private static void addParameter(
            JasperDesign design,
            String name,
            Class<?> type
    ) throws JRException {

        JRDesignParameter parameter =
                new JRDesignParameter();

        parameter.setName(name);
        parameter.setValueClass(type);

        design.addParameter(parameter);
    }

    private static void addField(
            JasperDesign design,
            String name,
            Class<?> type
    ) throws JRException {

        JRDesignField field = new JRDesignField();

        field.setName(name);
        field.setValueClass(type);

        design.addField(field);
    }

    private static JRDesignStaticText staticText(
            JasperDesign design,
            int x,
            int y,
            int width,
            int height,
            String value,
            Color foreground,
            Color background,
            boolean bold,
            float fontSize
    ) {

        JRDesignStaticText text =
                new JRDesignStaticText(design);

        text.setX(x);
        text.setY(y);
        text.setWidth(width);
        text.setHeight(height);
        text.setText(value);

        text.setFontName("SansSerif");
        text.setFontSize(fontSize);
        text.setBold(bold);

        text.setForecolor(foreground);
        text.setBackcolor(background);
        text.setMode(ModeEnum.OPAQUE);

        text.setHorizontalTextAlign(
                HorizontalTextAlignEnum.LEFT
        );

        text.setVerticalTextAlign(
                VerticalTextAlignEnum.MIDDLE
        );

        return text;
    }

    private static JRDesignTextField textField(
            JasperDesign design,
            int x,
            int y,
            int width,
            int height,
            String expression,
            Color background,
            HorizontalTextAlignEnum alignment
    ) {

        JRDesignTextField field =
                new JRDesignTextField(design);

        field.setX(x);
        field.setY(y);
        field.setWidth(width);
        field.setHeight(height);

        field.setFontName("SansSerif");
        field.setFontSize(10f);

        field.setBackcolor(background);
        field.setMode(ModeEnum.OPAQUE);

        field.setHorizontalTextAlign(alignment);
        field.setVerticalTextAlign(
                VerticalTextAlignEnum.MIDDLE
        );

        field.setExpression(
                new JRDesignExpression(expression)
        );

        return field;
    }

    /**
     * Membuat logo SM sendiri agar laporan tidak membutuhkan
     * file gambar eksternal.
     */
    private static Image createLogo() {

        BufferedImage image = new BufferedImage(
                56, 56, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.setColor(NAVY);
            graphics.fillOval(0, 0, 56, 56);

            graphics.setColor(GOLD);
            graphics.fillOval(5, 5, 46, 46);

            graphics.setColor(NAVY);
            graphics.setFont(
                    new Font("SansSerif", Font.BOLD, 17)
            );

            graphics.drawString("SM", 9, 35);
        } finally {
            graphics.dispose();
        }

        return image;
    }
}
