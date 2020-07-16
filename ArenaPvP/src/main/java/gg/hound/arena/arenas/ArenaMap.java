package gg.hound.arena.arenas;

import gg.hound.arena.arenas.block.BlockInfo;
import org.bukkit.Location;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class ArenaMap {

    private final int id;

    private int currentMultiplier = 1;

    private final String mapName;
    private final String mapCreator;

    private final boolean building;

    private final Location cornerOne;
    private final Location cornerTwo;

    private final Location spawnOne;
    private final Location spawnTwo;

    private List<BlockInfo> blockInfos;

    private final Queue<BaseArena> baseArenas = new ArrayDeque<>();

    public ArenaMap(int id, String mapName, String mapCreator, boolean building, Location cornerOne, Location cornerTwo, Location spawnOne, Location spawnTwo) {
        this.id = id;
        this.mapName = mapName;
        this.mapCreator = mapCreator;
        this.building = building;
        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;
        this.spawnOne = spawnOne;
        this.spawnTwo = spawnTwo;
    }


    public List<BlockInfo> getBlockInfos() {
        return blockInfos;
    }

    public void setBlockInfos(List<BlockInfo> blockInfos) {
        this.blockInfos = blockInfos;
    }

    public int getCurrentMultiplier() {
        return currentMultiplier;
    }

    public void incrementCurrentMultiplier() {
        currentMultiplier += 1;
    }

    public int getId() {
        return id;
    }

    public String getMapName() {
        return mapName;
    }

    public String getMapCreator() {
        return mapCreator;
    }

    public boolean isBuilding() {
        return building;
    }

    public Location getCornerOne() {
        return cornerOne;
    }

    public Location getCornerTwo() {
        return cornerTwo;
    }

    public Location getSpawnOne() {
        return spawnOne;
    }

    public Location getSpawnTwo() {
        return spawnTwo;
    }

    public BaseArena getArena() {
        BaseArena baseArena = baseArenas.poll();
        if (baseArena == null)
            return null;
        if (baseArena.getArenaState() == ArenaState.READY)
            return baseArena;
        baseArenas.add(baseArena);
        return null;
    }

    public void addArena(BaseArena baseArena) {
        baseArenas.add(baseArena);
        System.out.println("Arena ID: " + id + " Arenas: " + baseArenas.size());
    }
}
