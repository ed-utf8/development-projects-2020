package gg.hound.core.commands.punishments;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.temp.PunishmentLookup;
import gg.hound.core.punishments.temp.TempPunishment;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LookupCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;

    public LookupCommand(CorePlugin corePlugin, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command.");
            return true;
        }

        if (!commandSender.hasPermission("punishments.lookup")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 2) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /lookup <player> <type>");
            return true;
        }

        TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);

        if (tempInfoStoreUser == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "This player cannot be found in our database.");
            return true;
        }

        if (arguments[1].equalsIgnoreCase("ban") || arguments[1].equalsIgnoreCase("mute") || arguments[1].equalsIgnoreCase("kick") || arguments[1].equalsIgnoreCase("ipban")) {
            PunishmentLookup punishmentLookup = sqlManager.getPlayerPunishmentLookup(arguments[1], tempInfoStoreUser);

            if (punishmentLookup.getTempPunishmentArrayList().size() < 1) {
                commandSender.sendMessage(corePlugin.getPrefix() + "Unable to find any punishment data for this user.");
                return true;
            }

            commandSender.sendMessage(ChatColor.AQUA + "Lookup Information for" + ChatColor.GRAY + ": " + ChatColor.WHITE + tempInfoStoreUser.getName() + ChatColor.GRAY + "(LIMIT: 10)");
            for (TempPunishment tempPunishment : punishmentLookup.getTempPunishmentArrayList()) {
                if (tempPunishment.isActive()) {
                    commandSender.sendMessage(ChatColor.AQUA + tempPunishment.getPunishmentType()
                        + ChatColor.GRAY + " - "
                        + ChatColor.WHITE + tempPunishment.getReason()
                        + ChatColor.GRAY + " - "
                        + ChatColor.AQUA + tempPunishment.getPunisher()
                        + ChatColor.GRAY + " - "
                        + ChatColor.GREEN + tempPunishment.getStartTime()
                        + ChatColor.GRAY + " - "
                        + ChatColor.GREEN + tempPunishment.getEndTime()
                    );
                } else {
                    commandSender.sendMessage(ChatColor.AQUA + tempPunishment.getPunishmentType()
                        + ChatColor.GRAY + " - "
                        + ChatColor.WHITE + tempPunishment.getReason()
                        + ChatColor.GRAY + " - "
                        + ChatColor.AQUA + tempPunishment.getPunisher()
                        + ChatColor.GRAY + " - "
                        + ChatColor.GREEN + tempPunishment.getStartTime()
                        + ChatColor.GRAY + " - "
                        + ChatColor.RED + tempPunishment.getEndTime()
                    );
                }
            }
            return true;
        }

        commandSender.sendMessage(ChatColor.RED + "Unable to find PunishmentType.");
        return true;
    }
}
