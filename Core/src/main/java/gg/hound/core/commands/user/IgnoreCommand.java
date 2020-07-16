package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final SQLManager sqlManager;

    public IgnoreCommand(CorePlugin corePlugin, UserManager userManager, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.sqlManager = sqlManager;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Ignore Help");
            commandSender.sendMessage(corePlugin.getPrefix() + "/ignore add <name>");
            commandSender.sendMessage(corePlugin.getPrefix() + "/ignore remove <name>");
            commandSender.sendMessage(corePlugin.getPrefix() + "/ignore list");
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        switch (arguments[0].toLowerCase()) {
            case "add": {
                if (arguments.length != 2) {
                    player.sendMessage(corePlugin.getPrefix() + "Usage: /ignore add <name>");
                    return true;
                }

                TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[1]);
                if (tempInfoStoreUser == null) {
                    player.sendMessage(corePlugin.getPrefix() + "That player could not be found in our database");
                    return true;
                }

                if (sqlManager.ignorePlayer(coreUser, tempInfoStoreUser))
                    player.sendMessage(corePlugin.getPrefix() + arguments[1] + " has been ignored.");
                else
                    player.sendMessage(corePlugin.getPrefix() + arguments[1] + " is already ignored.");

                break;
            }
            case "remove": {
                if (arguments.length != 2) {
                    player.sendMessage(corePlugin.getPrefix() + "Usage: /ignore remove <name>");
                    return true;
                }

                TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[1]);
                if (tempInfoStoreUser == null) {
                    player.sendMessage(corePlugin.getPrefix() + "That player could not be found in our database");
                    return true;
                }

                if (sqlManager.unignorePlayer(coreUser, tempInfoStoreUser))
                    player.sendMessage(corePlugin.getPrefix() + arguments[1] + " has been un-ignored.");
                else
                    player.sendMessage(corePlugin.getPrefix() + arguments[1] + " is not ignored.");

                break;
            }
            case "list": {
                player.sendMessage(ignoredUsers(coreUser));
                break;
            }
            default: {
                player.sendMessage(corePlugin.getPrefix() + "§c§lIgnore Help");
                player.sendMessage(corePlugin.getPrefix() + "§c/ignore add <name>");
                player.sendMessage(corePlugin.getPrefix() + "§c/ignore remove <name>");
                player.sendMessage(corePlugin.getPrefix() + "§c/ignore list");
                break;
            }
        }
        return true;
    }

    private String ignoredUsers(CoreUser coreUser) {
        StringBuilder stringBuilder = new StringBuilder(corePlugin.getPrefix() + "Ignored Users: ");
        for (String name : coreUser.getIgnoredUsers().keySet()) {
            stringBuilder.append("\n");
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append("\u2022 ");
            stringBuilder.append(ChatColor.RED);
            stringBuilder.append(name);
        }

        return stringBuilder.toString();
    }

}
