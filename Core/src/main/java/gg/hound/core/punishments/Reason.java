package gg.hound.core.punishments;

import gg.hound.core.util.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public class Reason {

    private final int id;

    private final String name;
    private final String displayName;
    private final ItemStack itemStack;
    private final boolean textInputRequired;
    private final boolean timeInputRequired;
    private final boolean showInMenu;
    private final Map<Integer, Integer> lengths;
    private String customReason = "Other";

    public Reason(int id, String name, String displayName, ItemStack itemStack, boolean textInputRequired, boolean timeInputRequired, boolean showInMenu, Map<Integer, Integer> lengths) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.itemStack = itemStack;
        this.textInputRequired = textInputRequired;
        this.timeInputRequired = timeInputRequired;
        this.showInMenu = showInMenu;
        this.lengths = lengths;
    }

    public Reason(int id, String name, String displayName, ItemStack itemStack, boolean textInputRequired, boolean timeInputRequired, boolean showInMenu) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.itemStack = itemStack;
        this.textInputRequired = textInputRequired;
        this.timeInputRequired = timeInputRequired;
        this.showInMenu = showInMenu;
        this.lengths = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return new ItemBuilder(itemStack).setName(displayName).toItemStack();
    }

    public boolean isTextInputRequired() {
        return textInputRequired;
    }

    public boolean isTimeInputRequired() {
        return timeInputRequired;
    }

    public int getLength(int violation) {
        if (lengths == null)
            return 86400;

        return lengths.getOrDefault(violation, 86400);
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isShowInMenu() {
        return showInMenu;
    }
}
