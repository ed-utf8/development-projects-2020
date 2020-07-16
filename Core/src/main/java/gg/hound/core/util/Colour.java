package gg.hound.core.util;

import org.bukkit.ChatColor;

public class Colour {

    private final int id;
    private final String name;
    private final ChatColor chatColor;
    private final boolean bold;
    private final boolean itallic;
    private final boolean strikethrough;
    private final boolean showInInventory;
    private String bigDaddyConcatenation;

    public Colour(int id, String name, ChatColor chatColor, boolean bold, boolean itallic, boolean strikethrough, boolean showInInventory) {
        this.id = id;
        this.name = name;
        this.chatColor = chatColor;
        this.bold = bold;
        this.itallic = itallic;
        this.strikethrough = strikethrough;
        this.showInInventory = showInInventory;

        bigDaddyConcatenation = chatColor.toString();
        if (bold)
            bigDaddyConcatenation += ChatColor.BOLD.toString();
        if (itallic)
            bigDaddyConcatenation += ChatColor.ITALIC.toString();
        if (strikethrough)
            bigDaddyConcatenation += ChatColor.STRIKETHROUGH.toString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isShowInInventory() {
        return showInInventory;
    }

    public String colourize() {
        return ChatColor.RESET + bigDaddyConcatenation;
    }

}
