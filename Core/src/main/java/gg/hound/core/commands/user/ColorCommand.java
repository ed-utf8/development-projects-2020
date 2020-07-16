package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.util.Colour;
import gg.hound.core.util.ItemBuilder;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class ColorCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final PluginUtils pluginUtils;

    public ColorCommand(CorePlugin corePlugin, PluginUtils pluginUtils) {
        this.corePlugin = corePlugin;
        this.pluginUtils = pluginUtils;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(corePlugin.getPrefix() + "You must be a player to use that command");
            return true;
        }

        if (!commandSender.hasPermission("core.colour")) {
            commandSender.sendMessage(corePlugin.getNoPerms());
            return true;
        }

        Player player = (Player) commandSender;
        player.openInventory(colourInventory());
        pluginUtils.addColour(player.getUniqueId());
        return true;
    }

    private Inventory colourInventory() {
        List<Colour> enabledColors = new ArrayList<>();

        for (Colour colourEntry : pluginUtils.getColourMap().values()) {
            if (colourEntry.isShowInInventory())
                enabledColors.add(colourEntry);
        }

        Inventory inventory = Bukkit.createInventory(null, pluginUtils.calculateInventorySize(enabledColors.size()), ChatColor.GREEN + "Colour");

        for (Colour colour : enabledColors) {
            inventory.addItem(new ItemBuilder(Material.PAPER).setName(colour.colourize() + "Click to choose this colour.").addItemFlag(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, colour.getId()).toItemStack());
        }

        return inventory;
    }


}

