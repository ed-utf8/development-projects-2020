package eu.kiieranngd.advance.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import eu.kiieranngd.advance.Manager.MusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Edward on 08/04/2017.
 */

public class TrackScheduler extends AudioEventAdapter {

    private boolean repeating = false;
    private final AudioPlayer player;
    private final Queue<AudioTrack> queue;
    private AudioTrack lastTrack;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        if (endReason.mayStartNext) {
            if (repeating)
                player.startTrack(lastTrack.makeClone(), false);
            else
                nextTrack();
        }
    }

    public boolean isRepeating() {
        return repeating;
    }
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }
    public void shuffle() {
        Collections.shuffle((List<?>) queue);
    }
    public AudioPlayer getPlayer() {
        return player;
    }
    public AudioTrack getLastTrack() {
        return lastTrack;
    }
    public Queue<AudioTrack> getQueue() {
        return queue;
    }
}
