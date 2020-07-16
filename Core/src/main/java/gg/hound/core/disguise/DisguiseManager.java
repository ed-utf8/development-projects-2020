package gg.hound.core.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import gg.hound.core.CorePlugin;
import gg.hound.core.disguise.api.DisguiseObject;
import gg.hound.core.disguise.api.GameApisRest;
import gg.hound.core.disguise.skins.PlayerSkin;
import gg.hound.core.disguise.util.Protocol;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.user.CoreUser;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.FieldBoundReflections;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.WorldSettings;
import net.minecraft.server.v1_8_R3.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisguiseManager {

    private final CorePlugin corePlugin;
    private final DisguiseUtils disguiseUtils;
    private final GameApisRest gameApisRest;
    private final Protocol protocol;
    private final SQLManager sqlManager;
    private final UserManager userManager;

    private final Map<Integer, PlayerSkin> playerSkinHashMap;

    private final FieldBoundReflections profileName = FieldBoundReflections.access(GameProfile.class, "name");

    public DisguiseManager(CorePlugin corePlugin, SQLManager sqlManager, UserManager userManager) {
        this.corePlugin = corePlugin;
        this.sqlManager = sqlManager;
        this.userManager = userManager;

        this.gameApisRest = new GameApisRest();
        this.disguiseUtils = new DisguiseUtils();
        this.protocol = new Protocol();

        playerSkinHashMap = new HashMap<>();

        sqlManager.loadSkins(this);
    }

    public void disguisePlayer(Player toDisguise) {

        List<PlayerSkin> skins = new ArrayList<>(playerSkinHashMap.values());
        Collections.shuffle(skins);

        if (skins.size() < 1) {
            toDisguise.sendMessage(corePlugin.getPrefix() + ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Disguise has not been configured for this server.");
            return;
        }

        disguisePlayer(toDisguise, getRandomName(), skins.get(0), true);
    }

    public void disguisePlayer(Player toDisguise, String disguiseName) {

        List<PlayerSkin> skins = new ArrayList<>(playerSkinHashMap.values());
        Collections.shuffle(skins);

        if (skins.size() < 1) {
            toDisguise.sendMessage(corePlugin.getPrefix() + ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Disguise has not been configured for this server.");
            return;
        }

        disguisePlayer(toDisguise, disguiseName, skins.get(0), true);
    }

    public void disguisePlayer(Player toDisguise, String disguiseName, int skinId) {
        disguisePlayer(toDisguise, disguiseName, playerSkinHashMap.getOrDefault(skinId, new PlayerSkin(0, "eyJ0aW1lc3RhbXAiOjE1MDg5MDE0MDkyNzQsInByb2ZpbGVJZCI6ImU1ZTM4NDVlM2U0ZTRlY2ZiMTIyMDVhNDRiMjJkZjRhIiwicHJvZmlsZU5hbWUiOiJhdHRlbnRpb25wbHoiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifSwidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYzE0MmVkMmZkZDUyMjdiNTg5NzM3Y2NiYWQwZmZmYmFjMWE5NTQ0ZWNkODFiOWNiM2NkYWRiMjQ2NzQifX19", "iA6FgXp9C8H7m40pnEXYWqxHWo76to3hh5aG7ajzEbXX6uIzmi+bU+8o+b/qhCbL0bSjKaH5IPP/JXE0ho0dG+ajfKNqsnZGjBMu1hXFg+e1ZcD2iBullrqCKWtAuBve9UFaY73v4F18DWpcVn93wbkdQ+BhlrdTsewKj7xQCwP8p0nNDREViHRZIBGdBv5uRB+JG0cid6zU2oHYoHtX/l0sw6SBxQO3zZE/NRS5czoWLjfmHY2z73/Xftu68WMmrIBhK76A7Y74aThYlC4oh/05V0151JMLuROwI11MspJHNHynJIKYZ40LuItf39N4l/IRv3pDJv1G0A44gcfQwcEytmms4QlT8hW4/Zt3QDLLXkvdx1XXbwnxvXUoikZfoqQM5KV3LkQDZ4D6SbLuMnzbDWIQfmmWolXIdJnjIHis6KMHuVbJkZYFT6yfGAlYv/QDOB5dV/gtvN18aFtbUZHYPxo70g4FQR6Iky2vw1XSVmvM7hDWWVYzHti7DWQStPsPMyK0LfS/CO1t8BLcZ+fQjSCH+opsSKl9E276MqLWSykadk+q3cZbdV6AGnrb1NT2eima2fJI3OXXn/yssaNghe+jzVfogj/rR5XRiYO1eH7GjTGTGKaK508UzMdvfDfiRqZ6VCXzP0WLTBsuRTGPsCqV3glxzP+cHlq9fgU=")), false);
    }

    public void disguisePlayer(Player toDisguise, String disguiseName, PlayerSkin playerSkin, boolean execute) {

        DisguiseObject disguiseObject = playerSkin.getDisguiseObject(disguiseName);
        CoreUser coreUser = userManager.getUser(toDisguise.getUniqueId());
        EntityPlayer entityPlayer = protocol.getEntity(toDisguise);
        GameProfile gameProfile = entityPlayer.getProfile();


        for (Property property : gameProfile.getProperties().get("textures")) {
            DisguiseObject disguiseObjectOriginal = new DisguiseObject(gameProfile.getName(), property.getValue(), property.getSignature());
            disguiseObjectOriginal.setName(toDisguise.getName());
            coreUser.setDisguised(disguiseObjectOriginal);
        }

        editPlayer(toDisguise, entityPlayer, gameProfile, disguiseObject);

        toDisguise.sendMessage(ChatColor.GREEN + "You are now disguised.");

        if (execute)
            sqlManager.addDisguise(coreUser, disguiseName, playerSkin.getSkinId());
    }

    public void unDisguisePlayer(Player player) {

        CoreUser coreUser = userManager.getUser(player.getUniqueId());
        coreUser.setUnDisguised();
        sqlManager.removeDisguise(coreUser);

        EntityPlayer entityPlayer = protocol.getEntity(player);
        GameProfile gameProfile = entityPlayer.getProfile();
        DisguiseObject disguiseObject = coreUser.getUndisguiseObject();

        editPlayer(player, entityPlayer, gameProfile, disguiseObject);

        player.sendMessage(ChatColor.RED + "You are no longer disguised.");

    }

    private void editPlayer(Player toDisguise, EntityPlayer entityPlayer, GameProfile gameProfile, DisguiseObject disguiseObject) {

        for (Player all : Bukkit.getServer().getOnlinePlayers())
            all.hidePlayer(entityPlayer.getBukkitEntity());

        gameProfile.getProperties().clear();
        entityPlayer.displayName = disguiseObject.getName();
        entityPlayer.listName = IChatBaseComponent.ChatSerializer.a(disguiseObject.getName());
        profileName.set(gameProfile, disguiseObject.getName());
        gameProfile.getProperties().put("textures", new Property("textures", disguiseObject.getValue(), disguiseObject.getSignature()));

        for (Player all : Bukkit.getServer().getOnlinePlayers())
            all.showPlayer(entityPlayer.getBukkitEntity());

        entityPlayer.getBukkitEntity().setPlayerListName(disguiseObject.getName());

        int dimension = entityPlayer.getWorld().worldData.J();
        EnumDifficulty difficulty = entityPlayer.getWorld().getDifficulty();
        WorldType type = entityPlayer.getWorld().worldData.getType();
        WorldSettings.EnumGamemode gamemode = entityPlayer.playerInteractManager.getGameMode();

        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(dimension, difficulty, type, gamemode);
        entityPlayer.playerConnection.sendPacket(respawn);

        entityPlayer.triggerHealthUpdate();
        toDisguise.updateInventory();
        toDisguise.teleport(entityPlayer.getBukkitEntity().getLocation());
    }

    public boolean canUseDisguiseName(String disguiseName) {

        for (String s : disguiseUtils.getBlockedChars()) {
            if (disguiseName.toLowerCase().contains(s)) {
                return false;
            }
        }

        for (String s : disguiseUtils.getBlockedWords()) {
            if (disguiseName.toLowerCase().contains(s)) {
                return false;
            }
        }

        return sqlManager.getUser(disguiseName) == null;
    }

    private String getRandomName() {
        String name = "";

        List<String> names = new ArrayList<>(disguiseUtils.getJoinableWordNames());
        Collections.shuffle(names);
        name = names.get(0);

        if (canUseDisguiseName(name))
            return name;
        return getRandomName();
    }

    public void createSkin(String uuid) {
        DisguiseObject object = gameApisRest.getProfile(uuid).getDisguiseObject();
        sqlManager.addDisguiseSkin(object.getValue(), object.getSignature());
    }

    public void addSkin(PlayerSkin playerSkin) {
        playerSkinHashMap.put(playerSkin.getSkinId(), playerSkin);
    }
}
