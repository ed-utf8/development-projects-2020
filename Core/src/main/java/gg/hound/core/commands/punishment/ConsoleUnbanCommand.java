package gg.hound.core.commands.punishment;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.util.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsoleUnbanCommand implements CommandExecutor {

    private final SQLManager sqlManager;
    private final PluginUtils pluginUtils;
    private final CorePlugin corePlugin;

    public ConsoleUnbanCommand(SQLManager sqlManager, PluginUtils pluginUtils, CorePlugin corePlugin) {
        this.sqlManager = sqlManager;
        this.pluginUtils = pluginUtils;
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (commandSender instanceof Player) {
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Usage: /consoleunban <user> [length S|M|H|D|N|Y|P]");
            return true;
        }

        TempInfoStoreUser target = sqlManager.getUser(arguments[0]);

        if (target == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "That player is not in the database.");
            return true;
        }

        if (!sqlManager.isBanned(target)) {
            commandSender.sendMessage(corePlugin.getPrefix() + target.getName() + " is not currently banned!");
            return true;
        }

        TempInfoStoreUser executor = sqlManager.getUser(1L);

        if (arguments.length == 1) {
            sqlManager.unbanPlayer(target, executor, 0);
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + target.getName() + " has been unbanned.");
        } else if (arguments.length == 2) {
            int time = pluginUtils.getTime(arguments[1]);

            if (time > 0) {
                sqlManager.unbanPlayer(target, executor, time);
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + target.getName() + " has been unbanned.");
            } else {
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Invalid Time!");
            }
        }

        return true;
    }
}
