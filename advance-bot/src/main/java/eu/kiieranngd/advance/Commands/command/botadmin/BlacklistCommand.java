package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class BlacklistCommand implements Command {

    private Manager manager;
    private SQLManager sqlManager;

    public BlacklistCommand(Manager manager, SQLManager sqlManager) {
        this.manager = manager;
        this.sqlManager = sqlManager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {
                if (content.length() > 1) {
                    User user = event.getMessage().getMentionedUsers().get(0);
                    DiscordUser discordUser1 = manager.getUser(user.getId());
                    if (discordUser1 != null) {
                        switch (content.toLowerCase()) {
                            case "add":
                                discordUser1.setBlacklisted(true);
                                manager.sendPrivateMessage(event.getAuthor(), user.getName() + " has been blacklisted!");
                                sqlManager.blacklistUser(discordUser1);
                                break;
                            case "remove":
                                discordUser1.setBlacklisted(false);
                                manager.sendPrivateMessage(event.getAuthor(), user.getName() + " has been unblacklisted!");
                                sqlManager.blacklistUser(discordUser1);
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"bl"};}

    @Override
    public String getName() {return "blacklist";}

    @Override
    public String getDescription() {return "Blacklist a User from using Advance";}

    @Override
    public String getHelp() {return "blacklist add @MENTION | blacklist remove @MENTION";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}