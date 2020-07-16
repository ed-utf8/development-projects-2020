package eu.kiieranngd.advance.Commands.command.miscellaneous;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

/**
 * Created by Kieran on 07/07/2017.
 */
public class BotsCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bots Connected to " + event.getGuild().getName());

        List<Member> members = event.getGuild().getMembers();
        StringBuilder sb = new StringBuilder();
        members.stream().filter(member -> member.getUser().isBot()).forEach(member -> {
            sb.append(member.getAsMention() + "\n");
        });

        embedBuilder.setDescription(sb.toString());
        event.getTextChannel().sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String[] getAliases() {return new String[]{"listbots"};}

    @Override
    public String getName() {return "bots";}

    @Override
    public String getDescription() {
        return "Lists all the bots in the current guild";
    }

    @Override
    public String getHelp() {return "bots";}

    @Override
    public Category getCategory() {return Category.MISCELLANEOUS;}
}
