package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteChatCommand implements CommandExecutor {

    private final CorePlugin corePlugin;

    public MuteChatCommand(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("core.staff")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        corePlugin.setChatMuted(!corePlugin.isChatMuted());
        Bukkit.broadcastMessage(corePlugin.getPrefix() + "Chat has been " + (!corePlugin.isChatMuted() ? "enabled" : "disabled"));

        return true;
    }

}
