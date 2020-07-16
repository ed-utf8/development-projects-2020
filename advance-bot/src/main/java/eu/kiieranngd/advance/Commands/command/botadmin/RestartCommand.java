package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class RestartCommand implements Command {

    private Manager manager;
    private Advance advance;

    public RestartCommand(Manager manager, Advance advance) {
        this.manager = manager;
        this.advance = advance;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {
                advance.restartBot();
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"reboot"};}

    @Override
    public String getName() {return "restart";}

    @Override
    public String getDescription() {return "Restart the Bots Shards";}

    @Override
    public String getHelp() {return "restart";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}
