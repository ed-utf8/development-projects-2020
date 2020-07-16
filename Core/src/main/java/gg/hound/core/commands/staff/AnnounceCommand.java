package gg.hound.core.commands.staff;

import com.google.common.base.Joiner;
import gg.hound.core.CorePlugin;
import gg.hound.core.redis.RedisManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnounceCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final RedisManager redisManager;

    public AnnounceCommand(CorePlugin corePlugin, RedisManager redisManager) {
        this.corePlugin = corePlugin;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("core.admin")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /announce <message>");
            return true;
        }

        redisManager.sendAnnouncement(Joiner.on(" ").skipNulls().join(arguments));

        return true;
    }
}
