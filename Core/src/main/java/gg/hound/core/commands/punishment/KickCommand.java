package gg.hound.core.commands.punishment;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.Punishment;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KickCommand implements CommandExecutor {

    private final UserManager userManager;
    private final CorePlugin corePlugin;
    private final PunishmentData punishmentData;


    private final Inventory kickInventory;

    public KickCommand(UserManager userManager, CorePlugin corePlugin, PunishmentData punishmentData) {
        this.punishmentData = punishmentData;
        this.userManager = userManager;
        this.corePlugin = corePlugin;

        kickInventory = Bukkit.createInventory(null, 54, ChatColor.AQUA + "Kick Player");
        setup();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        if (!commandSender.hasPermission("punishments.kick")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        if (arguments.length < 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Usage: /kick <user>");
            return true;
        }

        CoreUser coreUser = userManager.getUser(((Player) commandSender).getUniqueId());

        if (coreUser.getUserId() <= 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "An Internal Error has occurred! Reconnect, if this continues please contact a developer.");
            return true;
        }

        Player target = Bukkit.getPlayer(arguments[0]);

        if (target == null) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Error: This player cannot be found!");
            return true;
        }

        CoreUser targetUser = userManager.getUser(target.getUniqueId());

        if (coreUser.getHighest(CoreUser.PowerType.PUNISH).getPunishPower() <= targetUser.getHighest(CoreUser.PowerType.PUNISH).getPunishPower()) {
            commandSender.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "You do not have enough punishment power to execute this action.");
            return true;
        }

        Punishment kick = new Punishment(targetUser.getTempUser(), coreUser.getTempUser());

        punishmentData.addKick(((Player) commandSender).getUniqueId(), kick);
        ((Player) commandSender).openInventory(kickInventory);


        return true;
    }

    private void setup() {
        ItemStack blank = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setAmount(1).setDurability((short) 14).toItemStack();
        for (int i = 0; i < kickInventory.getSize() - 1; i++) {
            kickInventory.setItem(i, blank);
        }

        int start = 10;
        for (Reason reason : punishmentData.getKickReasons().values()) {
            if (start == 17 || start == 26 || start == 35 || start == 47) {
                start = start + 2;
            }

            kickInventory.setItem(start, reason.getItemStack());

            start++;
        }

        ItemStack cancel = new ItemBuilder(Material.REDSTONE_BLOCK).setName("Cancel").toItemStack();

        kickInventory.setItem(53, cancel);
    }
}
