package eu.kiieranngd.advance.Commands.command.economy;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class BalanceCommand implements Command {

    private Manager manager;
    private Messages messages;

    public BalanceCommand (Manager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        event.getMessage().delete().queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        manager.sendPrivateMessage(event.getAuthor(), embedBuilder.setColor(messages.getRandomColor()).setDescription("You currently have " + String.valueOf(manager.getUser(event.getAuthor().getId()).getBalance()) + " Coins <:coins:319890988933840896>").build());

    }

    @Override
    public String[] getAliases() {return new String[]{"bal", "money", "bank"};}

    @Override
    public String getName() {return "balance";}

    @Override
    public String getDescription() {return "Check your Balance";}

    @Override
    public String getHelp() {return "balance";}

    @Override
    public Category getCategory() {return Category.ECONOMY;}
}
