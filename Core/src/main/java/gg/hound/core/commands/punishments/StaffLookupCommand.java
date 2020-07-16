package gg.hound.core.commands.punishments;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StaffLookupCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;

    public StaffLookupCommand(CorePlugin corePlugin, SQLManager sqlManager) {
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

        if (arguments.length != 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /slookup <player>");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);
                if (tempInfoStoreUser == null) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "That player cannot be found in our database");
                    return;
                }

                int[] stats = sqlManager.getStaffLookup(tempInfoStoreUser.getId());

                commandSender.sendMessage(ChatColor.GOLD + "User Statistics:");
                commandSender.sendMessage(ChatColor.GOLD + "------------------------");
                commandSender.sendMessage(ChatColor.YELLOW + "Total Bans: " + ChatColor.WHITE.toString() + stats[0]);
                commandSender.sendMessage(ChatColor.YELLOW + "Total Mutes: " + ChatColor.WHITE.toString() + stats[1]);
                commandSender.sendMessage(ChatColor.YELLOW + "Total Kicks: " + ChatColor.WHITE.toString() + stats[2]);
            }
        }.runTaskAsynchronously(corePlugin);

        return true;
    }
}

