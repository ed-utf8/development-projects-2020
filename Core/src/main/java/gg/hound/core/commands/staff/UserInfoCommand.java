package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

public class UserInfoCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;

    public UserInfoCommand(CorePlugin corePlugin, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        if (!commandSender.hasPermission("core.admin")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length != 2) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /userinfo <type(uuid/name/id)> <uuid/name/id>");
            return true;
        }

        switch (arguments[0].toLowerCase()) {
            case "uuid": {
                if (!isUUID(arguments[1])) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "Invalid data");
                    break;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendMessage(sqlManager.getUser(UUID.fromString(arguments[1])), commandSender);
                    }
                }.runTaskAsynchronously(corePlugin);
                break;
            }
            case "name": {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendMessage(sqlManager.getUser(arguments[1]), commandSender);
                    }
                }.runTaskAsynchronously(corePlugin);
                break;
            }
            case "id": {
                if (!isNumeric(arguments[1])) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "Invalid data");
                    break;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendMessage(sqlManager.getUser(Long.parseLong(arguments[1])), commandSender);
                    }
                }.runTaskAsynchronously(corePlugin);
                break;
            }
            default: {
                commandSender.sendMessage(corePlugin.getPrefix() + "Invalid data type");
                break;
            }
        }

        return true;
    }


    private void sendMessage(TempInfoStoreUser tempInfoStoreUser, CommandSender commandSender) {

        if (tempInfoStoreUser == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "§cYou have entered invalid data!");
            return;
        }

        commandSender.sendMessage(ChatColor.GOLD + "User Information");
        commandSender.sendMessage(ChatColor.GOLD + "§m------------------------");
        commandSender.sendMessage(ChatColor.YELLOW + "Username§7: §7" + tempInfoStoreUser.getName());
        commandSender.sendMessage(ChatColor.YELLOW + "User UUID§7: §7" + tempInfoStoreUser.getUuid());
        commandSender.sendMessage(ChatColor.YELLOW + "User Id§7: §7" + tempInfoStoreUser.getId());
        commandSender.sendMessage(ChatColor.YELLOW + "Users Accounts§7: §7" + Arrays.toString(sqlManager.getAlts(tempInfoStoreUser.getIpId()).toArray()));
        commandSender.sendMessage(ChatColor.YELLOW + "Is Banned§7: §7" + sqlManager.isBanned(tempInfoStoreUser));
        commandSender.sendMessage(ChatColor.YELLOW + "Is Muted§7: §7" + sqlManager.isMuted(tempInfoStoreUser));
        commandSender.sendMessage(ChatColor.YELLOW + "Is IPBanned§7: §7" + sqlManager.isIpBanned(tempInfoStoreUser));

    }

    private boolean isNumeric(String str) {
        try {
            long i = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isUUID(String uuid) {
        try {
            UUID uuid1 = UUID.fromString(uuid);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
