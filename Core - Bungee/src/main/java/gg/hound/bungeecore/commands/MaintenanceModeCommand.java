package gg.hound.bungeecore.commands;

import gg.hound.bungeecore.BungeeCorePlugin;
import gg.hound.bungeecore.maintainence.MaintenanceMode;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class MaintenanceModeCommand extends Command {

    private final BungeeCorePlugin bungeeCorePlugin;
    private final MaintenanceMode maintenanceMode;

    public MaintenanceModeCommand(BungeeCorePlugin bungeeCorePlugin, MaintenanceMode maintenanceMode) {
        super("maintenancemode", "perms.developer", "mm", "maintenance");
        this.bungeeCorePlugin = bungeeCorePlugin;
        this.maintenanceMode = maintenanceMode;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if (!commandSender.hasPermission("perms.developer")) {
            commandSender.sendMessage(new ComponentBuilder(bungeeCorePlugin.getStaffPrefix() + "§cYou do not have permission to run this command.").create());
            return;
        }

        if (strings.length < 1) {
            if (maintenanceMode.isMaintenanceMode()) {
                maintenanceMode.setMaintenanceMode(false);
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§4§lMaintainence Mode §8\u00bb §cMaintainence Mode has been deactivated.").create());

            } else {
                maintenanceMode.setMaintenanceMode(true);
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§4§lMaintainence Mode §8\u00bb §cMaintainence Mode has been activated.").create());
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (!proxiedPlayer.hasPermission("perms.admin"))
                        proxiedPlayer.disconnect(new ComponentBuilder("§4§lMaintainence Mode §8\u00bb §cMaintainence Mode has been activated.").create());
                }
            }
            return;
        }

        if (strings[0].equalsIgnoreCase("staff")) {
            if (maintenanceMode.isAdminOnly()) {
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§4§lMaintainence Mode §8\u00bb §cMaintainence Mode §a(Staff Join Only)").create());
                maintenanceMode.setAdminOnly(false);
            } else {
                ProxyServer.getInstance().broadcast(new ComponentBuilder("§4§lMaintainence Mode §8\u00bb §cMaintainence Mode §a(Admin Join Only)").create());
                maintenanceMode.setAdminOnly(true);
            }
        }

        if (strings[0].equalsIgnoreCase("whitelist") && strings.length == 2 && strings[1].length() == 36) {
            maintenanceMode.getWhitelist().add(UUID.fromString(strings[1]));
            commandSender.sendMessage("Whitelisted UUID!");
        }

    }
}
