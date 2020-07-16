package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PmToggleCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;

    public PmToggleCommand(CorePlugin corePlugin, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        coreUser.setPrivateMessages(!coreUser.isPrivateMessages());
        player.sendMessage(corePlugin.getPrefix() + "You have " + (coreUser.isPrivateMessages() ? "enabled" : "disabled") + " private messages");

        return true;
    }

}
