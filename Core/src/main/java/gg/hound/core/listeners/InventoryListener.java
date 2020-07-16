package gg.hound.core.listeners;

import gg.hound.core.CorePlugin;
import gg.hound.core.punishments.Punishment;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.punishments.Reason;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.Colour;
import gg.hound.core.util.PluginUtils;
import gg.hound.core.util.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    private final CorePlugin corePlugin;
    private final PunishmentData punishmentData;
    private final PluginUtils pluginUtils;
    private final SQLManager sqlManager;
    private final RedisManager redisManager;
    private final UserManager userManager;

    public InventoryListener(CorePlugin corePlugin, PunishmentData punishmentData, PluginUtils pluginUtils, SQLManager sqlManager, RedisManager redisManager, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.punishmentData = punishmentData;
        this.pluginUtils = pluginUtils;
        this.sqlManager = sqlManager;
        this.redisManager = redisManager;
        this.userManager = userManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getCurrentItem() == null) return;

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.AQUA + "IPBan Player")) {
            inventoryClickEvent.setCancelled(true);

            if (!inventoryClickEvent.getCurrentItem().hasItemMeta())
                return;

            if (inventoryClickEvent.getCurrentItem().getData().getItemType().equals(Material.STAINED_GLASS_PANE))
                return;

            Player player = (Player) inventoryClickEvent.getWhoClicked();
            ItemStack currentItem = inventoryClickEvent.getCurrentItem();
            String clickedReason = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());

            if (clickedReason.equalsIgnoreCase("Cancel")) {
                punishmentData.removeKick(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "IPBan Cancelled");
                player.closeInventory();
                return;
            }

            Punishment ipBan = punishmentData.getIPBan(player.getUniqueId());
            Reason reason = punishmentData.getIPBanReason(clickedReason);
            ipBan.setReason(reason);

            if (reason.isTextInputRequired()) {
                if (player.hasPermission("punishments.banip.other")) {
                    punishmentData.addOtherIPBan(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Please enter the reason for the ip-ban in chat.");
                    player.closeInventory();
                } else
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this. Please contact an admin.");
                return;
            }

            if (sqlManager.ipbanPlayer(ipBan))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact an admin.");
            else {
                Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + ipBan.getTarget().getName() + ChatColor.RED + " has been ip-banned for " + ChatColor.YELLOW + clickedReason);
                if (Bukkit.getPlayer(ipBan.getTarget().getUuid()) != null)
                    Bukkit.getPlayer(ipBan.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been ip-banned for " + ChatColor.WHITE + clickedReason);
                else
                    redisManager.sendBan(ipBan.getTarget().getUuid(), clickedReason);

                punishmentData.removeBan(player.getUniqueId());
                player.closeInventory();
                return;
            }
            return;
        }

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.AQUA + "Ban Player")) {
            inventoryClickEvent.setCancelled(true);

            if (!inventoryClickEvent.getCurrentItem().hasItemMeta())
                return;

            if (inventoryClickEvent.getCurrentItem().getData().getItemType().equals(Material.STAINED_GLASS_PANE))
                return;

            Player player = (Player) inventoryClickEvent.getWhoClicked();
            ItemStack currentItem = inventoryClickEvent.getCurrentItem();
            String clickedReason = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());

            if (clickedReason.equalsIgnoreCase("Cancel")) {
                punishmentData.removeBan(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Ban Cancelled");
                player.closeInventory();
                return;
            }

            Punishment ban = punishmentData.getBan(player.getUniqueId());
            Reason reason = punishmentData.getBanReason(clickedReason);

            int previousViolations = sqlManager.getBanViolations(ban.getTarget().getId(), reason);
            ban.setReason(reason);

            if (ban.getTime() == 0)
                ban.setTime(reason.getLength(previousViolations));

            if (reason.isTextInputRequired()) {
                if (player.hasPermission("punishments.ban.other")) {
                    punishmentData.addOtherBan(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Please enter the reason for the ban in chat.");
                    player.closeInventory();
                } else
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this. Please contact an admin.");
                return;
            }

            if (sqlManager.banPlayer(ban))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact an admin.");
            else {
                Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + ban.getTarget().getName() + ChatColor.RED + " has been banned for " + ChatColor.YELLOW + clickedReason);
                if (Bukkit.getPlayer(ban.getTarget().getUuid()) != null)
                    Bukkit.getPlayer(ban.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been banned for " + ChatColor.WHITE + clickedReason);
                else
                    redisManager.sendBan(ban.getTarget().getUuid(), clickedReason);

                punishmentData.removeBan(player.getUniqueId());
                player.closeInventory();
                return;
            }
        }

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.AQUA + "Mute Player")) {
            inventoryClickEvent.setCancelled(true);

            if (!inventoryClickEvent.getCurrentItem().hasItemMeta())
                return;

            if (inventoryClickEvent.getCurrentItem().getData().getItemType().equals(Material.STAINED_GLASS_PANE))
                return;

            Player player = (Player) inventoryClickEvent.getWhoClicked();
            ItemStack currentItem = inventoryClickEvent.getCurrentItem();
            String clickedReason = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());

            if (clickedReason.equalsIgnoreCase("Cancel")) {
                punishmentData.removeMute(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Mute Cancelled");
                player.closeInventory();
                return;
            }

            Punishment mute = punishmentData.getMute(player.getUniqueId());
            Reason reason = punishmentData.getMuteReason(clickedReason);

            int previousViolations = sqlManager.getMuteViolations(mute.getTarget().getId(), reason);
            mute.setReason(reason);
            if (mute.getTime() == 0)
                mute.setTime(reason.getLength(previousViolations));

            if (reason.isTextInputRequired()) {
                if (player.hasPermission("punishments.mute.other")) {
                    punishmentData.addOtherMute(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Please enter the reason for the mute in chat.");
                    player.closeInventory();
                } else
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this. Please contact an admin.");
                return;
            }

            if (sqlManager.mutePlayer(mute))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact an admin.");
            else {
                Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW +  mute.getTarget().getName() + ChatColor.RED + " has been muted for " + ChatColor.YELLOW + clickedReason);
                if (Bukkit.getPlayer(mute.getTarget().getName()) != null) {
                    CoreUser muteTarget = userManager.getUser(Bukkit.getPlayer(mute.getTarget().getName()).getUniqueId());
                    if (muteTarget != null) {
                        muteTarget.setMuted(true);
                        muteTarget.setUnMuteTime(pluginUtils.getUnmuteTime(mute.getTime()));
                    }
                } else
                    redisManager.sendMute(mute.getTarget().getUuid(), clickedReason, pluginUtils.getUnmuteTime(mute.getTime()));
            }
            punishmentData.removeMute(player.getUniqueId());
            player.closeInventory();
            return;
        }

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.AQUA + "Kick Player")) {
            inventoryClickEvent.setCancelled(true);

            if (!inventoryClickEvent.getCurrentItem().hasItemMeta())
                return;

            if (inventoryClickEvent.getCurrentItem().getData().getItemType().equals(Material.STAINED_GLASS_PANE))
                return;

            Player player = (Player) inventoryClickEvent.getWhoClicked();
            ItemStack currentItem = inventoryClickEvent.getCurrentItem();
            String clickedReason = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());

            if (clickedReason.equalsIgnoreCase("Cancel")) {
                punishmentData.removeKick(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Kick Cancelled");
                player.closeInventory();
                return;
            }

            Punishment kick = punishmentData.getKick(player.getUniqueId());
            Reason reason = punishmentData.getKickReason(clickedReason);
            kick.setReason(reason);

            if (reason.isTextInputRequired()) {
                if (player.hasPermission("punishments.kick.other")) {
                    punishmentData.addOtherKick(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Please enter the reason for the kick in chat.");
                    player.closeInventory();
                } else
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this. Please contact an admin.");
                return;
            }

            if (sqlManager.kickPlayer(kick))
                player.sendMessage(ChatColor.RED + "An error has occurred, please contact an admin.");
            else {
                if (Bukkit.getPlayer(kick.getTarget().getName()) != null) {
                    Bukkit.broadcastMessage(corePlugin.getPrefix() + ChatColor.YELLOW + kick.getTarget().getName() + " has been kicked for " + ChatColor.YELLOW + clickedReason);
                    if (Bukkit.getPlayer(kick.getTarget().getUuid()) != null) {
                        Bukkit.getPlayer(kick.getTarget().getUuid()).kickPlayer(ChatColor.RED + "You have been kicked for: " + ChatColor.WHITE + clickedReason);
                    }
                }
            }
            punishmentData.removeKick(player.getUniqueId());
            player.closeInventory();
            return;
        }

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.GREEN + "Report")) {
            if (!inventoryClickEvent.getCurrentItem().hasItemMeta()) return;
            String currentItem = inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().replace("§r§c", "");
            inventoryClickEvent.setCancelled(true);
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            if (currentItem.equalsIgnoreCase("other")) {
                punishmentData.addOtherReport(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Please enter the reason for the report in chat.");
            } else if (currentItem.equalsIgnoreCase("cancel")) {
                punishmentData.removeReport(player.getUniqueId());
                player.sendMessage(ChatColor.RED + "Report Cancelled");
            } else if (!inventoryClickEvent.getCurrentItem().getData().getItemType().equals(Material.STAINED_GLASS_PANE)) {
                Report report = punishmentData.getReport(player.getUniqueId());
                report.setReason(currentItem);

                redisManager.sendReport(report.getReporter(), report.getTarget(), currentItem);

                player.sendMessage(ChatColor.GREEN + "Report submitted");

                punishmentData.removeReport(player.getUniqueId());
            }
            player.closeInventory();
            return;
        }

        if (inventoryClickEvent.getInventory().getName().equals(ChatColor.GREEN + "Colour") && inventoryClickEvent.getCurrentItem() != null && inventoryClickEvent.getCurrentItem().getItemMeta() != null && inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName() != null) {
            inventoryClickEvent.setCancelled(true);
            if (pluginUtils.getColour().contains(inventoryClickEvent.getWhoClicked().getUniqueId())) {
                Player player = (Player) inventoryClickEvent.getWhoClicked();
                player.closeInventory();
                final int colourId = inventoryClickEvent.getCurrentItem().getEnchantmentLevel(Enchantment.DURABILITY);
                Colour colour = pluginUtils.getColour(colourId);
                player.sendMessage(ChatColor.GREEN + "Name color has been updated!");
                CoreUser coreUser = userManager.getUser(player.getUniqueId());
                coreUser.setColour(colour);
                sqlManager.changeUserColor(coreUser, colourId);
                coreUser.updatePrefix();

                pluginUtils.removeColour(player.getUniqueId());
            }
        }
    }

}
