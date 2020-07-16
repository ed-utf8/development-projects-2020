package gg.hound.core.commands.staff;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.PunishmentData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutoMuteCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final PunishmentData punishmentData;

    public AutoMuteCommand(CorePlugin corePlugin, PunishmentData punishmentData) {
        this.corePlugin = corePlugin;
        this.punishmentData = punishmentData;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!commandSender.hasPermission("core.admin")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 1)
            return true;

        switch (arguments[0].toLowerCase()) {
            case "add":
                if (punishmentData.containsWord(arguments[1])) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "That word is already in the automute");
                    return true;
                }

                punishmentData.addWord(arguments[1]);
                commandSender.sendMessage(corePlugin.getPrefix() + arguments[1] + " has been added to the automute");
                break;
            case "remove":
                if (!punishmentData.containsWord(arguments[1])) {
                    commandSender.sendMessage(corePlugin.getPrefix() + "That word is not in the automute");
                    return true;
                }

                punishmentData.removeWord(arguments[1]);
                commandSender.sendMessage(corePlugin.getPrefix() + arguments[1] + " has been removed from the automute");
                break;
            case "list":
                commandSender.sendMessage(corePlugin.getPrefix() + "Currently automuted words: ");
                commandSender.sendMessage(punishmentData.getAutomutedWords());
                break;
        }

        return true;
    }
}
