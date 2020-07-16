package gg.hound.core.commands.troll;

import gg.hound.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FakeOPCommand implements CommandExecutor {

    private final CorePlugin corePlugin;

    public FakeOPCommand(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
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
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /fakeop <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(arguments[0]);
        if (target == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Player not found");
            return true;
        }

        target.sendMessage("§7§o[" + commandSender.getName() + ": Opped " + target.getName() + "]");
        commandSender.sendMessage(corePlugin.getPrefix() + "Player has been fake opped.");

        return true;
    }
}
