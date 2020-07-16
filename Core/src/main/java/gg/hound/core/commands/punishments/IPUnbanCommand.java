package gg.hound.core.commands.punishments;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IPUnbanCommand implements CommandExecutor {

    private final SQLManager sqlManager;
    private final PluginUtils pluginUtils;
    private final UserManager userManager;
    private final CorePlugin corePlugin;

    public IPUnbanCommand(SQLManager sqlManager, PluginUtils pluginUtils, UserManager userManager, CorePlugin corePlugin) {
        this.sqlManager = sqlManager;
        this.pluginUtils = pluginUtils;
        this.userManager = userManager;
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {

        if (!(commandSender instanceof Player))
            return true;

        if (!commandSender.hasPermission("punishments.unipban")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /unbanip <user>");
            return true;
        }

        Player player = (Player) commandSender;
        CoreUser coreUser = userManager.getUser(player.getUniqueId());

        if (coreUser.getUserId() <= 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "An Internal Error has occurred! Reconnect, if this continues please contact a developer.");
            return true;
        }

        TempInfoStoreUser target = sqlManager.getUser(arguments[0]);

        if (target == null) {
            player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Error: This player cannot be found!");
            return true;
        }

        if (!sqlManager.isIpBanned(target)) {
            commandSender.sendMessage(corePlugin.getPrefix() + target.getName() + " is not currently ip-banned!");
            return true;
        }

        if (arguments.length == 1) {
            sqlManager.unbanIp(target, coreUser.getTempUser(), 0);
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + target.getName() + " has been unbanned.");
        } else if (arguments.length == 2) {
            int time = pluginUtils.getTime(arguments[1]);

            if (time > 0) {
                sqlManager.unbanIp(target, coreUser.getTempUser(), time);
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + target.getName() + " has been unbanned.");
            } else {
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Invalid Time!");
            }
        }


        return true;
    }
}
