package gg.hound.speed.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("showmethemoney")) {
            if (commandSender instanceof Player)
                ((Player) commandSender).teleport(new Location(Bukkit.getWorld("uhc"), 0, Bukkit.getWorld("uhc").getHighestBlockYAt(0, 0), 0));
            return true;
        }
        return false;
    }
}
