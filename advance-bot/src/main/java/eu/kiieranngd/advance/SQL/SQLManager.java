package eu.kiieranngd.advance.SQL;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Commands.command.CustomCommand;
import eu.kiieranngd.advance.Commands.command.GlobalCommand;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.ConfigManager;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Music.TrackScheduler;
import eu.kiieranngd.advance.User.DiscordUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by utf_8 on 28/03/2017
 */

public class  SQLManager {

    private final ConnectionPoolManager pool;

    private Manager manager;
    private Advance advance;
    private MusicManager musicManager;

    public SQLManager(Manager manager, Advance advance, MusicManager musicManager) {
        pool = new ConnectionPoolManager();
        this.manager = manager;
        this.advance = advance;
        this.musicManager = musicManager;
    }

    public boolean fail() {
        return pool.hasFailed();
    }

    public void onShutdown() {
        pool.closePool();
    }

    private void initUser(String id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `users`(`userID`) VALUES (?);");
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addGuild(String userID, String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `guild_users`(`guildID`, `userID`) VALUES (?, ?);");
            preparedStatement.setLong(1, manager.getGuild(guildID).getId());
            preparedStatement.setLong(2, manager.getUser(userID).getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadUser(String id, String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `userID` = ?;");
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    manager.addUser(new DiscordUser(resultSet.getLong("id")
                            , resultSet.getString("userID")
                            , getUserGuilds(resultSet.getLong("id"))
                            , getBalance(resultSet.getLong("id"))
                            , resultSet.getBoolean("donator")
                            , isAdmin(resultSet.getLong("id"))
                            , resultSet.getBoolean("blacklisted")));
                } else {
                    initUser(id);
                    try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                        if (resultSet1.next()) {
                            manager.addUser(new DiscordUser(resultSet1.getLong("id"), resultSet1.getString("userID"), manager.getGuild(guildID)));
                            addGuild(id, guildID);
                            initEcon(resultSet1.getLong("id"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initEcon(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `economy`(`userID`) VALUE (?);");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isAdmin(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `bot_admins` WHERE `userID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<CGuild> getUserGuilds(long id) {
        List<CGuild> guilds = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guild_users` WHERE `userID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    guilds.add(manager.getGuild(resultSet.getLong("guildID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guilds;
    }

    public void addGuild(String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `guilds`(`guildID`) VALUES (?);");
            preparedStatement.setString(1, guildID);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT * FROM `guilds` WHERE `guildID` = ?;");
            preparedStatement.setString(1, guildID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                CGuild cGuild;
                if (resultSet.next()) {
                    cGuild = new CGuild(resultSet.getLong("id"), resultSet.getString("guildID"), getAdmins(resultSet.getLong("id")), getMods(resultSet.getLong("id")), getUsers(resultSet.getLong("id")), getConfig(resultSet.getLong("id")));
                    manager.addGuild(cGuild);
                    musicManager.addGuild(cGuild);
                    preparedStatement = connection.prepareStatement("INSERT INTO `guild_config` VALUES (?, ?);");
                    preparedStatement.setLong(1, cGuild.getId());
                    preparedStatement.setString(2, "!");
                    preparedStatement.executeUpdate();
                } else {
                    logError("Error loading guild with id: " + guildID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ConfigManager getConfig(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guild_config` WHERE `guildID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ConfigManager(resultSet.getString("key"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CGuild> getGuilds() {
        List<CGuild> guilds = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guilds` WHERE `active` = ?;");
            preparedStatement.setBoolean(1, true);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                CGuild cGuild;
                while (resultSet.next()) {
                    cGuild = new CGuild(resultSet.getLong("id"), resultSet.getString("guildID"), getAdmins(resultSet.getLong("id")), getMods(resultSet.getLong("id")), getUsers(resultSet.getLong("id")), getConfig(resultSet.getLong("id")));
                    guilds.add(cGuild);
                    musicManager.addGuild(cGuild);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guilds;
    }

    private List<Long> getUsers(long id) {
        List<Long> discordUsers = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guild_users` WHERE `guildID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    discordUsers.add(resultSet.getLong("userID"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return discordUsers;
    }

    private boolean isDonator(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `id` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("donator");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String discordID(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `id` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("userID");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Long> getAdmins(long id) {
        List<Long> admins = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guild_admins` WHERE `guildID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    admins.add(resultSet.getLong("userID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    private List<Long> getMods(long id) {
        List<Long> mods = new ArrayList<>();
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guild_mods` WHERE `guildID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    mods.add(resultSet.getLong("userID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mods;
    }

    private DiscordUser getUser(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `id` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new DiscordUser(resultSet.getLong("id")
                            , resultSet.getString("userID")
                            , getUserGuilds(resultSet.getLong("id"))
                            , getBalance(resultSet.getLong("id"))
                            , resultSet.getBoolean("donator")
                            , isAdmin(resultSet.getLong("id"))
                            , resultSet.getBoolean("blacklisted"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private long getBalance(long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `economy` WHERE `userID` = ?;");
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addMessage(DiscordUser discordUser, CGuild cGuild, String message) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `user_messages`(`userID`, `message`) VALUES (?, ?);");
            preparedStatement.setLong(1, discordUser.getId());
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT * FROM `user_messages` WHERE `userID` = ? ORDER BY `date` DESC LIMIT 1;");
            preparedStatement.setLong(1, discordUser.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("INSERT INTO `guild_messages` VALUES (?, ?, ?)");
                    preparedStatement.setLong(1, resultSet.getLong("id"));
                    preparedStatement.setLong(2, cGuild.getId());
                    preparedStatement.setLong(3, resultSet.getLong("userID"));
                    preparedStatement.executeUpdate();
                } else {
                    logError("Could not save message from user with id: " + discordUser.getId() + "on guild with id: " + cGuild.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logError(String error) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `errors`(`error`) VALUES (?);");
            preparedStatement.setString(1, error);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesGuildExist(String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `guilds` WHERE `guildID` = ?;");
            preparedStatement.setString(1, guildID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void disableGuild(String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `guilds` SET `active` = ? WHERE `guildID` = ?;");
            preparedStatement.setBoolean(1, false);
            preparedStatement.setString(2, guildID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void enableGuild(String guildID) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `guilds` SET `active` = ? WHERE `guildID` = ?;");
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, guildID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCommands(CGuild cGuild) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `commands` WHERE `guildID` = ?;");
            preparedStatement.setLong(1, cGuild.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                Map<String, Command> commandMap = new HashMap<>();
                while (resultSet.next()) {
                    commandMap.put(resultSet.getString("command"), new CustomCommand(resultSet.getString("response"), resultSet.getString("command"), resultSet.getString("description"), manager));
                }
                manager.addGuildCommands(cGuild, commandMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCommands() {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `global_commands`;");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    advance.addCommand(resultSet.getString("command"), new GlobalCommand(resultSet.getString("response"), resultSet.getString("command"), resultSet.getString("description"), manager));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAdmin(CGuild cGuild, long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `guild_admins` VALUES (?, ?);");
            preparedStatement.setLong(1, cGuild.getId());
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAdmin(CGuild cGuild, long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `guild_admins` WHERE `guildID` = ? AND `userID` = ?;");
            preparedStatement.setLong(1, cGuild.getId());
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMod(CGuild cGuild, long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `guild_mods` VALUES (?, ?);");
            preparedStatement.setLong(1, cGuild.getId());
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMod(CGuild cGuild, long id) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `guild_mods` WHERE `guildID` = ? AND `userID` = ?;");
            preparedStatement.setLong(1, cGuild.getId());
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateData() {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement;
            for (DiscordUser discordUser : manager.getUserMap().values()) {
                for (CGuild cGuild : discordUser.getGuilds()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `guild_users` WHERE `guildID` = ? AND `userID` = ?;");
                    preparedStatement.setLong(1, cGuild.getId());
                    preparedStatement.setLong(2, discordUser.getId());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO `guild_users` VALUES (?, ?);");
                            preparedStatement.setLong(1, cGuild.getId());
                            preparedStatement.setLong(2, discordUser.getId());
                            preparedStatement.executeUpdate();
                        }
                    }
                }

                preparedStatement = connection.prepareStatement("UPDATE `economy` SET `balance` = ? WHERE `userID` = ?;");
                preparedStatement.setLong(1, discordUser.getBalance());
                preparedStatement.setLong(2, discordUser.getId());
                preparedStatement.executeUpdate();

                preparedStatement = connection.prepareStatement("SELECT * FROM `users` WHERE `id` = ?;");
                preparedStatement.setLong(1, discordUser.getId());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        discordUser.setDonator(resultSet.getBoolean("donator"));
                    }
                }
            }

            for (CGuild cGuild : manager.getGuilds()) {
                for (long discordUser : cGuild.getAdmins()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `guild_admins` WHERE `guildID` = ? AND `userID` = ?;");
                    preparedStatement.setLong(1, cGuild.getId());
                    preparedStatement.setLong(2, discordUser);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO `guild_admins` VALUES (?, ?);");
                            preparedStatement.setLong(1, cGuild.getId());
                            preparedStatement.setLong(2, discordUser);
                            preparedStatement.executeUpdate();
                        }
                    }
                }

                for (long discordUser : cGuild.getMods()) {
                    preparedStatement = connection.prepareStatement("SELECT * FROM `guild_mods` WHERE `guildID` = ? AND `userID` = ?;");
                    preparedStatement.setLong(1, cGuild.getId());
                    preparedStatement.setLong(2, discordUser);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO `guild_mods` VALUES (?, ?);");
                            preparedStatement.setLong(1, cGuild.getId());
                            preparedStatement.setLong(2, discordUser);
                            preparedStatement.executeUpdate();
                        }
                    }
                }

                for (DiscordUser discordUser : manager.getUserMap().values()) {
                    if (cGuild.getDiscordUsers().contains(discordUser.getId()) && !discordUser.getGuilds().contains(cGuild)) {
                        preparedStatement = connection.prepareStatement("DELETE FROM `guild_users` WHERE `guildID` = ? AND `userID` = ?;");
                        preparedStatement.setLong(1, cGuild.getId());
                        preparedStatement.setLong(2, discordUser.getId());
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void blacklistUser(DiscordUser discordUser) {
        try (Connection connection = pool.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `users` SET `blacklisted` = ? WHERE `id` = ?;");
            preparedStatement.setBoolean(1, discordUser.isBlacklisted());
            preparedStatement.setLong(2, discordUser.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
