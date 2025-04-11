package modelo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Clase para gestionar las conexiones a la base de datos usando HikariCP
 */
public class DatabaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private static HikariDataSource dataSource;
    
    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            logger.error("Error initializing database connection pool", e);
        }
    }
    
    private static void initializeDataSource() {
        try {
            Properties props = loadDatabaseProperties();
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));
            // Connection pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);
            
            // Connection test query
            config.setConnectionTestQuery("SELECT 1");
            
            dataSource = new HikariDataSource(config);
            
            logger.info("Database connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }
    
    private static Properties loadDatabaseProperties() {
        Properties props = new Properties();
        
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.warn("database.properties file not found, using default configuration");
                // Default configuration if properties file is not found
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/sistema_facturacion_inventario?useSSL=false&serverTimezone=UTC");
                props.setProperty("db.username", "root");
                props.setProperty("db.password", "123");
                props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                props.load(input);
                logger.info("Database properties loaded successfully");
            }
        } catch (IOException e) {
            logger.error("Error loading database properties", e);
            throw new RuntimeException("Failed to load database properties", e);
        }
        
        return props;
    }
    
    /**
     * Get a database connection from the connection pool
     * @return Connection object
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            synchronized (DatabaseConnector.class) {
                if (dataSource == null) {
                    initializeDataSource();
                }
            }
        }
        
        return dataSource.getConnection();
    }
    
    /**
     * Close the connection pool
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
