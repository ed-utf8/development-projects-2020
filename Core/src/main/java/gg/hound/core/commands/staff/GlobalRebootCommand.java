package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.redis.RedisManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalRebootCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final RedisManager redisManager;

    public GlobalRebootCommand(CorePlugin corePlugin, RedisManager redisManager) {
        this.corePlugin = corePlugin;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        redisManager.sendReboot();

        return true;
    }

}