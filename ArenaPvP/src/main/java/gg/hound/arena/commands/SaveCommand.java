package gg.hound.arena.commands;

import gg.hound.arena.Arena;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.sql.SQLManager;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand implements CommandExecutor {

    private final Arena arena;
    private final UserManager userManager;
    private final SQLManager sqlManager;
    private final KitManager kitManager;
    private final InventoryUtil inventoryUtil;

    public SaveCommand(Arena arena, UserManager userManager, SQLManager sqlManager, KitManager kitManager, InventoryUtil inventoryUtil) {
        this.arena = arena;
        this.userManager = userManager;
        this.sqlManager = sqlManager;
        this.kitManager = kitManager;
        this.inventoryUtil = inventoryUtil;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] arguments) {
        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;

        if (arguments.length == 1) {
            player.teleport(new Location(Bukkit.getWorld("practice"), 0, 100, 0));
            player.setAllowFlight(true);
            player.setFlying(true);
            return true;
        }
        if (!kitManager.isEditingKit(player.getUniqueId()))
            return true;

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return true;

        sqlManager.saveKit(user, kitManager.getKit(player.getUniqueId()), kitManager.serialiseInventory(player.getInventory().getContents()));

        kitManager.removeKitEditor(player);

        inventoryUtil.giveSpawnInventory(player);

        user.setInventoryLayout(kitManager.getKit(player.getUniqueId()), player.getInventory().getContents());

        player.sendMessage(arena.getPrefix() + "Successfully saved your layout.");
        return true;
    }
}
