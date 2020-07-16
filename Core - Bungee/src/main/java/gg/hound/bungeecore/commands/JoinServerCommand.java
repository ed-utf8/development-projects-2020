package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.redis.RedisManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JoinServerCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;

    public JoinServerCommand(BungeeCorePlugin bungeeCorePlugin) {
        super("join", "", "");
        this.bungeeCorePlugin = bungeeCorePlugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;

        if (strings.length != 1)
            return;

        ServerInfo serverInfo = bungeeCorePlugin.getProxy().getServerInfo(strings[0]);

        if (serverInfo == null)
            proxiedPlayer.sendMessage(new ComponentBuilder(ChatColor.RED + "Error while joining server: Server Not found!").create());
        else
            proxiedPlayer.connect(serverInfo);
    }
}
