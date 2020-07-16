package gg.hound.arena.commands;

import gg.hound.arena.Arena;
import gg.hound.arena.match.MatchManager;
import gg.hound.arena.user.User;
import gg.hound.arena.user.UserManager;
import gg.hound.arena.user.UserState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecCommand implements CommandExecutor {

    private final Arena arena;
    private final UserManager userManager;

    public SpecCommand(Arena arena, UserManager userManager) {
        this.arena = arena;
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] arguments) {
        if (!(commandSender instanceof Player))
            return true;

        Player player = (Player) commandSender;

        User user = userManager.getUser(player.getUniqueId());
        if (user == null)
            return true;

        if (arguments.length < 1) {
            player.sendMessage(arena.getPrefix() + "Usage: /spec <name>");
            return true;
        }

        if (user.getUserState() != UserState.SPECTATOR) {
            player.sendMessage(arena.getPrefix() + "You must be in spectator mode to do that.");
            return true;
        }

        Player target = Bukkit.getPlayer(arguments[0]);
        if (target == null) {
            player.sendMessage(arena.getPrefix() + "That player is not online.");
            return true;
        }

        User targetUser = userManager.getUser(target.getUniqueId());
        if (targetUser == null) {
            player.sendMessage(arena.getPrefix() + "That player is not online.");
            return true;
        }

        if (targetUser.getUserState() != UserState.MATCH) {
            player.sendMessage(arena.getPrefix() + "That player is not in a match.");
            return true;
        }

        player.sendMessage(arena.getPrefix() + "Now spectating " + target.getName());
        player.teleport(target);


        return true;
    }
}
