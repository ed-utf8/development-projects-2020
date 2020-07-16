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
public class ModCommand implements Command {

    private Manager manager;
    private SQLManager sqlManager;

    public ModCommand(Manager manager, SQLManager sqlManager) {
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
                                if (!cGuild.getMods().contains(discordUser.getId())) {
                                    cGuild.addMod(discordUser.getId());
                                    sqlManager.addMod(cGuild, discordUser.getId());
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is now a moderator").queue();
                                } else {
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is already a moderator").queue();
                                }
                                break;
                            case "remove":
                                if (cGuild.getMods().contains(discordUser.getId())) {
                                    cGuild.removeMod(discordUser.getId());
                                    sqlManager.removeMod(cGuild, discordUser.getId());
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is no longer a moderator").queue();
                                } else {
                                    event.getMessage().getChannel().sendMessage(user.getAsMention() + " is not a moderator").queue();
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
    public String getName() {return "mod";}

    @Override
    public String getDescription() {return "Add or remove a users mod role";}

    @Override
    public String getHelp() {
        return "mod add @MENTION | mod remove @MENTION";
    }

    @Override
    public Category getCategory() {return Category.GUILDADMIN;}
}
