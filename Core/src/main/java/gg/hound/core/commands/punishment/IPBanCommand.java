package gg.hound.core.commands.punishment;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.IPBan;
import gg.hound.core.punishments.Punishment;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.TempInfoStoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.ItemBuilder;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class IPBanCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final PunishmentData punishmentData;
    private final SQLManager sqlManager;
    private final UserManager userManager;
    private final PluginUtils pluginUtils;

    private final Inventory ipBanInventory;

    public IPBanCommand(CorePlugin corePlugin, PunishmentData punishmentData, SQLManager sqlManager, UserManager userManager, PluginUtils pluginUtils) {
        this.corePlugin = corePlugin;
        this.punishmentData = punishmentData;
        this.sqlManager = sqlManager;
        this.userManager = userManager;
        this.pluginUtils = pluginUtils;

        ipBanInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA + "IPBan Player");
        setup();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (!commandSender.hasPermission("punishments.banip")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /banip <user>");
            return true;
        }

        Player player = (Player) commandSender;
        CoreUser coreUser = userManager.getUser(player);

        if (coreUser.getUserId() <= 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "An Internal Error has occurred! Reconnect, if this continues please contact a developer.");
            return true;
        }

        TempInfoStoreUser tempInfoStoreUser = sqlManager.getUser(strings[0]);

        if (tempInfoStoreUser == null) {
            player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Error: This player cannot be found!");
            return true;
        }

        if (coreUser.getHighest(CoreUser.PowerType.PUNISH).getPunishPower() <= sqlManager.getHighestRankPower(tempInfoStoreUser, CoreUser.PowerType.PUNISH)) {
            player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "You do not have enough punishment power to execute this action.");
            return true;
        }

        Punishment ipban = new Punishment(tempInfoStoreUser, coreUser.getTempUser());

        if (strings.length == 2) {
            if (commandSender.hasPermission("punishments.banip.other")) {
                int time = pluginUtils.getTime(strings[1]);

                if (time >= 0) {
                    ipban.setTime(time);
                    ipban.setCustomTime(true);
                } else {
                    player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Invalid Time!");
                    return true;
                }
            }
        }

        punishmentData.addIPBan(player.getUniqueId(), ipban);
        player.openInventory(ipBanInventory);

        return true;
    }

    private void setup() {
        ItemStack blank = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setAmount(1).setDurability((short) 14).toItemStack();
        for (int i = 0; i < ipBanInventory.getSize() - 1; i++)
            ipBanInventory.setItem(i, blank);

        int start = 10;
        for (Reason reason : punishmentData.getIPBanReasons().values()) {
            if (start == 17 || start == 26 || start == 35 || start == 47)
                start += 2;

            ipBanInventory.setItem(start, reason.getItemStack());

            start++;
        }

        ItemStack cancel = new ItemBuilder(Material.REDSTONE_BLOCK).setName("Cancel").toItemStack();

        ipBanInventory.setItem(53, cancel);
    }

}
