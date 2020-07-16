package gg.hound.core.commands.permissions;

import gg.hound.core.CorePlugin;
import gg.hound.core.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerOpCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;

    public PlayerOpCommand(CorePlugin corePlugin, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You can't OP the console");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("bukkit.command.op")) {
            player.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (userManager.getUser(player.getUniqueId()).isDisguised() && !player.hasPermission("rank.developer"))
            return true;

        player.setOp(!player.isOp());
        player.sendMessage(corePlugin.getPrefix() + " You are " + (player.isOp() ? "now" : "no longer") + " OP");
        return true;
    }
}


