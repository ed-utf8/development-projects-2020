package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.User.DiscordUser;
import eu.kiieranngd.advance.Utils.BotUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class UsernameCommand implements Command {

    private BotUtils botUtils;
    private Manager manager;

    public UsernameCommand (BotUtils botUtils, Manager manager) {
        this.botUtils = botUtils;
        this.manager = manager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {
                String name = content;
                botUtils.changeBotUsername(name);
            }
        }
    }

    @Override
    public String getName() {return "username";}

    @Override
    public String getDescription() {return "Change Advances Username";}

    @Override
    public String getHelp() {return "username <name>";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}
