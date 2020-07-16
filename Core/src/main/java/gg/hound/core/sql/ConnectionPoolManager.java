package gg.hound.core.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import gg.hound.core.CorePlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolManager {

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
    private final CorePlugin corePlugin;
    private HikariDataSource dataSource;
    private boolean failed = false;

    public ConnectionPoolManager(FileConfiguration fileConfiguration, CorePlugin corePlugin) {
        hostname = fileConfiguration.getString("sql.host");
        port = fileConfiguration.getInt("sql.port");
        database = fileConfiguration.getString("sql.database");
        username = fileConfiguration.getString("sql.username");
        password = fileConfiguration.getString("sql.password");
        this.corePlugin = corePlugin;
        minimumConnections = 6;
        maximumConnections = 15;
        connectionTimeout = 5000;
        idleTimeout = 600000;
        maxLifetime = 1800000;
        setupPool();

    }

    private void setupPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setPoolName("CorePlugin_SQL");
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
            corePlugin.log("Successfully connected to database using MySQL!");
        } catch (HikariPool.PoolInitializationException e) {
            if (e.getMessage().toLowerCase().contains("access denied")) {
                corePlugin.log("Invalid SQL Credentials...");
            } else {
                e.printStackTrace();
            }
            failed = true;
        }
    }

    Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    void closePool() {
        if (!failed && dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    boolean hasFailed() {
        return failed;
    }

}

