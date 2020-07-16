package eu.kiieranngd.advance.Commands.command.guildadmin;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Ed on 09/07/2017.
 */
public class AdminCommand implements Command {

    private Manager manager;
    private SQLManager sqlManager;

    public AdminCommand(Manager manager, SQLManager sqlManager) {
        this.manager = manager;
        this.sqlManager = sqlManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (content.length() > 1) {
            CGuild cGuild = manager.getGuild(event.getGuild().getId());
            if (cGuild != null) {
                if (event.getGuild().getOwner().getUser() == event.getAuthor()) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    DiscordUser discordUser = manager.getUser(user.getId());
                    if (discordUser != null) {
                        switch (content.toLowerCase()) {
                            case "add":
                                if (!cGuild.getAdmins().contains(discordUser.getId())) {
                                    cGuild.addAdmin(discordUser.getId());
                                    sqlManager.addAdmin(cGuild, discordUser.getId());
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is now an administrator").queue();
                                } else {
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is already an administrator").queue();
                                }
                                break;
                            case "remove":
                                if (cGuild.getAdmins().contains(discordUser.getId())) {
                                    cGuild.removeAdmin(discordUser.getId());
                                    sqlManager.removeAdmin(cGuild, discordUser.getId());
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is no longer an administrator").queue();
                                } else {
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is not an administrator").queue();
                                }
                                break;
                        }
                    }
                } else {
                    event.getMessage().getChannel().sendMessage("You do not have permission for that " + event.getAuthor().getAsMention()).queue();
                }
            } else {
                event.getMessage().getChannel().sendMessage("There was an error sending adding an admin, please contact the developer!").queue();
            }
        }
    }

    @Override
    public String getName() {return "admin";}

    @Override
    public String getDescription() {return "Add or remove a users admin role";}

    @Override
    public String getHelp() {return "admin add @MENTION | admin remove @MENTION";}

    @Override
    public Category getCategory() {return Category.GUILDADMIN;}
}
