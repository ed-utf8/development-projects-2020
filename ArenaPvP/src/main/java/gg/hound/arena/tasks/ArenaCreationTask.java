package gg.hound.arena.tasks;

import gg.hound.arena.Arena;
import gg.hound.arena.arenas.ArenaState;
import gg.hound.arena.arenas.BaseArena;
import gg.hound.arena.arenas.block.BlockInfo;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Queue;

public class ArenaCreationTask extends BukkitRunnable {

    private final Arena arena;

    public ArenaCreationTask(Arena arena) {
        this.arena = arena;
    }

    private final Queue<BaseArena> baseArenas = new ArrayDeque<>();

    private BaseArena currentArena;
    private Queue<BlockInfo> blockInfos;
    private int multiplier = -1;

    private int arenaCounter = 0;

    @Override
    public void run() {
        if (baseArenas.size() == 0) {
            if (currentArena == null)
                return;
        }

        if (currentArena == null) {
            currentArena = baseArenas.poll();
        }

        if (blockInfos == null) {
            blockInfos = new ArrayDeque<>();
            blockInfos.addAll(currentArena.getBlockInfos());
        }

        if (multiplier == -1) {
            multiplier = currentArena.getMultiplier();
        }

        World world;
        Location location;
        BlockInfo blockInfo;
        for (int i = 0; i <= 1250; i++) {
            if (blockInfos.size() == 0) {
                blockInfos = null;
                currentArena.setArenaState(ArenaState.READY);
                currentArena = null;
                multiplier = -1;
                arenaCounter++;
                System.out.println("Placed: " + arenaCounter + " arenas");
                if (arenaCounter == 12 * 5)
                    arena.setCanConnect(true);
                return;
            }

            blockInfo = blockInfos.poll();

            world = blockInfo.getLocation().getWorld();

            location = blockInfo.getLocation().clone();

            location.setX(location.getX() + (250 * multiplier));

            world.getBlockAt(location).setTypeId(blockInfo.getBlockTypeID());
            world.getBlockAt(location).setData(blockInfo.getBlockData());
        }

    }

    public void addArena(BaseArena baseArena) {
        baseArenas.add(baseArena);
    }
}
