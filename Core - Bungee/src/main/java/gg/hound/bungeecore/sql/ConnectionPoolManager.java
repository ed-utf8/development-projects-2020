package gg.hound.bungeecore.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import gg.hound.bungeecore.BungeeCorePlugin;
import net.md_5.bungee.config.Configuration;

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

    private final BungeeCorePlugin bungeeCorePlugin;

    public ConnectionPoolManager(Configuration fileConfiguration, BungeeCorePlugin bungeeCorePlugin) {
        hostname = fileConfiguration.getString("host");
        port = fileConfiguration.getInt("port");
        database = fileConfiguration.getString("database");
        username = fileConfiguration.getString("username");
        password = fileConfiguration.getString("password");
        this.bungeeCorePlugin = bungeeCorePlugin;
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
            config.setPoolName("BungeeCore_SQL");
            config.setDataSourceClassName("org.mariadb.jdbc.MySQLDataSource");
            config.setUsername(username);
            config.setPassword(password);
            config.setMinimumIdle(minimumConnections);
            config.setMaximumPoolSize(maximumConnections);
            config.setConnectionTimeout(connectionTimeout);
            config.setMaxLifetime(maxLifetime);
            config.setIdleTimeout(idleTimeout);
            config.setThreadFactory(
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("hikari-sql-pool-%d")
                            .build());
            config.addDataSourceProperty("serverName", hostname);
            config.addDataSourceProperty("portNumber", port);
            config.addDataSourceProperty("databaseName", database);
            dataSource = new HikariDataSource(config);
            bungeeCorePlugin.log("Successfully connected to database using MySQL!");
        } catch (HikariPool.PoolInitializationException e) {
            if (e.getMessage().toLowerCase().contains("access denied")) {
                bungeeCorePlugin.log("Invalid SQL Credentials...");
            } else {
                e.printStackTrace();
            }
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

