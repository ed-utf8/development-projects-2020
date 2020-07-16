package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffToggleCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;

    public StaffToggleCommand(CorePlugin corePlugin, UserManager userManager) {
        this.corePlugin = corePlugin;

        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You can only perform this command as a player");
            return true;
        }

        if (!commandSender.hasPermission("core.staff")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        coreUser.setStaffNotifications(!coreUser.isStaffNotifications());
        player.sendMessage(corePlugin.getPrefix() + "You have " + (coreUser.isStaffNotifications() ? "enabled" : "disabled") + " staff notifications");
        return true;
    }

}
