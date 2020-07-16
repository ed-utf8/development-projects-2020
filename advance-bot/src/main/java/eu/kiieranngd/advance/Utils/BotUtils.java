package eu.kiieranngd.advance.Utils;

import eu.kiieranngd.advance.Advance;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;

import java.io.File;

/**
 * Created by Edward on 29/03/2017.
 */
public class BotUtils {

    private String ACTIVE_BLOCK = "\u2588";
    private String EMPTY_BLOCK = "\u00AD";

    private Advance advance;
    public BotUtils (Advance advance) {this.advance = advance;}

    public void changeBotStatus(String status) {
        advance.getBots().forEach(jda -> jda.getPresence().setGame(Game.playing(status)));
    }

    public void changeBotStatusStream(String status) {
        advance.getBots().forEach(jda -> jda.getPresence().setGame(Game.streaming(status, "https://twitch.tv/advancehub")));
    }

    public void changeBotUsername(String newUser) {
        advance.getBots().forEach(jda -> jda.getSelfUser().getManager().setName(newUser).queue());
    }

    public void changeBotAvatar(File image) {
        advance.getBots().forEach(jda -> {
            try {
                jda.getSelfUser().getManager().setAvatar(Icon.from(image)).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getProgressBar(long l, long total) {
        int activeBlocks = (int) ((float) l / (float) total * 10f);
        StringBuilder builder = new StringBuilder().append(EMPTY_BLOCK);
        for (int i = 0; i < 10; i++) builder.append(activeBlocks >= i ? ACTIVE_BLOCK : ' ');
        return builder.append(EMPTY_BLOCK).toString();
    }

    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public void changeBotPresence(OnlineStatus status) {
        advance.getBots().forEach(jda -> jda.getPresence().setStatus(status));
    }

    public void logChannel(MessageEmbed embed) {
        advance.getBots().forEach(jda -> jda.getGuildById("194001900671205378").getTextChannelById("302523820118507520").sendMessage(embed).queue());
    }

    public void mlogChannel(String message) {
        advance.getBots().forEach(jda -> jda.getGuildById("194001900671205378").getTextChannelById("302523820118507520").sendMessage(message).queue());    }
}
