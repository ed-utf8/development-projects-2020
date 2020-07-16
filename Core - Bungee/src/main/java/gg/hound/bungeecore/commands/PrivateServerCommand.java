package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.servermanager.PrivateServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PrivateServerCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final PrivateServerManager privateServerManager;

    public PrivateServerCommand(BungeeCorePlugin bungeeCorePlugin, PrivateServerManager privateServerManager) {
        super("privateserver", "private.servers", "myserver", "createserver", "psm");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.privateServerManager = privateServerManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            helpMessage(commandSender);
            return;
        }

        if (strings[0].equalsIgnoreCase("shutdown") && strings.length == 2) {
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
                privateServerManager.shutdownServer(proxiedPlayer, strings[1].toLowerCase());
                return;
            }
            commandSender.sendMessage("Noob not a player");
        }


        if (strings[0].equalsIgnoreCase("create") && strings.length == 2) {
            if (commandSender instanceof ProxiedPlayer) {
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
                privateServerManager.createServer(proxiedPlayer, strings[1].toLowerCase());
                return;
            }
            commandSender.sendMessage("Noob not a player");
            return;
        }

        helpMessage(commandSender);
    }

    private void helpMessage(CommandSender commandSender) {
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/privateserver create <type>").create());
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/privateserver shutdown <name>").create());
        //commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/servermanager list").create());
    }


}
