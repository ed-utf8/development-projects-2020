package eu.kiieranngd.advance.Commands.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by utf_8 on 08/04/2017
 */
public class NPCommand implements Command {

    private Manager manager;
    private Messages messages;
    private MusicManager musicManager;

    public NPCommand(Manager manager, Messages messages, MusicManager musicManager) {
        this.manager = manager;
        this.messages = messages;
        this.musicManager = musicManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        AudioPlayer audioPlayer = musicManager.getMusicManager(manager.getGuild(event.getGuild().getId())).player;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (audioPlayer != null) {
            AudioTrack currentTrack = audioPlayer.getPlayingTrack();
            if (currentTrack != null) {
                String link = currentTrack.getInfo().uri;
                String title = currentTrack.getInfo().title;
                String position = getTimestamp(currentTrack.getPosition());
                String duration = getTimestamp(currentTrack.getDuration());
                double progress = (double) currentTrack.getPosition() / currentTrack.getDuration();


                String nowplaying = String.format("\uD83C\uDFB5 **Playing ❯ ** [%s](%s)\n▶" + progressBar(progress) + " [%s / %s] \uD83D\uDD0A",
                        title, link, position, duration);

                event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription(nowplaying).build()).queue();
            } else
                event.getChannel().sendMessage(embedBuilder.setColor(messages.getRandomColor()).setDescription("\uD83C\uDFB5 The player is not currently playing anything!").build()).queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"nowplaying", "current"};}

    @Override
    public String getName() {
        return "np";
    }

    @Override
    public String getDescription() {
        return "Get the current playing track!";
    }

    @Override
    public String getHelp() {
        return "np";
    }

    @Override
    public Category getCategory() {
        return Category.MUSIC;
    }

    private String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    private String progressBar(double percent)
    {
        String str = "";
        for(int i=0; i<8; i++)
            if(i == (int)(percent*8))
                str+="\uD83D\uDD18";
            else
                str+="▬";
        return str;
    }
}
