package gg.hound.core.commands.punishments;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.TempInfoStoreUser;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AltsCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final SQLManager sqlManager;

    public AltsCommand(CorePlugin corePlugin, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;
        if (!player.hasPermission("punishments.ips")) {
            player.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length != 1) {
            player.sendMessage(corePlugin.getPrefix() + "Usage: /ips <player>");
            return true;
        }

        TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);
        if (tempInfoStoreUser == null) {
            player.sendMessage(corePlugin.getPrefix() + "That player has never joined the network.");
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<String> names = sqlManager.getAlts(tempInfoStoreUser.getIpId());
        for (int i = 0; i < (Math.min(names.size(), 10)); i++) {
            stringBuilder.append("\n");
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append("\u2022 ");
            stringBuilder.append(ChatColor.AQUA);
            stringBuilder.append(names.get(i));
            if (i == 9)
                stringBuilder.append("\n").append(ChatColor.AQUA).append("Plus ").append((names.size() - 1) - i).append(" more...");
        }
        player.sendMessage(corePlugin.getPrefix() + arguments[0] + " has " + names.size() + (names.size() != 1 ? " alts" : " alt"));
        player.sendMessage(stringBuilder.toString());

        return true;
    }
}
