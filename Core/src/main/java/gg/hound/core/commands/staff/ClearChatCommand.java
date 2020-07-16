package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand implements CommandExecutor {

    private final CorePlugin corePlugin;

    public ClearChatCommand(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("core.staff")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (!online.hasPermission("core.staff")) {
                for (int i = 0; i < 58; i++)
                    online.sendMessage(" ");

                online.sendMessage(corePlugin.getPrefix() + "Chat has been cleared by a Staff Member!");
            } else
                online.sendMessage(corePlugin.getPrefix() + "Chat has been cleared by a Staff Member! (Your chat has not been cleared as you are staff!)");
        }

        return true;
    }

}
