package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.redis.RedisManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JoinMeCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final RedisManager redisManager;

    private final ConcurrentHashMap<UUID, Integer> onCooldown;

    public JoinMeCommand(BungeeCorePlugin bungeeCorePlugin, RedisManager redisManager) {
        super("joinme", "perms.media", "joinmygame");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.redisManager = redisManager;

        this.onCooldown = new ConcurrentHashMap<>();

        bungeeCorePlugin.getProxy().getScheduler().schedule(bungeeCorePlugin, () -> {
            for (Map.Entry<UUID, Integer> cooldown : onCooldown.entrySet()) {
                if (cooldown.getValue() > 1) {
                    cooldown.setValue(cooldown.getValue() - 1);
                } else {
                    onCooldown.remove(cooldown.getKey());
                }
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

        if (!proxiedPlayer.hasPermission("perms.developer")) {
            if (onCooldown.containsKey(proxiedPlayer.getUniqueId())) {
                commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getPrefix() + "§cYou cannot use this command again yet!").create());
                return;
            }

            onCooldown.put(proxiedPlayer.getUniqueId(), 5);
        }
        redisManager.sendJoinMe(proxiedPlayer.getName(), proxiedPlayer.getServer().getInfo().getName());


    }
}
