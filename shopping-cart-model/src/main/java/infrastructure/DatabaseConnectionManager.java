package infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionManager {

    private final String dbUrl;
    private final String user;
    private final String password;

    public DatabaseConnectionManager(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
        createTableIfNeeded();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, user, password);
    }

    private void createTableIfNeeded() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS products (" +
                "name VARCHAR(255) PRIMARY KEY, " +
                "quantity INT, " +
                "price DOUBLE PRECISION)";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}