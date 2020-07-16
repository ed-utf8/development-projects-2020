package eu.kiieranngd.advance.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import eu.kiieranngd.advance.Manager.MusicManager;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Created by Edward on 08/04/2017.
 */

public class GuildMusicManager {

    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
