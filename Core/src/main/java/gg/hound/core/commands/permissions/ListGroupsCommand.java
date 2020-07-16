package gg.hound.core.commands.permissions;

import gg.hound.core.CorePlugin;
import gg.hound.core.group.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListGroupsCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final GroupManager groupManager;

    public ListGroupsCommand(CorePlugin corePlugin, GroupManager groupManager) {
        this.corePlugin = corePlugin;
        this.groupManager = groupManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("permissions.use")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String groupName : groupManager.getPermissionGroupHashMap().keySet()) {
            stringBuilder.append("\n");
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append("\u2022 ");
            stringBuilder.append(ChatColor.AQUA);
            stringBuilder.append(groupName);
        }
        commandSender.sendMessage(corePlugin.getPrefix() + "Currently available groups:");
        commandSender.sendMessage(stringBuilder.toString());
        return true;
    }
}
