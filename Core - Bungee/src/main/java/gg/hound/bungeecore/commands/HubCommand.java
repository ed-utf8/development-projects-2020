package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.servermanager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ServerManager serverManager;

    public HubCommand(BungeeCorePlugin bungeeCorePlugin, ServerManager serverManager) {
        super("hub", "", "lobby");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.serverManager = serverManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }

        if (((ProxiedPlayer) commandSender).getServer().getInfo().getName().toLowerCase().startsWith("lobby-")) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§cYou are already connected to a lobby.").create());
            return;
        }
        ((ProxiedPlayer) commandSender).connect(serverManager.getRandomHub());
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "§aConnecting you to a Lobby Server.").create());

    }
}
