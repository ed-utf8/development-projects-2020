package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MessageReplyCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final HashMap<Player, Player> playerMap;
    private final PluginUtils pluginUtils;

    public MessageReplyCommand(CorePlugin corePlugin, UserManager userManager, PluginUtils pluginUtils) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.pluginUtils = pluginUtils;

        this.playerMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arguments) {
        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;
        CoreUser user = userManager.getUser(player.getUniqueId());

        if (user == null)
            return true;

        if (user.isMuted()) {
            if (user.getUnMuteTime() > pluginUtils.currentTime() && user.getUnMuteTime() != 0) {
                commandSender.sendMessage(corePlugin.getPrefix() + "You are currently muted.");
                return true;
            } else
                user.setMuted(false);
        }

        if (!user.isPrivateMessages()) {
            player.sendMessage(corePlugin.getPrefix() + "Please enable private messages to send messages.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("message")) {
            if (arguments.length < 2) {
                player.sendMessage(corePlugin.getPrefix() + "Usage: /message <name> <message>");
                return true;
            }

            Player target = Bukkit.getPlayer(arguments[0]);
            if (target == null) {
                player.sendMessage(corePlugin.getPrefix() + arguments[0] + " is not on the server.");
                return true;
            }

            CoreUser targetUser = userManager.getUser(target.getUniqueId());
            if (targetUser == null)
                return true;

            if (!targetUser.isPrivateMessages() && !player.hasPermission("core.staff")) {
                player.sendMessage(corePlugin.getPrefix() + "This player has their private messages disabled.");
                return true;
            }

            playerMap.put(player, target);
            playerMap.put(target, player);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < arguments.length; i++)
                stringBuilder.append(arguments[i]).append(" ");

            target.sendMessage("§7(From " + user.getPrefix() + player.getName() + "§7) §b" + stringBuilder.toString());
            player.sendMessage("§7(To " + targetUser.getPrefix() + target.getName() + "§7) §b" + stringBuilder.toString());

            return true;
        }

        if (command.getName().equalsIgnoreCase("reply")) {
            if (arguments.length < 1) {
                player.sendMessage(corePlugin.getPrefix() + "Usage: /reply <message>");
                return true;
            }

            Player targetPlayer = playerMap.get(player);
            if (targetPlayer == null) {
                player.sendMessage(corePlugin.getPrefix() + "That player is no longer online.");
                return true;
            }

            if (!targetPlayer.isOnline()) {
                player.sendMessage(corePlugin.getPrefix() + "That player is not online.");
                return true;
            }

            CoreUser targetUser = userManager.getUser(targetPlayer.getUniqueId());
            if (targetUser == null)
                return true;


            if (!targetUser.isPrivateMessages() && !player.hasPermission("core.staff")) {
                player.sendMessage(corePlugin.getPrefix() + "This player has their private messages disabled.");
                return true;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String argument : arguments) stringBuilder.append(argument).append(" ");

            playerMap.put(player, targetPlayer);
            playerMap.put(targetPlayer, player);

            targetPlayer.sendMessage("§7(From " + user.getPrefix() + player.getName() + "§7) §b" + stringBuilder.toString());
            player.sendMessage("§7(To " + targetUser.getPrefix() + targetPlayer.getName() + "§7) §b" + stringBuilder.toString());

            return true;
        }
        return false;
    }
}
