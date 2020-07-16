package gg.hound.core.commands.permissions;

import gg.hound.core.CorePlugin;
import gg.hound.core.group.Group;
import gg.hound.core.group.GroupManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final GroupManager groupManager;
    private final SQLManager sqlManager;
    private final UserManager userManager;

    public PermissionsCommand(CorePlugin corePlugin, GroupManager groupManager, SQLManager sqlManager, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.groupManager = groupManager;
        this.sqlManager = sqlManager;
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("permissions.use")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 2) {
            sendHelpMessage(commandSender);
            return true;
        }

        TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);
        if (tempInfoStoreUser == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "That player has never joined the network.");
            return true;
        }

        if (arguments.length == 2) {
            if (arguments[1].equalsIgnoreCase("list")) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String groupName : sqlManager.getGroups(tempInfoStoreUser)) {
                    stringBuilder.append("\n");
                    stringBuilder.append(ChatColor.GRAY);
                    stringBuilder.append("\u2022 ");
                    stringBuilder.append(ChatColor.AQUA);
                    stringBuilder.append(groupName);
                }
                commandSender.sendMessage(corePlugin.getPrefix() + arguments[0] + " has the following groups:");
                commandSender.sendMessage(stringBuilder.toString());
            } else sendHelpMessage(commandSender);
        } else if (arguments.length == 3) {
            if (arguments[2].equalsIgnoreCase("default")) {
                commandSender.sendMessage(corePlugin.getPrefix() + "You cannot add/remove this rank");
                return true;
            }

            Group permissionGroup = groupManager.getGroup(arguments[2]);
            if (permissionGroup == null) {
                commandSender.sendMessage(corePlugin.getPrefix() + "This group does not exist");
                return true;
            }

            if (permissionGroup.getRankName().equalsIgnoreCase("default")) {
                commandSender.sendMessage(corePlugin.getPrefix() + "You cannot set the default rank as everybody has this!");
                return true;
            }

            if (commandSender instanceof Player) {
                CoreUser coreUser = userManager.getUser((((Player) commandSender).getUniqueId()));
                if (coreUser == null)
                    return true;

                if (coreUser.getHighest(CoreUser.PowerType.ASSIGN).getAssignPower() <= permissionGroup.getNeededAssignPower()) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "You do not have the power to assign this rank.");
                    return true;
                }
            }

            if (arguments[1].equalsIgnoreCase("add")) {
                sqlManager.addGroup(tempInfoStoreUser, permissionGroup);
                commandSender.sendMessage(corePlugin.getPrefix() + "Successfully assigned " + arguments[2] + " to " + arguments[0]);
            } else if (arguments[1].equalsIgnoreCase("remove")) {
                sqlManager.removeGroup(tempInfoStoreUser, permissionGroup);
                commandSender.sendMessage(corePlugin.getPrefix() + "Successfully removed " + arguments[2] + " from " + arguments[0]);
            } else
                sendHelpMessage(commandSender);
        } else
            sendHelpMessage(commandSender);

        return true;
    }

    private void sendHelpMessage(CommandSender commandSender) {
        commandSender.sendMessage(corePlugin.getPrefix() + "/permissions <user> add <rank>");
        commandSender.sendMessage(corePlugin.getPrefix() + "/permissions <user> remove <rank>");
        commandSender.sendMessage(corePlugin.getPrefix() + "/permissions <user> list");
    }
}
