package eu.kiieranngd.advance.SQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionPoolManager {

    private HikariDataSource dataSource;

    private final String hostname;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private final int minimumConnections;
    private final int maximumConnections;
    private final long connectionTimeout;
    private final long idleTimeout;
    private final long maxLifetime;

    private boolean failed;

    public ConnectionPoolManager() {
        hostname = "localhost";
        port = 3306;
        database = "discord";
        username = "botadmin";
        password = "JNopN2YcL5LMKYnr";
        minimumConnections = 3;
        maximumConnections = 6;
        connectionTimeout = 5000;
        idleTimeout = 600000;
        maxLifetime = 1800000;
        setupPool();
    }

    private void setupPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName("Discord_MariaDB");
            config.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
            config.setUsername(username);
            config.setPassword(password);
            config.setMinimumIdle(minimumConnections);
            config.setMaximumPoolSize(maximumConnections);
            config.setConnectionTimeout(connectionTimeout);
            config.setMaxLifetime(maxLifetime);
            config.setIdleTimeout(idleTimeout);
            config.addDataSourceProperty("serverName", hostname);
            config.addDataSourceProperty("portNumber", port);
            config.addDataSourceProperty("databaseName", database);
            dataSource = new HikariDataSource(config);
            System.out.println("Successfully connected to database using MySQL!");
        } catch (HikariPool.PoolInitializationException e) {
            e.printStackTrace();
            failed = true;
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (!failed && dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean hasFailed() {
        return failed;
    }
}

