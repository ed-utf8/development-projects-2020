package eu.kiieranngd.advance.Manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Music.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class MusicManager extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<CGuild, GuildMusicManager> musicManagers;

    public MusicManager() {

        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());

        musicManagers = new HashMap<>();
    }

    public void addGuild(CGuild cGuild) {
        GuildMusicManager guildMusicManager = new GuildMusicManager(playerManager);
        guildMusicManager.player.setVolume(35);
        musicManagers.put(cGuild, guildMusicManager);
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
    public GuildMusicManager getMusicManager(CGuild cGuild) {
        return musicManagers.get(cGuild);
    }
}
