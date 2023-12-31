package Folder.Dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    //Class will easv.mrs.be included when we start working on DATABASES
    private static final String PROP_FILE = "Config/Config.Settings.txt";

    private SQLServerDataSource dataSource;

    public DatabaseConnector() throws IOException {
        Properties databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream(new File(PROP_FILE)));

        dataSource = new SQLServerDataSource();
        dataSource.setServerName(databaseProperties.getProperty("Server"));
        dataSource.setDatabaseName(databaseProperties.getProperty("Database"));

        if (Boolean.parseBoolean(databaseProperties.getProperty("UseIntegratedSecurity"))) {
            dataSource.setIntegratedSecurity(true);
        } else {
            dataSource.setUser(databaseProperties.getProperty("User"));
            dataSource.setPassword(databaseProperties.getProperty("Password"));
        }

        dataSource.setPortNumber(Integer.parseInt(databaseProperties.getProperty("Port")));
        dataSource.setTrustServerCertificate(true);
    }

    public Connection getConnection() throws SQLServerException {
        return dataSource.getConnection();
    }


    // Testing purposes...
    public static void main(String[] args) throws SQLException, IOException {

        DatabaseConnector databaseConnector = new DatabaseConnector();

        try (Connection connection = databaseConnector.getConnection()) {
            System.out.println("Is it open? " + !connection.isClosed());
        } //Connection gets closed here
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
