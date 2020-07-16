package gg.hound.core.commands.punishment;

import gg.hound.core.CorePlugin;
import gg.hound.core.redis.RedisManager;
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

public class UnmuteCommand implements CommandExecutor {

    private final SQLManager sqlManager;
    private final PluginUtils pluginUtils;
    private final UserManager userManager;
    private final RedisManager redisManager;
    private final CorePlugin corePlugin;

    public UnmuteCommand(SQLManager sqlManager, PluginUtils pluginUtils, UserManager userManager, RedisManager redisManager, CorePlugin corePlugin) {
        this.sqlManager = sqlManager;
        this.pluginUtils = pluginUtils;
        this.userManager = userManager;
        this.redisManager = redisManager;
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {

        if (!(commandSender instanceof Player))
            return true;

        if (!commandSender.hasPermission("punishments.unmute")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /unmute <user>");
            return true;
        }

        Player player = (Player) commandSender;
        CoreUser coreUser = userManager.getUser(player.getUniqueId());

        if (coreUser.getUserId() <= 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "An Internal Error has occurred! Reconnect, if this continues please contact a developer.");
            return true;
        }

        TempInfoStoreUser target = sqlManager.getUser(strings[0]);

        if (target == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "That player is not in the database.");
            return true;
        }

        if (!sqlManager.isMuted(target)) {
            commandSender.sendMessage(corePlugin.getPrefix() + target.getName() + " is not currently muted!");
            return true;
        }

        if (strings.length == 1) {
            sqlManager.unmutePlayer(target, coreUser.getTempUser(), 0);
            redisManager.sendUnmute(target.getUuid(), pluginUtils.currentTime());
            commandSender.sendMessage(corePlugin.getPrefix() + target.getName() + " has been un-muted!");

        } else if (strings.length == 2) {

            int time = pluginUtils.getTime(strings[1]);

            if (time > 0) {
                sqlManager.unmutePlayer(target, coreUser.getTempUser(), time);
                redisManager.sendUnmute(target.getUuid(), pluginUtils.getUnmuteTime(time));
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + target.getName() + " will be un-muted at " + pluginUtils.getDate(pluginUtils.getUnmuteTime(time)));
            } else {
                commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Invalid Time!");
            }
        }

        return true;
    }

}
