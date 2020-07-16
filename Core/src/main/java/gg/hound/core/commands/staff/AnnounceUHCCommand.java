package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.redis.RedisManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnounceUHCCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final RedisManager redisManager;

    public AnnounceUHCCommand(CorePlugin corePlugin, RedisManager redisManager) {
        this.corePlugin = corePlugin;
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("uhc.host")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (!corePlugin.getServerName().toUpperCase().contains("UHC-")) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You cannot use this command when not on a UHC server");
            return true;
        }

        redisManager.sendUHC(corePlugin.getServerName());

        return true;
    }

}
