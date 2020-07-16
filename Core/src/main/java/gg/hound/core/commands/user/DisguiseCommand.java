package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.disguise.DisguiseManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class DisguiseCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final DisguiseManager disguiseManager;
    private final SQLManager sqlManager;

    public DisguiseCommand(CorePlugin corePlugin, UserManager userManager, DisguiseManager disguiseManager, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.disguiseManager = disguiseManager;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        if (!commandSender.hasPermission("core.disguise")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;


        switch (command.getName().toLowerCase()) {
            case "disguise": {
                if (!corePlugin.getServerName().toLowerCase().contains("lobby-")) {
                    player.sendMessage(corePlugin.getPrefix() + "You can only disguise in the HUB/LOBBY.");
                    return true;
                }

                if (coreUser.isDisguised()) {
                    player.sendMessage(corePlugin.getPrefix() + "Please use /undisguise before disguising again.");
                    return true;
                }

                if (arguments.length == 1 && !player.isOp() && commandSender.hasPermission("core.developer")) {
                    if (arguments[0].length() <= 16)
                        if (disguiseManager.canUseDisguiseName(arguments[0]))
                            disguiseManager.disguisePlayer(player, arguments[0]);
                        else
                            commandSender.sendMessage("Sorry name already in use!");
                    else
                        commandSender.sendMessage("Sorry dev name to long!");
                } else {
                    disguiseManager.disguisePlayer(player);
                }
                break;
            }
            case "undisguise": {

                if (!corePlugin.getServerName().toLowerCase().contains("lobby-")) {
                    player.sendMessage(corePlugin.getPrefix() + "You can only disguise in the HUB/LOBBY.");
                    return true;
                }

                if (!coreUser.isDisguised()) {
                    player.sendMessage(corePlugin.getPrefix() + "§4Error: §cYou are not disguised. §fType /disguise to disguise.");
                    return true;
                }

                disguiseManager.unDisguisePlayer(player);
                break;
            }
            case "addskin": {
                if (!player.hasPermission("core.admin")) {
                    player.sendMessage(corePlugin.getNoPerms());
                    return true;
                }

                if (arguments.length != 1) {
                    player.sendMessage(corePlugin.getPrefix() + "Usage: /addskin <uuid>");
                    return true;
                }

                if (arguments[0].length() != 36) {
                    player.sendMessage(corePlugin.getPrefix() + "Invalid UUID");
                    return true;
                }

                disguiseManager.createSkin(arguments[0]);
                player.sendMessage(corePlugin.getPrefix() + "Creating a skin, this will be available on restart!");
                break;
            }
            case "disguisecheck": {
                if (!player.hasPermission("core.staff")) {
                    player.sendMessage(corePlugin.getNoPerms());
                    return true;
                }

                if (arguments.length != 1) {
                    player.sendMessage(corePlugin.getPrefix() + "Usage: /disguisecheck <name>");
                    return true;
                }

                TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(arguments[0]);
                if (tempInfoStoreUser == null) {
                    player.sendMessage(corePlugin.getPrefix() + "That player could not be found in our database");
                    return true;
                }

                if (tempInfoStoreUser.isDisguise())
                    player.sendMessage(corePlugin.getPrefix() + "This is the disguise of: " + tempInfoStoreUser.getName());
                else
                    player.sendMessage(corePlugin.getPrefix() + "This player is not disguised.");

                break;
            }
            case "disguiselist": {
                if (!player.hasPermission("core.staff")) {
                    player.sendMessage(corePlugin.getNoPerms());
                    return true;
                }

                Map<String, String> disguiseList = sqlManager.disguiseList();

                if (disguiseList == null) {
                    commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "No disguised players found!");
                    return true;
                }

                commandSender.sendMessage(ChatColor.RED + "Listed Disguises. [Max 30]");
                for (Map.Entry<String, String> stringStringEntry : disguiseList.entrySet()) {
                    commandSender.sendMessage(ChatColor.GRAY + stringStringEntry.getKey() + ChatColor.RED + " \u00bb " + ChatColor.GREEN + stringStringEntry.getValue());
                }

                break;
            }
        }

        return true;
    }

}
