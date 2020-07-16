package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordLinkCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;
    private final UserManager userManager;

    public DiscordLinkCommand(CorePlugin corePlugin, SQLManager sqlManager, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to use that command");
            return true;
        }

        if (arguments.length != 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Join https://hound.gg/discord and use command !link to get a code!");
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        if (sqlManager.isDiscordLinked(coreUser)) {
            player.sendMessage(corePlugin.getPrefix() + "Your discord is already linked");
            return true;
        }

        if (!sqlManager.isCodeValid(arguments[0])) {
            player.sendMessage(corePlugin.getPrefix() + "Invalid code");
            return true;
        }

        sqlManager.updateDiscord(coreUser, arguments[0]);
        player.sendMessage(corePlugin.getPrefix() + "Your Minecraft Account is now linked, use !reloadperms to get your ranks on the discord server.");

        return true;
    }
}
