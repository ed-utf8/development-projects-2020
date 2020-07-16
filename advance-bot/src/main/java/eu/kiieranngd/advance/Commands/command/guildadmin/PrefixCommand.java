package eu.kiieranngd.advance.Commands.command.guildadmin;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.ConfigManager;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Ed on 09/07/2017.
 */
public class PrefixCommand implements Command {

    private Manager manager;

    public PrefixCommand(Manager manager) {this.manager = manager;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        CGuild cGuild = manager.getGuild(event.getGuild().getId());
        if (cGuild != null) {
            ConfigManager configManager = cGuild.getConfigManager();
            if (configManager != null) {
                if (content.length() > 1) {
                    if (event.getGuild().getOwner() == event.getMember()) {
                        configManager.setPrefix(content);
                        manager.sendPrivateMessage(event.getGuild().getOwner().getUser(), "Successfully updated the prefix to: " + content);
                    } else {
                        manager.sendPrivateMessage(event.getGuild().getOwner().getUser(), "You do not have permissions for this!");
                    }
                } else {
                    channel.sendMessage("The Current Prefix is `" + configManager.getPrefix() + "`").queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"changeprefix"};}

    @Override
    public String getName() {return "prefix";}

    @Override
    public String getDescription() {return "Change the Default Prefix for your Guild";}

    @Override
    public String getHelp() {return "prefix <new prefix>";}

    @Override
    public Category getCategory() {return Category.GUILDADMIN;}
}