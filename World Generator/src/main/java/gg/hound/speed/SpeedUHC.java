package gg.hound.speed;

import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent;
import gg.hound.speed.command.WorldCommand;
import gg.hound.speed.world.BiomeSwap;
import gg.hound.speed.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedUHC extends JavaPlugin implements Listener {

    private long startTime;

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("showmethemoney").setExecutor(new WorldCommand());
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        WorldManager worldManager = new WorldManager(this);
        worldManager.removeWorld();
        new BiomeSwap();
        worldManager.createWorld();
    }

    @EventHandler
    public void onFillComplete(WorldBorderFillFinishedEvent worldBorderFillFinishedEvent) {
        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " seconds taken to generate chunks");
        worldBorderFillFinishedEvent.getWorld().save();
        System.out.println("Saving all UHC data.");

        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Closing server in order to zip.");
                Bukkit.getServer().shutdown();
            }
        }.runTaskLater(this, 20 * 10);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
