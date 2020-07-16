package gg.hound.bungeecore.sql;


import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.user.BungeeCoreUser;
import gg.hound.bungeecore.user.UserManager;
import gg.hound.bungeecore.util.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.UUID;

public class SQLManager {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ConnectionPoolManager connectionPoolManager;
    private final UserManager userManager;
    private final String API_KEY;

    public SQLManager(BungeeCorePlugin bungeeCorePlugin, Configuration fileConfiguration, UserManager userManager) {
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.userManager = userManager;
        this.connectionPoolManager = new ConnectionPoolManager(fileConfiguration, bungeeCorePlugin);
        this.API_KEY = fileConfiguration.getString("vpn.key");

        if (connectionPoolManager.hasFailed()) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "end");
            return;
        }

        loadServers();

    }

    public void close() {
        connectionPoolManager.closePool();
    }


    public void createServer(String serverName, String serverType, String serverAddress, int serverPort, boolean serverPrivate) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `servers` WHERE `name` = ?");
            preparedStatement.setString(1, serverName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.isBeforeFirst()) {
                    while (resultSet.next()) {
                        bungeeCorePlugin.log("Error:  Server " + serverName + " Already exists in SQL.");
                    }
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `servers` VALUES (?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, serverName);
                    preparedStatement.setString(2, serverAddress);
                    preparedStatement.setInt(3, serverPort);
                    preparedStatement.setString(4, serverType);
                    preparedStatement.setBoolean(5, serverPrivate);
                    preparedStatement.executeUpdate();
                }

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteServer(String serverName) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `servers` WHERE `name` = ?");
            preparedStatement.setString(1, serverName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadServers() {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `servers`;");
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    addServer(resultSet.getString("name").toUpperCase(), resultSet.getString("host_address"), resultSet.getInt("port"), resultSet.getBoolean("private"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addServer(String serverName, String serverAddress, int serverPort, boolean protectedMode) {
        if (ProxyServer.getInstance().getServerInfo(serverName) == null) {
            ServerInfo craftedServer = ProxyServer.getInstance().constructServerInfo(serverName, new InetSocketAddress(serverAddress, serverPort), "", protectedMode);
            ProxyServer.getInstance().getServers().put(serverName, craftedServer);
            bungeeCorePlugin.log("§cServer has been added. [" + serverName + "]");
        } else {
            bungeeCorePlugin.log("§cServer already exists. [" + serverName + "]");
        }
    }

    public UserConnection logIp(String hostAddress) {

        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id` FROM `ip` WHERE `ip` = INET6_ATON(?) LIMIT 1;");
            preparedStatement.setString(1, hostAddress);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `vpn_ip` WHERE `ip_id` = ? AND `vpn_level` = 'BLOCKED' AND `whitelisted` = 0 LIMIT 1;");
                    preparedStatement.setLong(1, resultSet.getLong("id"));

                    try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                        if (resultSet1.next())
                            return new UserConnection(hostAddress, resultSet.getLong("id"), true);
                        else
                            return new UserConnection(hostAddress, resultSet.getLong("id"), false);
                    }
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `ip` (`ip`) VALUES (INET6_ATON(?));", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, hostAddress);
                    preparedStatement.executeUpdate();

                    try (ResultSet resultSet1 = preparedStatement.getGeneratedKeys()) {
                        if (resultSet1.next()) {
                            checkForVpn(hostAddress, resultSet1.getLong("id"));
                        }
                    }

                    return logIp(hostAddress);
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    public BungeeCoreUser loadUser(UUID uuid, String name, long ipId) {

        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`id`, `user`.`name` FROM `user` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    preparedStatement = connection.prepareStatement("INSERT INTO `user` (`uuid`, `name`) VALUES (?, ?)");
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, name);
                    preparedStatement.executeUpdate();
                    return loadUser(uuid, name, ipId);
                } else {
                    BungeeCoreUser coreUser = userManager.createUser(resultSet.getLong("id"), name, uuid);

                    if (!resultSet.getString("name").equals(name)) {
                        preparedStatement = connection.prepareStatement("UPDATE `user` SET `name` = ? WHERE `id` = ?");
                        preparedStatement.setString(1, name);
                        preparedStatement.setLong(2, resultSet.getLong("id"));
                        preparedStatement.executeUpdate();
                    }

                    preparedStatement = connection.prepareStatement("SELECT `group`.`name` FROM `user_group` INNER JOIN `group` ON `group`.`id` = `user_group`.`group_id` WHERE `user_group`.`user_id` = ?");
                    preparedStatement.setLong(1, resultSet.getLong("id"));

                    try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                        while (resultSet1.next()) {
                            coreUser.getGroups().add(resultSet1.getString("name"));
                        }
                    }

                    preparedStatement = connection.prepareStatement("INSERT INTO `user_ip` (`user_id`, `ip_id`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `last_used` = CURRENT_TIMESTAMP;");
                    preparedStatement.setLong(1, coreUser.getUserId());
                    preparedStatement.setLong(2, ipId);
                    preparedStatement.executeUpdate();

                    return coreUser;
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    private boolean checkForVpn(String hostAddress, Long ipId) {

        String query_result;
        try {
            query_result = query("https://proxycheck.io/v2/" + hostAddress + "?key=" + API_KEY + "&vpn=1&time=1&&node=1&asn=1");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        JSONParser parser = new JSONParser();
        JSONObject main, sub;
        try {
            main = (JSONObject) parser.parse(query_result);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        String status = (String) main.get("status");
        if (!status.equalsIgnoreCase("ok"))
            return false;

        sub = (JSONObject) main.get(hostAddress);

        String isProxy = (String) sub.get("proxy");

        if (isProxy.equalsIgnoreCase("yes")) {
            try (Connection connection = connectionPoolManager.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `vpn_ip` (`ip_id`) VALUES (?)");
                preparedStatement.setLong(1, ipId);
                preparedStatement.executeUpdate();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            return true;
        }

        return false;
    }

    private String query(String url)
            throws IOException {
        StringBuilder response = new StringBuilder();
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        connection.setConnectTimeout(2000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("User-Agent", "Defiance-AntiBot.v1.2.0");
        connection.setRequestProperty("tag", "Defiance-AntiBot.v1.2.0");
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()))) {
            while ((url = in.readLine()) != null) {
                response.append(url);
            }
        }
        return response.toString();
    }
}