package eu.kiieranngd.advance.Listeners;

import com.google.common.util.concurrent.MoreExecutors;
import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.CommandManager;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Manager.MusicManager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.User.DiscordUser;
import eu.kiieranngd.advance.Utils.BotUtils;
import net.dv8tion.jda.client.entities.Application;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class BotListeners extends ListenerAdapter {

    private SQLManager sqlManager;
    private Manager manager;
    private Messages messages;
    private List<JDA> jda;
    private Advance advance;
    private BotUtils botUtils;
    private MusicManager musicManager;
    private CommandManager commandManager;

    public BotListeners(SQLManager sqlManager, Manager manager, Messages messages, List<JDA> jda, Advance advance, BotUtils botUtils, MusicManager musicManager, CommandManager commandManager) {
        this.sqlManager = sqlManager;
        this.manager = manager;
        this.messages = messages;
        this.jda = jda;
        this.advance = advance;
        this.botUtils = botUtils;
        this.musicManager = musicManager;
        this.commandManager = commandManager;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        if (manager.getGuild(e.getGuild().getId()) == null) {
            if (!sqlManager.doesGuildExist(e.getGuild().getId())) {
                sqlManager.addGuild(e.getGuild().getId());
            } else {
                sqlManager.enableGuild(e.getGuild().getId());
            }
        }

        for (Member member : e.getGuild().getMembers()) {
            DiscordUser discordUser = manager.getUser(member.getUser().getId());
            if (discordUser == null) {
                sqlManager.loadUser(member.getUser().getId(), e.getGuild().getId());
            } else {
                discordUser.addGuild(manager.getGuild(e.getGuild().getId()));
            }
        }
        long botCount = e.getGuild().getMembers().stream().filter((u) -> (u.getUser().isBot())).count();
        String plusOne = "<:plusOne:323375195756822528>";

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(plusOne + " Server Join: " + e.getGuild().getName(), null, e.getGuild().getIconUrl());
        embedBuilder.setThumbnail(e.getGuild().getIconUrl()).setColor(messages.getRandomColor());
        embedBuilder.addField("Guild", "Name: " + e.getGuild().getName() +  "\nOwner: " + e.getGuild().getOwner().getAsMention() + "\nRegion: " + e.getGuild().getRegion() + "\nID: "+ e.getGuild().getId(), true);
        embedBuilder.addField("Members", "Users: " + String.valueOf(e.getGuild().getMembers().size()) + "\nBots: " + botCount, true);
        embedBuilder.addField("Creation", MiscUtil.getCreationTime(e.getGuild().getIdLong()).format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        embedBuilder.setFooter("Advance: Connected to " + advance.getTotalGuilds() + " Guilds ", e.getJDA().getSelfUser().getAvatarUrl()).setTimestamp(Instant.now());
        botUtils.logChannel(embedBuilder.build());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        sqlManager.disableGuild(e.getGuild().getId());

        long botCount = e.getGuild().getMembers().stream().filter((u) -> (u.getUser().isBot())).count();
        String minusOne = "<:minusOne:323375203390586881>";


                EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(minusOne + " Server Leave: " + e.getGuild().getName(), null, e.getGuild().getIconUrl());
        embedBuilder.setThumbnail(e.getGuild().getIconUrl()).setColor(messages.getRandomColor());
        embedBuilder.addField("Guild", "Name: " + e.getGuild().getName() + "\nOwner: " + e.getGuild().getOwner().getAsMention() + "\nRegion: " + e.getGuild().getRegion() + "\nID: "+ e.getGuild().getId(), true);
        embedBuilder.addField("Members", "Users: " + String.valueOf(e.getGuild().getMembers().size()) + "\nBots: " + botCount, true);
        embedBuilder.addField("Creation", MiscUtil.getCreationTime(e.getGuild().getIdLong()).format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        embedBuilder.setFooter("Advance: Connected to " + advance.getTotalGuilds() + " Guilds ", e.getJDA().getSelfUser().getAvatarUrl()).setTimestamp(Instant.now());
        botUtils.logChannel(embedBuilder.build());
    }

    @Override
    public void onShutdown(ShutdownEvent e) {
        sqlManager.onShutdown();
        MoreExecutors.shutdownAndAwaitTermination(commandManager.pool, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        CGuild cGuild = manager.getGuild(e.getGuild().getId());
        if (cGuild != null) {
            DiscordUser discordUser = manager.getUser(e.getMember().getUser().getId());
            if (discordUser == null) {
                sqlManager.loadUser(e.getMember().getUser().getId(), cGuild.getGuildID());
            }
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        CGuild cGuild = manager.getGuild(e.getGuild().getId());
        if (cGuild != null) {
            DiscordUser discordUser = manager.getUser(e.getMember().getUser().getId());
            if (discordUser != null) {
                discordUser.getGuilds().remove(cGuild);
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
        CGuild cGuild = manager.getGuild(event.getGuild().getId());

        if(!event.getVoiceState().getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId()) && event.getGuild().getAudioManager().isConnected()){
            if (!event.getChannelLeft().getId().equals(event.getGuild().getAudioManager().getConnectedChannel().getId())) { return; }
            if(event.getChannelLeft().getMembers().size() <= 1){
                musicManager.getMusicManager(cGuild).player.stopTrack();
                musicManager.getMusicManager(cGuild).player.setPaused(false);
                musicManager.getMusicManager(cGuild).scheduler.getQueue().clear();

                if(event.getGuild().getAudioManager().isConnected()){
                    event.getGuild().getAudioManager().closeAudioConnection();
                    event.getGuild().getAudioManager().setSendingHandler(null);
                }
            }
        }
    }
}