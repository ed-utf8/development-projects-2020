package gg.hound.speed.world;

import gg.hound.speed.SpeedUHC;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class WorldManager {

    private final SpeedUHC speedUHC;

    public WorldManager(SpeedUHC speedUHC) {
        this.speedUHC = speedUHC;
    }

    public void createWorld() {
        World world = Bukkit.createWorld(new WorldCreator("uhc").environment(World.Environment.NORMAL).type(WorldType.NORMAL).generateStructures(true));
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setSpawnLocation(0, 125, 0);
        world.setPVP(false);
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setDifficulty(Difficulty.EASY);
        speedUHC.setStartTime(System.currentTimeMillis());
        loadChunks(world);
    }

    public void removeWorld() {
        File sourceFolder = new File(Bukkit.getWorldContainer(), "uhc");
        deleteFiles(sourceFolder);
        System.out.println("World deleted");
    }

    private void deleteFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    deleteFiles(file);
                else file.delete();
            }
        }
        path.delete();
    }

    private void loadChunks(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb shape square");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb uhc set 3 0 0");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb uhc fill 1000");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb fill confirm");
                System.out.println("Now generating " + world.getName());

            }
        }.runTaskLater(speedUHC, 20 * 6);
    }


}
