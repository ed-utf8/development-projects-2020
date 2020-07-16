package gg.hound.arena.match.kit;

import gg.hound.arena.Arena;
import gg.hound.arena.match.kit.kits.FinalUHC;
import gg.hound.arena.match.kit.kits.NoDebuff;
import gg.hound.arena.match.kit.kits.SG;
import gg.hound.arena.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitManager {

    private final Arena arena;

    private final Map<UUID, Kit> editingKit = new HashMap<>();

    private final Map<String, Kit> kitMap = new HashMap<>();

    public KitManager(Arena arena) {
        this.arena = arena;

        loadKits();
    }

    private void loadKits() {
        FinalUHC finalUHC = new FinalUHC();
        kitMap.put(finalUHC.getName(), finalUHC);
        NoDebuff noDebuff = new NoDebuff();
        kitMap.put(noDebuff.getName(), noDebuff);
        SG sg = new SG();
        kitMap.put(sg.getName(), sg);
        arena.log("Successfully loaded kits.");
    }

    public Collection<Kit> getKits() {
        return kitMap.values();
    }

    public Kit getKit(String name) {
        return kitMap.get(name.replace("§a", ""));
    }

    public Kit getKit(int id) {
        for (Kit kit : kitMap.values()) {
            if (kit.getId() == id)
                return kit;
        }
        return null;
    }

    public void addKitEditor(Player player, Kit kit, User user) {
        editingKit.put(player.getUniqueId(), kit);

        player.sendMessage(arena.getPrefix() + "Now editing " + kit.getName());
        player.sendMessage(arena.getPrefix() + "Type '/save' when you're done");

        for (Player online : Bukkit.getOnlinePlayers())
            online.hidePlayer(player);

        player.getInventory().clear();

        if (user.getInventoryLayout(kit) == null)
            kit.giveKit(player);
        else
            player.getInventory().setContents(user.getInventoryLayout(kit));

        player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

        player.updateInventory();
    }

    public Kit getKit(UUID uuid) {
        return editingKit.get(uuid);
    }

    public boolean isEditingKit(UUID uuid) {
        return editingKit.containsKey(uuid);
    }

    public void removeKitEditor(Player player) {
        editingKit.remove(player.getUniqueId());

        for (Player online : Bukkit.getOnlinePlayers())
            online.showPlayer(player);

        player.teleport(player.getWorld().getSpawnLocation());
    }

    public ItemStack[] parseInventory(String inventoryString) {
        String[] serializedBlocks = inventoryString.split(";");
        ItemStack[] deserializedInventory = new ItemStack[36];

        for (int i = 1; i < serializedBlocks.length; i++) {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.parseInt(serializedBlock[0]);

            if (stackPosition >= deserializedInventory.length) continue;

            ItemStack itemStack = null;
            boolean createdItemStack = false;

            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack) {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t")) {
                    itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(itemAttribute[1])));
                    createdItemStack = true;
                } else if (itemAttribute[0].equals("d") && createdItemStack) {
                    itemStack.setDurability(Short.parseShort(itemAttribute[1]));
                } else if (itemAttribute[0].equals("a") && createdItemStack) {
                    itemStack.setAmount(Integer.parseInt(itemAttribute[1]));
                } else if (itemAttribute[0].equals("e") && createdItemStack) {
                    itemStack.addEnchantment(Enchantment.getById(Integer.parseInt(itemAttribute[1])), Integer.parseInt(itemAttribute[2]));
                } else if (itemAttribute[0].equalsIgnoreCase("g") && createdItemStack) {
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName("§6Golden Head");
                    itemStack.setItemMeta(itemMeta);
                }
            }

            deserializedInventory[i - 1] = itemStack;
        }

        return deserializedInventory;
    }

    public String serialiseInventory(ItemStack[] contents) {
        StringBuilder serialization = new StringBuilder(contents.length + ";");
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack != null) {
                StringBuilder serializedItemStack = new StringBuilder();

                String isType = String.valueOf(itemStack.getType().getId());
                serializedItemStack.append("t@").append(isType);

                if (itemStack.getDurability() != 0) {
                    String isDurability = String.valueOf(itemStack.getDurability());
                    serializedItemStack.append(":d@").append(isDurability);
                }

                if (itemStack.getAmount() != 1) {
                    String isAmount = String.valueOf(itemStack.getAmount());
                    serializedItemStack.append(":a@").append(isAmount);
                }

                Map<Enchantment,Integer> isEnchanted = itemStack.getEnchantments();
                if (isEnchanted.size() > 0) {
                    for (Map.Entry<Enchantment,Integer> enchantmentIntegerEntry : isEnchanted.entrySet()) {
                        serializedItemStack.append(":e@").append(enchantmentIntegerEntry.getKey().getId()).append("@").append(enchantmentIntegerEntry.getValue());
                    }
                }
                if (itemStack.getType().equals(Material.GOLDEN_APPLE)) {
                    if (itemStack.getItemMeta().getDisplayName() != null) {
                        if (itemStack.getItemMeta().getDisplayName().equals("§6Golden Head")) {
                            serializedItemStack.append(":g@");
                        }
                    }
                }


                serialization.append(i).append("#").append(serializedItemStack).append(";");
            }
        }
        return serialization.toString();
    }




}
