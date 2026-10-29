package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {

    private static final String URL =
            "jdbc:mariadb://localhost:3306/student_master";

    private static final String USER = "root";

    // Kosongkan jika root MariaDB tidak memakai password.
    private static final String PASSWORD = "";

    private DatabaseConnector() {
        // Mencegah pembuatan objek connector.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}