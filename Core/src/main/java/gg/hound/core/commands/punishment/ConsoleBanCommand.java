package gg.hound.core.commands.punishment;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.Punishment;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsoleBanCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;
    private final PluginUtils pluginUtils;
    private final PunishmentData punishmentData;
    private final RedisManager redisManager;

    public ConsoleBanCommand(CorePlugin corePlugin, SQLManager sqlManager, PluginUtils pluginUtils, PunishmentData punishmentData, RedisManager redisManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
        this.pluginUtils = pluginUtils;
        this.punishmentData = punishmentData;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (commandSender instanceof Player)
            return true;

        if (arguments.length != 2) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /consoleban <user> <chargeback>");
            return true;
        }

        TempInfoStoreUser consoleUser = sqlManager.getUser(1L);
        TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);
        if (tempInfoStoreUser == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "That player is not in the database.");
            return true;
        }

        Punishment ban = new Punishment(tempInfoStoreUser, consoleUser, pluginUtils.getTime("p"));
        Reason reason = punishmentData.getBanReason("Console Ban");

        if (!arguments[1].equalsIgnoreCase("true")) {
            reason.setCustomReason("Illegal Modifications");

            Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.DARK_RED + arguments[0] + ChatColor.RED + " has been banned for Illegal Modifications");
        } else reason.setCustomReason("Store - Chargeback");

        ban.setReason(reason);
        sqlManager.banPlayer(ban);
        Player player = Bukkit.getPlayer(arguments[0]);
        if (player != null)
            player.kickPlayer(corePlugin.getPrefix() + "You have been permanently banned for " + ban.getReason());
        else
            redisManager.sendBan(tempInfoStoreUser.getUuid(), reason.getCustomReason());

        return true;
    }
}
