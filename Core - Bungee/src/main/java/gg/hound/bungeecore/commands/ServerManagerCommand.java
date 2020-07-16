package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.servermanager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class ServerManagerCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final ServerManager serverManager;

    public ServerManagerCommand(BungeeCorePlugin bungeeCorePlugin, ServerManager serverManager) {
        super("servermanager", "perms.developer", "sd", "deploy", "manager");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.serverManager = serverManager;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length < 1) {
            helpMessage(commandSender);
            return;
        }

        if (strings[0].equalsIgnoreCase("list")) {
            serverManager.listGameInstances(commandSender);
            return;
        }

        if (strings[0].equalsIgnoreCase("delete") && strings.length == 2) {
            serverManager.deleteServer(commandSender, strings[1].toUpperCase());
            return;
        }

        if (strings[0].equalsIgnoreCase("create") && strings.length == 6) {
            serverManager.createServer(commandSender, strings[1].toUpperCase(), strings[2], strings[3], Integer.parseInt(strings[4]), Boolean.getBoolean(strings[5]));
            return;
        }

        helpMessage(commandSender);
    }

    private void helpMessage(CommandSender commandSender) {
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/servermanager create <name> <type> <address> <port> false").create());
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/servermanager delete <name>").create());
        commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getServerManagerPrefix() + "/servermanager list").create());
    }


}
