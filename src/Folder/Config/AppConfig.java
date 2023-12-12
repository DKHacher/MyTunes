package Folder.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum AppConfig {
    INSTANCE;

    private static final String PROP_FILE = "config/appConfig.properties";
    private final Properties properties;

    AppConfig() {
        properties = new Properties();
        try (InputStream input = new FileInputStream(PROP_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading configuration file: " + PROP_FILE);
            System.err.println("Please ensure the file exists and is accessible.");
            System.err.println("Error details: " + e.getMessage());
            System.exit(1); // Abnormal termination of the program
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
