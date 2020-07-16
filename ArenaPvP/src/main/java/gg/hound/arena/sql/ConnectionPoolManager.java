package gg.hound.arena.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import gg.hound.arena.Arena;
import org.bukkit.configuration.file.FileConfiguration;

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

    private boolean failed = false;

    private final Arena arena;

    public ConnectionPoolManager(FileConfiguration fileConfiguration, Arena arena) {
        hostname = fileConfiguration.getString("sql.host");
        port = fileConfiguration.getInt("sql.port");
        database = fileConfiguration.getString("sql.database");
        username = fileConfiguration.getString("sql.username");
        password = fileConfiguration.getString("sql.password");
        this.arena = arena;
        minimumConnections = 8;
        maximumConnections = 30;
        connectionTimeout = 5000;
        idleTimeout = 600000;
        maxLifetime = 1800000;
        setupPool();

    }

    private void setupPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName("Arena_SQL");
            config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
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
            config.setThreadFactory(
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("hikari-sql-pool-%d")
                            .build());
            dataSource = new HikariDataSource(config);
            arena.log("Successfully connected to database using MySQL!");
        } catch (HikariPool.PoolInitializationException poolInitializationException) {
            if (poolInitializationException.getMessage().toLowerCase().contains("access denied"))
                arena.log("Invalid SQL Credentials...");
            else
                poolInitializationException.printStackTrace();
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

