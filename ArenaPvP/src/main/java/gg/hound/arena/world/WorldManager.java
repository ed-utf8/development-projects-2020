package gg.hound.arena.world;

import gg.hound.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldManager {

    private final Arena arena;

    public WorldManager(Arena arena) {
        this.arena = arena;
        createWorld();
    }

    private void createWorld() {

        File sourceFolder = new File(Bukkit.getWorldContainer(), "template");

        if (!sourceFolder.exists()) {
            Bukkit.createWorld(new WorldCreator("template").generator(new FlatWorldGenerator()).generateStructures(false));

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.shutdown();
                }
            }.runTaskLater(arena, 20 * 10);
            return;
        }

        File targetFolder = new File(Bukkit.getWorldContainer(), "practice");
        deleteWorld(targetFolder);
        copyWorld(sourceFolder, targetFolder);
        removeEntities(Bukkit.createWorld(new WorldCreator("practice").generator(new FlatWorldGenerator())));
        World practice = Bukkit.getWorld("practice");
        practice.setDifficulty(Difficulty.NORMAL);
        practice.setGameRuleValue("doMobSpawning", "false");

    }

    private void deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    private void copyWorld(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
        }
    }

    private void removeEntities(World world) {
        for (Entity e : world.getEntities()) {
            if (e.getType() != EntityType.PLAYER) {
                e.remove();
            }
        }
    }
}
