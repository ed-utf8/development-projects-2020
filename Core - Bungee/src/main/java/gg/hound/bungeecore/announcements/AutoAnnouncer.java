package gg.hound.bungeecore.announcements;

import gg.hound.bungeecore.BungeeCorePlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoAnnouncer {

    private final ArrayList<TextComponent> messages;
    private int maxMessage = 0, message = 0;

    public AutoAnnouncer(BungeeCorePlugin bungeeCorePlugin) {

        this.messages = new ArrayList<>();

        String announcementPrefix = "§c§lAnnouncement §7\u00bb §e";

        TextComponent broadcastTwitter = new TextComponent(announcementPrefix + "Follow us on Twitter §7@ §fhttps://twitter.com/Hound_GG");
        broadcastTwitter.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/SerenityPvPMC"));
        broadcastTwitter.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open link").create()));

        this.messages.add(broadcastTwitter);

        TextComponent broadcastDiscord = new TextComponent(announcementPrefix + "Join our Discord §7@ §fhttps://hound.gg/discord");
        broadcastDiscord.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://hound.gg/discord"));
        broadcastDiscord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open link").create()));

        this.messages.add(broadcastDiscord);

        TextComponent broadcastStore = new TextComponent(announcementPrefix + "Visit our Store §7@ §fhttps://store.hound.gg");
        broadcastStore.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.hound.gg"));
        broadcastStore.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to open link").create()));

        this.messages.add(broadcastStore);

        TextComponent bugFound = new TextComponent(announcementPrefix + "Found a bug? Report it to a staff member!");

        this.messages.add(bugFound);

        this.maxMessage = this.messages.size();

        bungeeCorePlugin.getProxy().getScheduler().schedule(bungeeCorePlugin, () -> {
            if (message == (maxMessage - 1)) {
                message = 0;
            }

            for (ProxiedPlayer proxiedPlayer : bungeeCorePlugin.getProxy().getPlayers()) {
                proxiedPlayer.sendMessage(new ComponentBuilder(" ").create());
                proxiedPlayer.sendMessage(this.messages.get(message));
                proxiedPlayer.sendMessage(new ComponentBuilder(" ").create());
            }


            message++;
        }, 5L, 5L, TimeUnit.MINUTES);
    }
}
