package gg.hound.arena.sql;

import gg.hound.arena.Arena;
import gg.hound.arena.arenas.ArenaMap;
import gg.hound.arena.match.kit.Kit;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;

public class SQLManager {

    private final ConnectionPoolManager connectionPoolManager;
    private final KitManager kitManager;

    private final long oneDay = 86400000;

    public SQLManager(Arena arena, FileConfiguration fileConfiguration, KitManager kitManager) {
        this.connectionPoolManager = new ConnectionPoolManager(fileConfiguration, arena);

        if (connectionPoolManager.hasFailed()) {
            arena.log("Connection has failed. Shutting down.");
            Bukkit.getServer().shutdown();
        }

        this.kitManager = kitManager;
    }

    public void onDisable() {
        connectionPoolManager.closePool();
    }

    public List<ArenaMap> loadMaps() {
        try (Connection connection = connectionPoolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_arena` WHERE `enabled` = 1;");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<ArenaMap> arenaMaps = new ArrayList<>();
            while (resultSet.next()) {
                arenaMaps.add(new ArenaMap(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("author"), resultSet.getInt("id") % 3 != 0,
                        new Location(Bukkit.getWorld("practice"), resultSet.getInt("corner_one_x"),
                                resultSet.getInt("corner_one_y"),
                                resultSet.getInt("corner_one_z")),
                        new Location(Bukkit.getWorld("practice"), resultSet.getInt("corner_two_x"),
                                resultSet.getInt("corner_two_y"),
                                resultSet.getInt("corner_two_z")),
                        new Location(Bukkit.getWorld("practice"), resultSet.getInt("spawn_left_x"),
                                resultSet.getInt("spawn_left_y"),
                                resultSet.getInt("spawn_left_z"),
                                resultSet.getInt("spawn_left_yaw"),
                                resultSet.getInt("spawn_left_pitch")),
                        new Location(Bukkit.getWorld("practice"), resultSet.getInt("spawn_right_x"),
                                resultSet.getInt("spawn_right_y"),
                                resultSet.getInt("spawn_right_z"),
                                resultSet.getInt("spawn_right_yaw"),
                                resultSet.getInt("spawn_right_pitch"))
                        ));
            }
            return arenaMaps;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public User loadUser(UUID uuid) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `id` FROM `user` WHERE `uuid` = ?;");
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next())
                    return null;

                long id = resultSet.getLong("id");
                boolean donator = verifyUnlimited(id);

                preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_user` WHERE `user_id` = ?");
                preparedStatement.setLong(1, id);

                try (ResultSet pvpUserSet = preparedStatement.executeQuery()) {
                    if (!pvpUserSet.next()) {
                        createUser(id, donator);
                        return new User(id, kitManager.getKits(), donator ? -1 : 20);
                    }

                    int matches = pvpUserSet.getInt("matches");
                    if (matches == -1)
                        matches = donator ? -1 : 20;
                    else if (matches < 20) {
                        if (System.currentTimeMillis() - pvpUserSet.getTimestamp("daily_connection").getTime() >= oneDay)
                            matches = 20;
                    }

                    Map<Kit, Integer> elo = getElo(id, 1);

                    preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_user_settings` WHERE `user_id` = ?;");
                    preparedStatement.setLong(1, id);
                    try (ResultSet settingsSet = preparedStatement.executeQuery()) {
                        if (!settingsSet.next())
                            return new User(id, elo, true, true, matches);

                        if (matches == -1) {
                            preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_giftmatches` WHERE `user_id` = ?;");
                            preparedStatement.setLong(1, id);
                            try (ResultSet giftMatchesSet = preparedStatement.executeQuery()) {
                                if (!giftMatchesSet.next())
                                    return new User(id, elo, settingsSet.getBoolean("duelRequests"), settingsSet.getBoolean("partyRequests"), matches, true, -1);

                                if (System.currentTimeMillis() - giftMatchesSet.getTime("last_use").getTime() >= oneDay)
                                    return new User(id, elo, settingsSet.getBoolean("duelRequests"), settingsSet.getBoolean("partyRequests"), matches, true, -1);
                                else return new User(id, elo, settingsSet.getBoolean("duelRequests"), settingsSet.getBoolean("partyRequests"), matches, false, giftMatchesSet.getTimestamp("last_use").getTime());
                            }
                        } else return new User(id, elo, settingsSet.getBoolean("duelRequests"), settingsSet.getBoolean("partyRequests"), matches);
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }


                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }


            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    private void createUser(long id, boolean donator) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement;
            if (donator) {
                preparedStatement = connection.prepareStatement("INSERT INTO `pvp_user`(`user_id`, `matches`) VALUES (?, ?);");
                preparedStatement.setLong(1, id);
                preparedStatement.setInt(2, -1);
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `pvp_user`(`user_id`) VALUES (?);");
                preparedStatement.setLong(1, id);
            }
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO `pvp_user_settings`(`user_id`) VALUES (?);");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

            for (Kit kit : kitManager.getKits()) {
                preparedStatement = connection.prepareStatement("INSERT INTO `pvp_stats_1v1`(`user_id`, `season`, `kit`) VALUES (?, ?, ?);");
                preparedStatement.setLong(1, id);
                preparedStatement.setInt(2, 1);
                preparedStatement.setInt(3, kit.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private boolean verifyUnlimited(long id) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group_id` FROM `user_group` WHERE `user_id` = ?;");
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    private Map<Kit, Integer> getElo(long id, int season) {
        Map<Kit, Integer> eloMap = new HashMap<>();
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_stats_1v1` WHERE `user_id` = ? AND `season` = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.setInt(2, season);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    eloMap.put(kitManager.getKit(resultSet.getInt("kit")), resultSet.getInt("elo"));
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return eloMap;
    }

    public Map<Kit, ItemStack[]> userKits(long id) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `pvp_user_kit` WHERE `user_id` = ?;");
            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Map<Kit, ItemStack[]> kitMap = new HashMap<>();
                while (resultSet.next()) {
                    kitMap.put(kitManager.getKit(resultSet.getInt("kit_id")), kitManager.parseInventory(resultSet.getString("inventory_layout")));
                }

                return kitMap;
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public void saveStats(long userID, int userElo, int kitID) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `pvp_stats_1v1` SET `elo` = ? WHERE `user_id` = ? AND `kit` = ? AND `season` = ?;");
            preparedStatement.setInt(1, userElo);
            preparedStatement.setLong(2, userID);
            preparedStatement.setInt(3, kitID);
            preparedStatement.setInt(4, 1);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void saveStats(long userIDOne, long userIDTwo, int elo, int kitID) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `pvp_stats_2v2` SET `elo` = ? WHERE `user_low` = ? AND `user_high` = ? AND `kit` = ? AND `season` = ?;");
            preparedStatement.setInt(1, elo);
            preparedStatement.setLong(2, Math.min(userIDOne, userIDTwo));
            preparedStatement.setLong(3, Math.max(userIDOne, userIDTwo));
            preparedStatement.setInt(4, kitID);
            preparedStatement.setInt(5, 1);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void saveMatches(User user) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `pvp_user` SET `matches` = ? WHERE `user_id` = ?;");
            preparedStatement.setInt(1, user.getMatchesRemaining());
            preparedStatement.setLong(2, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void saveKit(User user, Kit kit, String serialisedInventory) {
        try (Connection connection = connectionPoolManager.getConnection()) {
            PreparedStatement preparedStatement;
            if (user.getInventoryLayout(kit) == null) {
                preparedStatement = connection.prepareStatement("INSERT INTO `pvp_user_kit` VALUES (?, ?, ?);");
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setInt(2, kit.getId());
                preparedStatement.setString(3, serialisedInventory);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("UPDATE `pvp_user_kit` SET `inventory_layout` = ? WHERE `user_id` = ? AND `kit_id` = ?;");
                preparedStatement.setString(1, serialisedInventory);
                preparedStatement.setLong(2, user.getId());
                preparedStatement.setInt(3, kit.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

}
