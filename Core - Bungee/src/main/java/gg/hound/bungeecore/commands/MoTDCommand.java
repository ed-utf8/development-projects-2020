package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.motd.MessageOfTheDay;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class MoTDCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final MessageOfTheDay messageOfTheDay;

    public MoTDCommand(BungeeCorePlugin bungeeCorePlugin, MessageOfTheDay messageOfTheDay) {
        super("motd", "perms.developer", "messageoftheday");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.messageOfTheDay = messageOfTheDay;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!commandSender.hasPermission("perms.developer")) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getStaffPrefix() + "§cYou do not have permission to run this command.").create());
            return;
        }


        if (strings.length < 2) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getStaffPrefix() + "§cUsage: /motd <line> <message>").create());
            return;
        }

        if (strings[0].equalsIgnoreCase("1")) {
            messageOfTheDay.setLine(1, strJoin(strings, " ").replaceFirst("1 ", ""));
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getStaffPrefix() + "MoTD Line 1: " + ChatColor.translateAlternateColorCodes('&', messageOfTheDay.getLine(1))).create());
            return;
        }
        if (strings[0].equalsIgnoreCase("2")) {
            messageOfTheDay.setLine(2, strJoin(strings, " ").replaceFirst("2 ", ""));
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getStaffPrefix() + "MoTD Line 2: " + ChatColor.translateAlternateColorCodes('&', messageOfTheDay.getLine(2))).create());
            return;
        }

        return;
    }

    private String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }
}
