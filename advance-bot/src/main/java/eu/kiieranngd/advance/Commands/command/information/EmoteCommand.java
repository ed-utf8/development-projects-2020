package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;

/**
 * Created by kdrew on 09/07/2017.
 */
public class EmoteCommand implements Command {

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {

        boolean hasEmotes = !event.getGuild().getEmotes().isEmpty();
        if (hasEmotes) {
            String strEmotes = event.getGuild().getEmotes().stream().map(Emote::getAsMention).collect(Collectors.joining(" "));
            event.getTextChannel().sendMessage("Emotes: " + strEmotes).queue();
        } else {
            event.getTextChannel().sendMessage("Could't Find any Custom Emotes for this Guild").queue();
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"emote", "emotelist"};}

    @Override
    public String getName() {return "emotes";}

    @Override
    public String getDescription() {return "Check out the Guilds Custom Emotes";}

    @Override
    public String getHelp() {return "emotes";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
