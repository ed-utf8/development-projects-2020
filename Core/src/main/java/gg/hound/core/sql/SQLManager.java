package gg.hound.core.sql;

import gg.hound.core.CorePlugin;
import gg.hound.core.disguise.DisguiseManager;
import gg.hound.core.disguise.SQLReDisguiseObject;
import gg.hound.core.disguise.skins.PlayerSkin;
import gg.hound.core.events.CoreUserRankUpdateEvent;
import gg.hound.core.group.Group;
import gg.hound.core.group.GroupManager;
import gg.hound.core.punishments.*;
import gg.hound.core.punishments.temp.PunishmentLookup;
import gg.hound.core.punishments.temp.TempPunishment;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.util.Colour;
import gg.hound.core.util.ItemBuilder;
import gg.hound.core.util.PluginUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SQLManager {

    private final CorePlugin corePlugin;
    private final ConnectionPoolManager connectionPoolManager;
    private final PunishmentData punishmentData;
    private final RedisManager redisManager;
    private final GroupManager groupManager;
    private final PluginUtils pluginUtils;

    public SQLManager(CorePlugin corePlugin, FileConfiguration fileConfiguration, PunishmentData punishmentData, RedisManager redisManager, GroupManager groupManager, PluginUtils pluginUtils) {
        this.corePlugin = corePlugin;
        this.punishmentData = punishmentData;
        this.groupManager = groupManager;
        this.pluginUtils = pluginUtils;
        this.connectionPoolManager = new ConnectionPoolManager(fileConfiguration, corePlugin);
        this.redisManager = redisManager;

        if (connectionPoolManager.hasFailed()) {
            Bukkit.getPluginManager().disablePlugin(this.corePlugin);
            return;
        }

        setUpServer();
        loadColours();
        loadPunishmentData();
        loadGroups();
    }

    public void close() {
        connectionPoolManager.closePool();
    }

    private void setUpServer() {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `name`, `private` FROM `servers` WHERE `port` = ? LIMIT 1;");
            preparedStatement.setInt(1, Bukkit.getServer().getPort());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    corePlugin.setServerName(resultSet.getString("name"));
                    if (resultSet.getBoolean("private"))
                        Bukkit.getServer().setWhitelist(true);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void loadPunishmentData() {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `punishment_ipban_reason`.`id` AS `ban_id`, `punishment_ipban_reason`.`enabled` AS `enabled`, `punishment_ipban_reason`.`name`, `punishment_ipban_reason`.`icon`, `punishment_ipban_reason`.`text_input_required`, `punishment_ipban_reason`.`time_input_required`, `icon`.`material`, `icon`.`damage`, `icon`.`data`, `icon`.`display_name_colour`, `icon`.`display_name` FROM `punishment_ipban_reason` INNER JOIN `icon` ON `icon`.`id` = `punishment_ipban_reason`.`icon` WHERE `punishment_ipban_reason`.`enabled` = 1;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ipban_reason_length` WHERE `reason_id` = ?;");
                    preparedStatement.setInt(1, resultSet.getInt("ban_id"));
                    try (ResultSet punishmentSet = preparedStatement.executeQuery()) {
                        Map<Integer, Integer> lengths = new HashMap<>();
                        while (punishmentSet.next()) {
                            lengths.put(punishmentSet.getInt("offence_num"), punishmentSet.getInt("length_sec"));
                        }
                        Reason reason = new Reason(resultSet.getInt("ban_id"), resultSet.getString("name"), resultSet.getString("display_name"), new ItemBuilder(Material.getMaterial(resultSet.getInt("material"))).setDurability((short) resultSet.getInt("damage")).setName(pluginUtils.getColour(resultSet.getInt("display_name_colour")).colourize() + resultSet.getString("display_name")).toItemStack(), resultSet.getBoolean("text_input_required"), resultSet.getBoolean("time_input_required"), resultSet.getBoolean("enabled"), lengths);
                        punishmentData.getIPBanReasons().put(resultSet.getString("name"), reason);
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }

            preparedStatement = connection.prepareStatement("SELECT `punishment_ban_reason`.`id` AS `ban_id`, `punishment_ban_reason`.`enabled` AS `enabled`, `punishment_ban_reason`.`name`, `punishment_ban_reason`.`icon`, `punishment_ban_reason`.`text_input_required`, `punishment_ban_reason`.`time_input_required`, `icon`.`material`, `icon`.`damage`, `icon`.`data`, `icon`.`display_name_colour`, `icon`.`display_name` FROM `punishment_ban_reason` INNER JOIN `icon` ON `icon`.`id` = `punishment_ban_reason`.`icon` WHERE `punishment_ban_reason`.`enabled` = 1;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ban_reason_length` WHERE `reason_id` = ?;");
                    preparedStatement.setInt(1, resultSet.getInt("ban_id"));
                    try (ResultSet punishmentSet = preparedStatement.executeQuery()) {
                        Map<Integer, Integer> lengths = new HashMap<>();
                        while (punishmentSet.next()) {
                            lengths.put(punishmentSet.getInt("offence_num"), punishmentSet.getInt("length_sec"));
                        }
                        Reason reason = new Reason(resultSet.getInt("ban_id"), resultSet.getString("name"), resultSet.getString("display_name"), new ItemBuilder(Material.getMaterial(resultSet.getInt("material"))).setDurability((short) resultSet.getInt("damage")).setName(pluginUtils.getColour(resultSet.getInt("display_name_colour")).colourize() + resultSet.getString("display_name")).toItemStack(), resultSet.getBoolean("text_input_required"), resultSet.getBoolean("time_input_required"), resultSet.getBoolean("enabled"), lengths);
                        punishmentData.getBanReasons().put(resultSet.getString("name"), reason);
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            preparedStatement = connection.prepareStatement("SELECT `punishment_mute_reason`.`id` AS `mute_id`, `punishment_mute_reason`.`enabled` AS `enabled`, `punishment_mute_reason`.`name`, `punishment_mute_reason`.`icon`, `punishment_mute_reason`.`text_input_required`, `punishment_mute_reason`.`time_input_required`, `icon`.`material`, `icon`.`damage`, `icon`.`data`, `icon`.`display_name_colour`, `icon`.`display_name` FROM `punishment_mute_reason` INNER JOIN `icon` ON `icon`.`id` = `punishment_mute_reason`.`icon` WHERE `punishment_mute_reason`.`enabled` = 1;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_mute_reason_length` WHERE `reason_id` = ?;");
                    preparedStatement.setInt(1, resultSet.getInt("mute_id"));
                    try (ResultSet punishmentSet = preparedStatement.executeQuery()) {
                        Map<Integer, Integer> lengths = new HashMap<>();
                        while (punishmentSet.next()) {
                            lengths.put(punishmentSet.getInt("offence_num"), punishmentSet.getInt("length_sec"));
                        }
                        Reason reason = new Reason(resultSet.getInt("mute_id"), resultSet.getString("name"), resultSet.getString("display_name"), new ItemBuilder(Material.getMaterial(resultSet.getInt("material"))).setDurability((short) resultSet.getInt("damage")).setName(pluginUtils.getColour(resultSet.getInt("display_name_colour")).colourize() + resultSet.getString("display_name")).toItemStack(), resultSet.getBoolean("text_input_required"), resultSet.getBoolean("time_input_required"), resultSet.getBoolean("enabled"), lengths);
                        punishmentData.getMuteReasons().put(resultSet.getString("name"), reason);
                    }
                }
            }

            preparedStatement = connection.prepareStatement("SELECT `punishment_kick_reason`.`id` AS `kick_id`, `punishment_kick_reason`.`enabled` AS `enabled`, `punishment_kick_reason`.`name`, `punishment_kick_reason`.`icon`, `punishment_kick_reason`.`text_input_required`, `icon`.`material`, `icon`.`damage`, `icon`.`data`, `icon`.`display_name_colour`, `icon`.`display_name` FROM `punishment_kick_reason` INNER JOIN `icon` ON `icon`.`id` = `punishment_kick_reason`.`icon` WHERE `punishment_kick_reason`.`enabled` = 1;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Reason reason = new Reason(resultSet.getInt("kick_id"), resultSet.getString("name"), resultSet.getString("display_name"), new ItemBuilder(Material.getMaterial(resultSet.getInt("material"))).setDurability((short) resultSet.getInt("damage")).setName(pluginUtils.getColour(resultSet.getInt("display_name_colour")).colourize() + resultSet.getString("display_name")).toItemStack(), resultSet.getBoolean("text_input_required"), false, resultSet.getBoolean("enabled"));
                    punishmentData.getKickReasons().put(resultSet.getString("name"), reason);
                }
            }

            preparedStatement = connection.prepareStatement("SELECT `report_reason`.`id` AS `report_id`, `report_reason`.`name`, `report_reason`.`icon`, `report_reason`.`input_required`, `icon`.`display_name` AS `display_name`, `icon`.`material`, `icon`.`damage`, `icon`.`data` FROM `report_reason` INNER JOIN `icon` ON `icon`.`id` = `report_reason`.`icon` WHERE `report_reason`.`enabled` = 1;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    punishmentData.getReportReasons().put(resultSet.getString("name"), new Reason(resultSet.getInt("report_id"), resultSet.getString("name"), resultSet.getString("display_name"), new ItemBuilder(Material.getMaterial(resultSet.getInt("material"))).setDurability((short) resultSet.getInt("damage")).toItemStack(), resultSet.getBoolean("input_required"), false, true));
            }

            /*preparedStatement = connection.prepareStatement("SELECT * FROM `automute_pattern` WHERE `enabled` = 1 ORDER BY `weight` DESC;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    punishmentData.getAutomutePatterns().add(new AutomutePattern(resultSet.getInt("id"), Pattern.compile(resultSet.getString("pattern")), resultSet.getInt("length_sec")));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }*/

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void loadGroups() {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `group`;");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT `permission`.`value`" +
                        " FROM `group_permission`" +
                        " INNER JOIN `permission`" +
                        " ON `group_permission`.`permission_id` = `permission`.`id`" +
                        " WHERE `group_permission`.`group_id` = ?;");
                    preparedStatement.setInt(1, resultSet.getInt("id"));

                    HashMap<String, Boolean> permissionMap = new HashMap<>();

                    try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                        while (resultSet1.next())
                            permissionMap.put(resultSet1.getString("value"), true);

                        groupManager.createGroup(resultSet.getString("name"), new Group(
                            resultSet.getInt("id"),
                            permissionMap,
                            resultSet.getString("name"),
                            resultSet.getString("prefix"),
                            resultSet.getString("chat_separator"),
                            resultSet.getInt("format_weight"),
                            resultSet.getInt("punish_power"),
                            resultSet.getInt("needed_punish_power"),
                            resultSet.getInt("assign_power"),
                            resultSet.getInt("needed_assign_power"),
                            pluginUtils.getColour(resultSet.getInt("chat_colour")),
                            resultSet.getBoolean("staff"),
                            resultSet.getBoolean("admin")));
                    }

                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void loadColours() {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `colour`;");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    pluginUtils.getColourMap().put(resultSet.getInt("id"), new Colour(resultSet.getInt("id"), resultSet.getString("name"), ChatColor.valueOf(resultSet.getString("mc")), resultSet.getBoolean("is_bold"), resultSet.getBoolean("is_itallic"), resultSet.getBoolean("is_strikethrough"), resultSet.getBoolean("enabled")));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loadUser(CoreUser coreUser, String ip) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`id`, `user`.`colour` FROM `user` WHERE `uuid` = ? LIMIT 1;");
            preparedStatement.setString(1, coreUser.getUuid().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coreUser.setUserId(resultSet.getLong("id"));
                    coreUser.setColour(pluginUtils.getColour(resultSet.getInt("colour")));

                    preparedStatement = connection.prepareStatement("SELECT `id` FROM `ip` WHERE `ip` = INET6_ATON(?);");
                    preparedStatement.setString(1, ip);

                    try (ResultSet ipSet = preparedStatement.executeQuery()) {
                        if (ipSet.next())
                            coreUser.setIpId(ipSet.getLong("id"));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }

					/*preparedStatement = connection.prepareStatement("SELECT `prefix` FROM `user_prefix` WHERE `id` = ?;");
					preparedStatement.setLong(1, resultSet.getLong("id"));
					try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
						if (resultSet1.next())
							coreUser.setSpecialPrefix(resultSet1.getString("prefix"));
					}*/
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loadUserExtra(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_options` WHERE `user_id` = ? LIMIT 1;");
            preparedStatement.setLong(1, coreUser.getUserId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    coreUser.setPrivateMessages(resultSet.getBoolean("private_messages"));
                    coreUser.setStaffNotifications(resultSet.getBoolean("staff_notifications"));
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `user_options` (`user_id`, `staff_notifications`, `private_messages`) VALUES (?, ?, ?)");
                    preparedStatement.setLong(1, coreUser.getUserId());
                    preparedStatement.setInt(2, 1);
                    preparedStatement.setInt(3, 1);

                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loadMute(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_mute` WHERE `target_user` = ? AND `end` > ? OR `end` IS NULL;");
            preparedStatement.setLong(1, coreUser.getUserId());
            preparedStatement.setTimestamp(2, new Timestamp(pluginUtils.currentTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coreUser.setMuted(true);
                    if (resultSet.getTimestamp("end") != null)
                        coreUser.setUnMuteTime(resultSet.getTimestamp("end").getTime());
                    else coreUser.setUnMuteTime(-1);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loadUserGroups(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group`.`name` FROM `user_group` INNER JOIN `group` ON `user_group`.`group_id` = `group`.`id` WHERE `user_group`.`user_id` = ?;");
            preparedStatement.setLong(1, coreUser.getUserId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    coreUser.addGroup(groupManager.getGroup(resultSet.getString("name")));
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public TempInfoStoreUser getUser(String username) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user` WHERE `name` = ? LIMIT 1;");
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT `ip_id` FROM `user_ip` WHERE `user_id` = ? ORDER BY `last_used` DESC LIMIT 1;");
                    preparedStatement.setLong(1, resultSet.getLong("id"));

                    try (ResultSet ipSet = preparedStatement.executeQuery()) {
                        if (ipSet.next())
                            return new TempInfoStoreUser(resultSet.getLong("id"), UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), ipSet.getLong("ip_id"));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                } else {
                    PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM `user` INNER JOIN `user_disguise` ON `user`.`id` = `user_disguise`.`user_id` WHERE `user_disguise`.`disguise_name` = ? LIMIT 1;");
                    preparedStatement1.setString(1, username);
                    try (ResultSet resultSet1 = preparedStatement1.executeQuery()) {
                        if (resultSet1.next()) {
                            preparedStatement = connection.prepareStatement("SELECT `ip_id` FROM `user_ip` WHERE `user_id` = ? ORDER BY `last_used` DESC LIMIT 1;");
                            preparedStatement.setLong(1, resultSet1.getLong("id"));

                            try (ResultSet ipSet = preparedStatement.executeQuery()) {
                                if (ipSet.next())
                                    return new TempInfoStoreUser(resultSet1.getLong("user_id"), UUID.fromString(resultSet1.getString("uuid")), resultSet1.getString("name"), ipSet.getLong("ip_id")).setDisguise(true);
                            } catch (SQLException sqlException) {
                                sqlException.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public TempInfoStoreUser getUser(UUID uuid) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user` WHERE `uuid` = ? LIMIT 1;");
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT `ip_id` FROM `user_ip` WHERE `user_id` = ? ORDER BY `last_used` DESC LIMIT 1;");
                    preparedStatement.setLong(1, resultSet.getLong("id"));

                    try (ResultSet ipSet = preparedStatement.executeQuery()) {
                        if (ipSet.next())
                            return new TempInfoStoreUser(resultSet.getLong("id"), UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), ipSet.getLong("ip_id"));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public TempInfoStoreUser getUser(Long id) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user` WHERE `id` = ? LIMIT 1;");
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("SELECT `ip_id` FROM `user_ip` WHERE `user_id` = ? ORDER BY `last_used` DESC LIMIT 1;");
                    preparedStatement.setLong(1, resultSet.getLong("id"));

                    try (ResultSet ipSet = preparedStatement.executeQuery()) {
                        if (ipSet.next())
                            return new TempInfoStoreUser(resultSet.getLong("id"), UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), ipSet.getLong("ip_id"));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public List<String> getGroups(TempInfoStoreUser tempInfoStoreUser) {
        List<String> groups = new ArrayList<>();
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `name` FROM `user_group` INNER JOIN `group` ON `user_group`.`group_id` = `group`.`id` WHERE `user_group`.`user_id` = ?;");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.isBeforeFirst()) {
                    while (resultSet.next()) {
                        groups.add(resultSet.getString("name"));
                    }
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return groups;
    }

    private boolean hasGroup(TempInfoStoreUser tempInfoStoreUser, Group permissionGroup) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_group` WHERE `user_id` = ? AND `group_id` = ?");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());
            preparedStatement.setInt(2, permissionGroup.getRankId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return false;
    }

    public void addGroup(TempInfoStoreUser tempInfoStoreUser, Group permissionGroup) {
        if (hasGroup(tempInfoStoreUser, permissionGroup))
            return;

        redisManager.sendRankUpdate(CoreUserRankUpdateEvent.GroupAction.ADD, tempInfoStoreUser.getUuid(), permissionGroup.getRankName());
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT IGNORE INTO `user_group` (`user_id`, `group_id`) VALUES (?, ?);");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());
            preparedStatement.setInt(2, permissionGroup.getRankId());
            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void removeGroup(TempInfoStoreUser tempInfoStoreUser, Group permissionGroup) {
        if (!hasGroup(tempInfoStoreUser, permissionGroup))
            return;

        redisManager.sendRankUpdate(CoreUserRankUpdateEvent.GroupAction.REMOVE, tempInfoStoreUser.getUuid(), permissionGroup.getRankName());
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `user_group` WHERE `user_id` = ? AND `group_id` = ?");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());
            preparedStatement.setInt(2, permissionGroup.getRankId());
            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void changeUserColor(CoreUser coreUser, int id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `user` SET `colour` =  ? WHERE `id` = ?");
                    preparedStatement.setInt(1, id);
                    preparedStatement.setLong(2, coreUser.getUserId());
                    preparedStatement.executeUpdate();

                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }.runTaskAsynchronously(corePlugin);
    }

    public String getName(String uuid) {
        String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
        try {
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            String playerSlot = nameValue.get(nameValue.size() - 1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public void removeDisguise(CoreUser coreUser) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `user_disguise` WHERE `user_id` = ?");
                    preparedStatement.setLong(1, coreUser.getUserId());
                    preparedStatement.executeUpdate();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }.runTaskAsynchronously(corePlugin);
    }

    public void addDisguise(CoreUser coreUser, String name, int skinId) {

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user_disguise` VALUES (?, ?, ?)");
                    preparedStatement.setLong(1, coreUser.getUserId());
                    preparedStatement.setString(2, name);
                    preparedStatement.setInt(3, skinId);
                    preparedStatement.executeUpdate();

                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }.runTaskAsynchronously(corePlugin);
    }

    public Map<String, String> disguiseList() {
        Map<String, String> disguiseList = new ConcurrentHashMap<>();
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `user_disguise`.`disguise_name` FROM `user_disguise` INNER JOIN `user` ON `user`.`id` = `user_disguise`.`user_id` LIMIT 30;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    disguiseList.put(resultSet.getString("name"), resultSet.getString("disguise_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disguiseList;
    }

    public void loadDisguise(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `user_disguise` WHERE `user_id` = ?;");
            preparedStatement.setLong(1, coreUser.getUserId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coreUser.setSqlReDisguiseObject(new SQLReDisguiseObject(resultSet.getString("disguise_name"), resultSet.getInt("skin_id")));
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void loadSkins(DisguiseManager disguiseManager) {
        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `disguise_skins`;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    while (resultSet.next()) {
                        disguiseManager.addSkin(new PlayerSkin(resultSet.getInt("id"), resultSet.getString("value"), resultSet.getString("signature")));
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void addDisguiseSkin(String value, String signature) {

        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `disguise_skins` (value, signature) VALUES (?, ?)");
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, signature);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean isDiscordLinked(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `discord_user` WHERE `id` = ?");
            preparedStatement.setLong(1, coreUser.getUserId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    public boolean isCodeValid(String code) {

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `discord_user` WHERE `discordCode` = ? AND `linked` = ? LIMIT 1;");
            preparedStatement.setString(1, code);
            preparedStatement.setBoolean(2, false);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateDiscord(CoreUser coreUser, String code) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `discord_user` SET `id` = ?, `linked` = ? WHERE `discordCode` = ? AND `linked` = ?;");
            preparedStatement.setLong(1, coreUser.getUserId());
            preparedStatement.setBoolean(2, true);
            preparedStatement.setString(3, code);
            preparedStatement.setBoolean(4, false);
            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public boolean kickPlayer(Punishment kick) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `punishment_kick`" +
                "    (`target_user`, `target_ip`, `punisher_user`, `punisher_ip`, `reason`)" +
                "VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, kick.getTarget().getId());
            preparedStatement.setLong(2, kick.getTarget().getIpId());
            preparedStatement.setLong(3, kick.getExecutor().getId());
            preparedStatement.setLong(4, kick.getExecutor().getIpId());
            preparedStatement.setInt(5, kick.getReason().getId());
            preparedStatement.executeUpdate();

            if (kick.getReason().isTextInputRequired()) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("INSERT INTO `punishment_kick_reason_input` (`kick_id`, `input`) VALUES (?, ?);");
                        preparedStatement.setLong(1, resultSet.getLong(1));
                        preparedStatement.setString(2, kick.getReason().getCustomReason());
                        preparedStatement.executeUpdate();
                    }
                }
            }

            return false;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return true;
    }

    public boolean banPlayer(Punishment ban) {

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `punishment_ban` (`target_user`, `target_ip`, `punisher_user`, `punisher_ip`, `end`, `reason`) VALUES (?, ?, ?, ?, (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, ban.getTarget().getId());
            preparedStatement.setLong(2, ban.getTarget().getIpId());
            preparedStatement.setLong(3, ban.getExecutor().getId());
            preparedStatement.setLong(4, ban.getExecutor().getIpId());
            preparedStatement.setLong(5, ban.getTime());
            preparedStatement.setInt(6, ban.getReason().getId());
            preparedStatement.executeUpdate();

            if (ban.getReason().isTextInputRequired()) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("INSERT INTO `punishment_ban_reason_input` (`ban_id`, `input`) VALUES (?, ?);");
                        preparedStatement.setLong(1, resultSet.getLong(1));
                        preparedStatement.setString(2, ban.getReason().getCustomReason());
                        preparedStatement.executeUpdate();
                    }
                }
            }

            return false;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return true;
    }

    public boolean mutePlayer(Punishment mute) {

        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `punishment_mute` (`target_user`, `target_ip`, `punisher_user`, `punisher_ip`, `end`, `reason`) VALUES (?, ?, ?, ?, (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), ?);", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, mute.getTarget().getId());
            preparedStatement.setLong(2, mute.getTarget().getIpId());
            preparedStatement.setLong(3, mute.getExecutor().getId());
            preparedStatement.setLong(4, mute.getExecutor().getIpId());
            preparedStatement.setInt(5, mute.getTime());
            preparedStatement.setInt(6, mute.getReason().getId());
            preparedStatement.executeUpdate();

            if (mute.getReason().isTextInputRequired()) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("INSERT INTO `punishment_mute_reason_input` (`mute_id`, `input`) VALUES (?, ?);");
                        preparedStatement.setLong(1, resultSet.getLong(1));
                        preparedStatement.setString(2, mute.getReason().getCustomReason());
                        preparedStatement.executeUpdate();
                    }
                }
            }

            return false;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return true;
    }

    public boolean ipbanPlayer(Punishment ipban) {

        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `punishment_ipban` (`target_user`, `target_ip`, `punisher_user`, `punisher_ip`, `end`, `reason`) VALUES (?, ?, ?, ?, (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), ?);", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1, ipban.getTarget().getId());
            preparedStatement.setLong(2, ipban.getTarget().getIpId());
            preparedStatement.setLong(3, ipban.getExecutor().getId());
            preparedStatement.setLong(4, ipban.getExecutor().getIpId());
            preparedStatement.setInt(5, ipban.getTime());
            preparedStatement.setInt(6, ipban.getReason().getId());
            preparedStatement.executeUpdate();

            if (ipban.getReason().isTextInputRequired()) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        preparedStatement = connection.prepareStatement("INSERT INTO `punishment_ipban_reason_input` (`ipban_id`, `input`) VALUES (?, ?);");
                        preparedStatement.setLong(1, resultSet.getLong(1));
                        preparedStatement.setString(2, ipban.getReason().getCustomReason());
                        preparedStatement.executeUpdate();
                    }
                }
            }

            return false;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return true;
    }

    public void unbanPlayer(TempInfoStoreUser target, TempInfoStoreUser executor, int time) {

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `punishment_ban` SET `pardon_user` = ?, `pardon_ip` = ?, `end` = (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), `pardon_time` = CURRENT_TIMESTAMP WHERE `target_user` = ? AND `end` >= CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, executor.getId());
            preparedStatement.setLong(2, executor.getIpId());
            preparedStatement.setInt(3, time);
            preparedStatement.setLong(4, target.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void unmutePlayer(TempInfoStoreUser target, TempInfoStoreUser executor, int time) {

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `punishment_mute` SET `pardon_user` = ?, `pardon_ip` = ?, `end` = (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), `pardon_time` = CURRENT_TIMESTAMP WHERE `target_user` = ? AND `end` >= CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, executor.getId());
            preparedStatement.setLong(2, executor.getIpId());
            preparedStatement.setInt(3, time);
            preparedStatement.setLong(4, target.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public void unbanIp(TempInfoStoreUser target, TempInfoStoreUser executor, int time) {

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `punishment_ipban` SET `pardon_user` = ?, `pardon_ip` = ?, `end` = (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL ? SECOND)), `pardon_time` = CURRENT_TIMESTAMP WHERE `target_user` = ? AND `end` >= CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, executor.getId());
            preparedStatement.setLong(2, executor.getIpId());
            preparedStatement.setInt(3, time);
            preparedStatement.setLong(4, target.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    public long getID(String name) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id` FROM `user` WHERE `name` = ?;");
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public int getHighestRankPower(TempInfoStoreUser tempInfoStoreUser, CoreUser.PowerType powerType) {
        try (Connection connection = connectionPoolManager.getConnection()) {

            switch (powerType) {

                case PREFIX: {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group`.`prefix_weight` AS `level` FROM `user_group` INNER JOIN `group` ON `user_group`.`group_id` = `group`.`id` WHERE `user_group`.`user_id` = ? ORDER BY  `group`.`needed_punish_power` DESC LIMIT 1;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt("level");
                        }
                    }
                    break;
                }

                case PUNISH: {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group`.`needed_punish_power` AS `level` FROM `user_group` INNER JOIN `group` ON `user_group`.`group_id` = `group`.`id` WHERE `user_group`.`user_id` = ? ORDER BY  `group`.`needed_punish_power` DESC LIMIT 1;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt("level");
                        }
                    }
                    break;
                }

                case ASSIGN: {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group`.`needed_assign_power` AS `level` FROM `user_group` INNER JOIN `group` ON `user_group`.`group_id` = `group`.`id` WHERE `user_group`.`user_id` = ? ORDER BY  `group`.`needed_punish_power` DESC LIMIT 1;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getInt("level");
                        }
                    }
                    break;
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isBanned(TempInfoStoreUser tempInfoStoreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ban` WHERE `target_user` = ? AND `end` > CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return false;
    }

    public boolean isMuted(TempInfoStoreUser tempInfoStoreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_mute` WHERE `target_user` = ? AND `end` > CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return false;
    }

    public boolean isIpBanned(TempInfoStoreUser tempInfoStoreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ipban` WHERE `target_user` = ? AND `end` > CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, tempInfoStoreUser.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return false;
    }

    public Ban getBan(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ban` WHERE `target_user` = ? AND `end` > CURRENT_TIMESTAMP OR `end` IS NULL;");
            preparedStatement.setLong(1, coreUser.getUserId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Ban(punishmentData.getBanReason(resultSet.getInt("reason")), (resultSet.getTimestamp("end") == null) ? -1 : resultSet.getTimestamp("end").getTime());
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public PunishmentLookup getPlayerPunishmentLookup(String punishmentType, TempInfoStoreUser tempInfoStoreUser) {

        ArrayList<TempPunishment> punishmentLookup = new ArrayList<>();

        switch (punishmentType.toLowerCase()) {

            case "ban":
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `reason`, `start`, `end` FROM `punishment_ban` INNER JOIN `user` ON `punishment_ban`.`punisher_user` = `user`.`id` WHERE `punishment_ban`.`target_user` = ? ORDER BY `start` DESC LIMIT 10;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            boolean isActive = false;
                            if (resultSet.getTimestamp("end") == null) isActive = true;
                            if (resultSet.getTimestamp("end") != null && resultSet.getTimestamp("end").getTime() < pluginUtils.currentTime())
                                isActive = true;
                            punishmentLookup.add(new TempPunishment("BAN", pluginUtils.getDate(resultSet.getTimestamp("start").getTime()), pluginUtils.getDate(resultSet.getTimestamp("end").getTime()), punishmentData.getBanReason(resultSet.getInt("reason")).getName(), resultSet.getString("name"), isActive));
                        }
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                break;

            case "mute":
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `reason`, `start`, `end` FROM `punishment_mute` INNER JOIN `user` ON `punishment_mute`.`punisher_user` = `user`.`id` WHERE `punishment_mute`.`target_user` = ? ORDER BY `start` DESC LIMIT 10;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            boolean isActive = false;
                            if (resultSet.getTimestamp("end") == null) isActive = true;
                            if (resultSet.getTimestamp("end") != null && resultSet.getTimestamp("end").getTime() < pluginUtils.currentTime())
                                isActive = true;
                            punishmentLookup.add(new TempPunishment("MUTE", pluginUtils.getDate(resultSet.getTimestamp("start").getTime()), pluginUtils.getDate(resultSet.getTimestamp("end").getTime()), punishmentData.getMuteReason(resultSet.getInt("reason")).getName(), resultSet.getString("name"), isActive));
                        }
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                break;


            case "kick":
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `reason`, `start` FROM `punishment_kick` INNER JOIN `user` ON `punishment_kick`.`punisher_user` = `user`.`id` WHERE `punishment_kick`.`target_user` = ? ORDER BY `start` DESC LIMIT 10;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            punishmentLookup.add(new TempPunishment("KICK", pluginUtils.getDate(resultSet.getTimestamp("start").getTime()), pluginUtils.getDate(resultSet.getTimestamp("start").getTime()), resultSet.getString("reason"), resultSet.getString("name"), false));
                        }
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                break;

            case "ipban":
                try (Connection connection = connectionPoolManager.getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `reason`, `start`, `end` FROM `punishment_ipban` INNER JOIN `user` ON `punishments_ipban`.`punisher_user` = `user`.`id` WHERE `punishment_ipban`.`target_user` = ? ORDER BY `start` DESC LIMIT 10;");
                    preparedStatement.setLong(1, tempInfoStoreUser.getId());

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            boolean isActive = false;
                            if (resultSet.getTimestamp("end") == null) isActive = true;
                            if (resultSet.getTimestamp("end") != null && resultSet.getTimestamp("end").getTime() < pluginUtils.currentTime())
                                isActive = true;
                            punishmentLookup.add(new TempPunishment("IP-BAN", pluginUtils.getDate(resultSet.getTimestamp("start").getTime()), pluginUtils.getDate(resultSet.getTimestamp("end").getTime()), punishmentData.getBanReason(resultSet.getInt("reason")).getName(), resultSet.getString("name"), isActive));
                        }
                    }
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                break;
        }

        return new PunishmentLookup(punishmentLookup);
    }

    public ArrayList<String> getAlts(long ipId) {
        ArrayList<String> alts = new ArrayList<>();
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user`.`name`, `user`.`id` FROM `user_ip` INNER JOIN `user` ON `user`.`id` = `user_ip`.`user_id` WHERE `user_ip`.`ip_id` = ?;");
            preparedStatement.setLong(1, ipId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    if (isBanned(new TempInfoStoreUser(resultSet.getLong("id"), null, null, 0L))) {
                        alts.add(ChatColor.RED + "§m" + resultSet.getString("name"));
                    } else {
                        alts.add(resultSet.getString("name"));
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return alts;
    }

    public int getBanViolations(long id, Reason reason) {
        int vios = 0;
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_ban` WHERE `target_user` = ? AND `reason` = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, reason.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    vios = resultSet.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vios + 1;
    }

    public int getMuteViolations(long id, Reason reason) {
        int vios = 0;
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_mute` WHERE `target_user` = ? AND `reason` = ? AND `valid` = 1;");
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, reason.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    vios = resultSet.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vios + 1;
    }

    public int[] getInformation() {

        int users = 0, bans = 0, mutes = 0, kicks = 0;

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `user`;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    users = resultSet.getInt("count");
                }
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_ban`;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bans = resultSet.getInt("count");
                }
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_mute`;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mutes = resultSet.getInt("count");
                }
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_kick`;");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    kicks = resultSet.getInt("count");
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return new int[]{users, bans, mutes, kicks};
    }

    public int[] getStaffLookup(Long userId) {

        int bans = 0, mutes = 0, kicks = 0;

        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_ban` WHERE `punisher_user` = ?;");
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bans = resultSet.getInt("count");
                }
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_mute` WHERE `punisher_user` = ?;");
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    mutes = resultSet.getInt("count");
                }
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(`id`) AS `count` FROM `punishment_kick` WHERE `punisher_user` = ?;");
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    kicks = resultSet.getInt("count");
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return new int[]{bans, mutes, kicks};
    }

    public IPBan getIPBan(CoreUser coreUser) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `punishment_ipban` WHERE `target_ip` = ? AND `end` > CURRENT_TIMESTAMP;");
            preparedStatement.setLong(1, coreUser.getIpId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new IPBan(punishmentData.getIPBanReason(resultSet.getInt("reason")), resultSet.getTimestamp("end").getTime(), true, false, resultSet.getLong("id"));
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public boolean ignorePlayer(CoreUser coreUser, TempInfoStoreUser tempInfoStoreUser) {
        if (!coreUser.getIgnoredUsers().containsKey(tempInfoStoreUser.getName())) {
            coreUser.getIgnoredUsers().put(tempInfoStoreUser.getName(), tempInfoStoreUser.getId());
            try (Connection connection = connectionPoolManager.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user_ignore` (user_id, user_ignored) VALUES (?, ?);");
                preparedStatement.setLong(1, coreUser.getUserId());
                preparedStatement.setLong(2, tempInfoStoreUser.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean unignorePlayer(CoreUser coreUser, TempInfoStoreUser tempInfoStoreUser) {

        if (coreUser.getIgnoredUsers().containsKey(tempInfoStoreUser.getName())) {
            coreUser.getIgnoredUsers().remove(tempInfoStoreUser.getName());
            try (Connection connection = connectionPoolManager.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `user_ignore` WHERE `user_id` = ? AND `user_ignored` = ?;");
                preparedStatement.setLong(1, coreUser.getUserId());
                preparedStatement.setLong(2, tempInfoStoreUser.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public Map<String, Long> getIgnoredUsers(Long id) {
        Map<String, Long> ignoredUsers = new HashMap<>();
        try (Connection connection = connectionPoolManager.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `user_ignored`, `name` FROM `user` INNER JOIN `user_ignore` ON `user_ignored` = `id` WHERE `user_id` = ?;");
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ignoredUsers.put(resultSet.getString("name"), resultSet.getLong("user_ignored"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ignoredUsers;
    }

}
