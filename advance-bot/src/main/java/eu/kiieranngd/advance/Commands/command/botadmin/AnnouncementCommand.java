package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.Instant;

/**
 * Created by kdrew on 09/07/2017.
 */
public class AnnouncementCommand implements Command {

    private Messages messages;
    private Manager manager;
    private Advance advance;

    public AnnouncementCommand(Messages messages, Manager manager, Advance advance) {
        this.manager = manager;
        this.messages = messages;
        this.advance = advance;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {
                if (content.length() > 0) {

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setAuthor("Announcement", "https://twitter.com/AdvanceHub", event.getJDA().getSelfUser().getAvatarUrl())
                            .setDescription(content)
                            .setColor(messages.getRandomColor())
                            .setFooter(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getAvatarUrl()).setTimestamp(Instant.now());

                    try {
                        event.getGuild().getDefaultChannel().sendMessage(embedBuilder.build()).queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    event.getMessage().getChannel().sendMessage(new EmbedBuilder().setColor(messages.getRandomColor()).setDescription("❌ Sorry, you haven't entered a Message to send.").build()).queue();
                }
            }
        } else {
            event.getMessage().getChannel().sendMessage(new EmbedBuilder().setColor(messages.getRandomColor()).setDescription("❌ Sorry, you do not have permission to send an Announcement.").build()).queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"announce", "createannouncement"};}

    @Override
    public String getName() {return "announcement";}

    @Override
    public String getDescription() {return "Send an Announcement to all the Guilds";}

    @Override
    public String getHelp() {return "announcement";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}