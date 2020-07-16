package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;

/**
 * Created by kdrew on 09/07/2017.
 */
public class GuildCommand implements Command {

    private Messages messages;
    private Manager manager;

    public GuildCommand(Messages messages, Manager manager) {
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        Guild guild = event.getGuild();
        CGuild cGuild = manager.getGuild(guild.getId());

        long onlineCount  = guild.getMembers().stream().filter((u) -> (u.getOnlineStatus()== OnlineStatus.ONLINE || u.getOnlineStatus()== OnlineStatus.UNKNOWN)).count();
        long idleCount    = guild.getMembers().stream().filter((u) -> (u.getOnlineStatus()== OnlineStatus.IDLE)).count();
        long dndCount     = guild.getMembers().stream().filter((u) -> (u.getOnlineStatus()== OnlineStatus.DO_NOT_DISTURB)).count();
        long offlineCount = guild.getMembers().stream().filter((u) -> (u.getOnlineStatus()== OnlineStatus.OFFLINE || u.getOnlineStatus() == OnlineStatus.INVISIBLE)).count();
        long botCount     = guild.getMembers().stream().filter((u) -> (u.getUser().isBot())).count();

        long channels = guild.getVoiceChannels().size() + guild.getTextChannels().size();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(guild.getName() + "'s Information" , null, guild.getIconUrl())
                .setThumbnail(guild.getIconUrl())
                .setColor(Color.GREEN)

                .addField("❯ Guild", guild.getName() + "\n" + guild.getId(), true)
                .addField("❯ Owner", guild.getOwner().getAsMention() + "#" + guild.getOwner().getUser().getDiscriminator() + "\n" + guild.getOwner().getUser().getId(), true)
                .addField("❯ Information", "Channel: " + guild.getDefaultChannel().getAsMention() + "\nRegion: " + guild.getRegion().getName() + "\nVerification: " + guild.getVerificationLevel().toString(), true)
                .addField("❯ Data", "Creation: " + guild.getCreationTime().toString().substring(0, 10) + "\nDatabase ID: " + String.valueOf(cGuild.getId()) + "\nPrefix: " + manager.getGuild(event.getGuild().getId()).getConfigManager().getPrefix(), true)
                .addField("❯ Members","<:online:319880156942696449> " + onlineCount + " **|** " + "<:away:319890807765073921> " + idleCount + " **|** " + "<:dnd:319890819932880896> " + dndCount + " **|** " + "<:offline:319890857899851785> " + offlineCount + " **|** " + "<:botadmin:319880143441100801> " + botCount + " **|** " + "\uD83D\uDD17 " + String.valueOf(guild.getMembers().size()), true)
                .addField("❯ Channels", "<:tc:319890925298122762> " + String.valueOf(guild.getTextChannels().size()) + " Text Channels **|** " + "<:vc:319890935817437186> " + String.valueOf(guild.getVoiceChannels().size()) + " Voice Channels **|** " + "\uD83D\uDCAC " + channels + " Total", true)
                .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());
        channel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String[] getAliases() {return new String[]{"guildinfo", "server", "g"};}

    @Override
    public String getName() {return "guild";}

    @Override
    public String getDescription() {return "Get information about the guild";}

    @Override
    public String getHelp() {return "guild";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
