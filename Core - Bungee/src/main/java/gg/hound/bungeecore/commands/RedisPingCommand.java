package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.redis.RedisManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RedisPingCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final RedisManager redisManager;

    public RedisPingCommand(BungeeCorePlugin bungeeCorePlugin, RedisManager redisManager) {
        super("redis-ping", "perms.developer", "redis-pong");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.redisManager = redisManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        if (redisManager.getPing())
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§aRedis is all good and responsive.").create());
        else
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cError while pinging redis, restart required.").create());

    }
}
