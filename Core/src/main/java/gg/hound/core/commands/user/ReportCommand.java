package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.tasks.PlayerCooldownTask;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.ItemBuilder;
import gg.hound.core.util.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ReportCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final PlayerCooldownTask playerCooldownTask;
    private final Inventory inventory;
    private final PunishmentData punishmentData;

    public ReportCommand(CorePlugin corePlugin, UserManager userManager, PlayerCooldownTask playerCooldownTask, PunishmentData punishmentData) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.playerCooldownTask = playerCooldownTask;
        this.punishmentData = punishmentData;

        inventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Report");
        setup();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to execute this command");
            return true;
        }

        if (arguments.length != 1) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Usage: /report <user>");
            return true;
        }

        Player player = (Player) commandSender;

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        if (coreUser == null)
            return true;

        if (playerCooldownTask.hasReportCooldown(coreUser)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "Please wait before attempting to report a player again");
            return true;
        }

        Player target = Bukkit.getPlayer(arguments[0]);
        if (target == null) {
            player.sendMessage(corePlugin.getPrefix() + "This player is not online");
            return true;
        }

        player.openInventory(inventory);

        if (!player.hasPermission("perms.staff"))
            playerCooldownTask.addReportCooldown(coreUser);

        punishmentData.addReport(player.getUniqueId(), new Report(arguments[0], player.getName()));
        return true;
    }


    private void setup() {
        ItemStack blank = new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setAmount(1).setDurability((short) 14).toItemStack();
        for (int i = 0; i < inventory.getSize() - 1; i++) {
            inventory.setItem(i, blank);
        }

        int start = 10;
        for (Reason reason : punishmentData.getReportReasons().values()) {
            if (start == 17 || start == 26 || start == 38 || start == 47) {
                start = start + 2;
            }

            inventory.setItem(start, reason.getItemStack());

            start++;
        }

        ItemStack cancel = new ItemBuilder(Material.REDSTONE_BLOCK).setName("Cancel").toItemStack();

        inventory.setItem(53, cancel);
    }

}
