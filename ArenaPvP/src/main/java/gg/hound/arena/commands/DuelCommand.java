package gg.hound.arena.commands;

import gg.hound.arena.Arena;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.match.duel.Duel;
import gg.hound.arena.match.duel.DuelManager;
import gg.hound.arena.match.kit.KitManager;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import gg.hound.arena.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelCommand implements CommandExecutor {

    private final Arena arena;
    private final UserManager userManager;
    private final KitManager kitManager;
    private final DuelManager duelManager;
    private final MatchManager matchManager;
    private final InventoryUtil inventoryUtil;

    public DuelCommand(Arena arena, UserManager userManager, KitManager kitManager, DuelManager duelManager, MatchManager matchManager, InventoryUtil inventoryUtil) {
        this.arena = arena;
        this.userManager = userManager;
        this.kitManager = kitManager;
        this.duelManager = duelManager;
        this.matchManager = matchManager;
        this.inventoryUtil = inventoryUtil;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] arguments) {
        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;

        if (!arena.areMatchesEnabled()) {
            player.sendMessage(arena.getPrefix() + "Duels are currently disabled");
            return true;
        }

        if (arguments.length != 1) {
            player.sendMessage(arena.getPrefix() + "Usage: /duel <name>");
            return true;
        }

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return true;

        if (user.getUserState() != UserState.LOBBY) {
            player.sendMessage(arena.getPrefix() + "You cannot use duels right now.");
            return true;
        }

        if (!user.isDuelRequests()) {
            player.sendMessage(arena.getPrefix() + "Please enable your duel requests in order to duel other players.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("duel")) {
            Player target = Bukkit.getPlayer(arguments[0]);
            if (target == null) {
                player.sendMessage(arena.getPrefix() + arguments[0] + " is not online.");
                return true;
            }

            User targetUser = userManager.getUser(player.getUniqueId());
            if (targetUser == null) {
                player.sendMessage(arena.getPrefix() + "An error has occurred, please contact a developer.");
                return true;
            }

            if (targetUser.getUserState() != UserState.LOBBY) {
                player.sendMessage(arena.getPrefix() + arguments[0] + " cannot currently accept duel requests.");
                return true;
            }

            if (!targetUser.isDuelRequests()) {
                player.sendMessage(arena.getPrefix() + arguments[0] + " is not currently accepting duel requests.");
                return true;
            }

            Duel duel = new Duel(player.getUniqueId(), target.getUniqueId(), kitManager);
            duelManager.addDuelCreator(player.getUniqueId(), duel);
            player.openInventory(inventoryUtil.duelCreationInventory(duel));

        } else if (command.getName().equalsIgnoreCase("accept")) {
            Player targetPlayer = Bukkit.getPlayer(UUID.fromString(arguments[0]));
            if (targetPlayer == null) {
                player.sendMessage(arena.getPrefix() + "That player is no longer online.");
                return true;
            }

            User targetUser = userManager.getUser(targetPlayer.getUniqueId());
            if (targetUser == null) {
                player.sendMessage(arena.getPrefix() + "An error has occurred.");
                return true;
            }

            if (targetUser.getUserState() != UserState.LOBBY) {
                player.sendMessage(arena.getPrefix() + "That player is no longer able to accept duel requests.");
                return true;
            }

            for (Duel duel : user.getDuels()) {
                if (duel.getSender() == targetPlayer.getUniqueId()) {

                }
            }
        }
        return true;
    }
}
