package gg.hound.arena.arenas;

import gg.hound.arena.Arena;
import gg.hound.arena.tasks.ArenaCreationTask;
import gg.hound.arena.arenas.block.BlockInfo;
import gg.hound.arena.sql.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MapManager {

    private final Arena arena;
    private final SQLManager sqlManager;
    private final ArenaCreationTask arenaCreationTask;

    private List<ArenaMap> soloMaps;
    private List<ArenaMap> partyMaps;

    private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public MapManager(Arena arena, SQLManager sqlManager, ArenaCreationTask arenaCreationTask) {
        this.arena = arena;
        this.sqlManager = sqlManager;
        this.arenaCreationTask = arenaCreationTask;

        loadMaps();
    }

    public void loadMaps() {
        soloMaps = sqlManager.loadMaps();

        if (soloMaps != null)
            createMaps();
        else {
            System.out.println("No maps found.");
            Bukkit.getServer().shutdown();
        }

    }

    private void createMaps() {
        for (ArenaMap arenaMap : soloMaps) {
            arenaMap.setBlockInfos(getBlocks(arenaMap.getCornerOne(), arenaMap.getCornerTwo()));
            System.out.println("Arena ID: " + arenaMap.getId() + " Blocks: " + arenaMap.getBlockInfos().size());
            for (int i = 1; i <= 5; i++)
                createNewMap(arenaMap, i);

        }
    }

    private void createNewMap(ArenaMap arenaMap, int multiplier) {
        arenaMap.incrementCurrentMultiplier();

        Location cornerOne = arenaMap.getCornerOne().clone();
        Location cornerTwo = arenaMap.getCornerTwo().clone();
        Location spawnOne = arenaMap.getSpawnOne().clone();
        Location spawnTwo = arenaMap.getSpawnTwo().clone();

        cornerOne.setX(cornerOne.getX() + (250 * multiplier));
        cornerTwo.setX(cornerTwo.getX() + (250 * multiplier));

        spawnOne.setX(spawnOne.getX() + (250 * multiplier));
        spawnTwo.setX(spawnTwo.getX() + (250 * multiplier));

        BaseArena baseArena = new BaseArena(arenaMap.getMapName(), arenaMap.getMapCreator(), spawnOne, spawnTwo, arenaMap.getBlockInfos(), multiplier);

        arenaCreationTask.addArena(baseArena);
        arenaMap.addArena(baseArena);
    }

    private List<BlockInfo> getBlocks(Location cornerOne, Location cornerTwo) {
        List<BlockInfo> blockInfos = new ArrayList<>();
        World world = cornerOne.getWorld();
        for (int y = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY()); y <= Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY()); y++) {
            for (int x = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX()); x <= Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX()); x++) {
                for (int z = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ()); z <= Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ()); z++) {
                    if (!world.getChunkAt(x, z).isLoaded())
                        world.getChunkAt(x, z).load();

                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR)
                        blockInfos.add(new BlockInfo(block.getLocation(), block.getTypeId(), block.getData()));

                }
            }
        }
        return blockInfos;
    }

    public BaseArena getSoloMap(boolean building) {
        ArenaMap arenaMap = soloMaps.get(threadLocalRandom.nextInt(soloMaps.size()));
        if (building) {
            if (arenaMap.isBuilding()) {
                createNewMap(arenaMap, arenaMap.getCurrentMultiplier());
                return arenaMap.getArena();
            } else return getSoloMap(true);
        } else {
            if (!arenaMap.isBuilding()) {
                createNewMap(arenaMap, arenaMap.getCurrentMultiplier());
                return arenaMap.getArena();
            } else return getSoloMap(false);
        }
    }

    public BaseArena getPartyMap(boolean building) {
        ArenaMap arenaMap = partyMaps.get(threadLocalRandom.nextInt(partyMaps.size()));
        if (building) {
            if (arenaMap.isBuilding()) {
                createNewMap(arenaMap, arenaMap.getCurrentMultiplier());
                return arenaMap.getArena();
            } else return getSoloMap(true);
        } else {
            if (!arenaMap.isBuilding()) {
                createNewMap(arenaMap, arenaMap.getCurrentMultiplier());
                return arenaMap.getArena();
            } else return getSoloMap(false);
        }
    }





}
