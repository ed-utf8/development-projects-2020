package gg.hound.core.commands.staff;

import com.google.common.base.Joiner;
import gg.hound.core.CorePlugin;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final RedisManager redisManager;

    public StaffChatCommand(CorePlugin corePlugin, UserManager userManager, RedisManager redisManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        if (!commandSender.hasPermission("core.staff")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /staffchat <message>");
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        if (!coreUser.isStaffNotifications()) {
            player.sendMessage(corePlugin.getPrefix() + "You have staff notifications disabled, enable them to send messages.");
            return true;
        }

        redisManager.sendStaffChat(coreUser.getUserName(), Joiner.on(" ").skipNulls().join(arguments));
        return true;
    }
}
