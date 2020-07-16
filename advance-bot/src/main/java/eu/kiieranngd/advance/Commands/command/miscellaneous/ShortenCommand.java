package eu.kiieranngd.advance.Commands.command.miscellaneous;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Utils.WebUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ShortenCommand implements Command {

    private Manager manager;
    private WebUtils webUtils;

    public ShortenCommand(Manager manager, WebUtils webUtils) {
        this.manager = manager;
        this.webUtils = webUtils;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        String prefix = manager.getGuild(event.getGuild().getId()).getConfigManager().getPrefix();

        if(content.isEmpty()) {
            channel.sendMessage("Incorrect usage: "+prefix+"shorten <link to shorten>`").queue();
            return;
        }

        channel.sendMessage("Here is your shortened url:" + webUtils.shortenUrl(content)).queue();
    }

    @Override
    public String[] getAliases() {return new String[]{"shortenlink", "linkshorten", "bitly", "googl", "short"};}

    @Override
    public String getName() {
        return "shorten";
    }

    @Override
    public String getDescription() {
        return "Shorten a Given URL";
    }

    @Override
    public String getHelp() {
        return "shorten <link>";
    }

    @Override
    public Category getCategory() {
        return Category.MISCELLANEOUS;
    }
}
