package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class InfoCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;

    public InfoCommand(CorePlugin corePlugin, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {

        if (!commandSender.hasPermission("core.admin")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                int[] stats = sqlManager.getInformation();
                commandSender.sendMessage(ChatColor.GOLD + "Network Statistics");
                commandSender.sendMessage(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "------------------------");
                commandSender.sendMessage(ChatColor.YELLOW + "Unique Users: " + ChatColor.WHITE.toString() + stats[0]);
                commandSender.sendMessage(ChatColor.YELLOW + "Total Bans: " + ChatColor.WHITE.toString() + stats[1]);
                commandSender.sendMessage(ChatColor.YELLOW + "Total Mutes: " + ChatColor.WHITE.toString() + stats[2]);
                commandSender.sendMessage(ChatColor.YELLOW + "Total Kicks: " + ChatColor.WHITE.toString() + stats[3]);
            }
        }.runTaskAsynchronously(corePlugin);

        return true;
    }
}
