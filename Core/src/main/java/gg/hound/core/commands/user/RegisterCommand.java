package gg.hound.core.commands.user;

import gg.hound.core.CorePlugin;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterCommand implements CommandExecutor {

    private final CorePlugin corePlugin;
    private final UserManager userManager;
    private final SQLManager sqlManager;

    public RegisterCommand(CorePlugin corePlugin, UserManager userManager, SQLManager sqlManager) {
        this.corePlugin = corePlugin;
        this.userManager = userManager;
        this.sqlManager = sqlManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arguments) {
        if (command.getName().equalsIgnoreCase("webregister")) {
            if (!(commandSender instanceof Player))
                return true;

            Player player = (Player) commandSender;

            if (arguments.length != 1) {
                player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Usage: /register <email>");
                return true;
            }

            if (!isValidEmailAddress(arguments[0])) {
                player.sendMessage(corePlugin.getPrefix() + ChatColor.RED + "Invalid email address");
                return true;
            }

            CoreUser coreUser = userManager.getUser(player.getUniqueId());
            if (coreUser == null) return true;

            if (coreUser.hasRegistered()) {
                //USE LINK HERE
                player.sendMessage(corePlugin.getPrefix() + ChatColor.GREEN + "You have already registered");
            }


        }
        return false;
    }

    private boolean isValidEmailAddress(String email) {
        String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
