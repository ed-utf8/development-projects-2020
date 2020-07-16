package eu.kiieranngd.advance.Commands.command.economy;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class PayCommand implements Command {

    private Manager manager;
    private Messages messages;

    public PayCommand (Manager manager, Messages messages) {
        this.manager = manager;
        this.messages = messages;
    }
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (content.length() == 2) {
            DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
            DiscordUser targetUser = manager.getUser(event.getMessage().getMentionedUsers().get(0).getId());
            long amount = Long.valueOf(content);
            if (discordUser != null && targetUser != null) {
                if (amount >= 1) {
                    if (discordUser.getBalance() >= amount) {
                        discordUser.setBalance(discordUser.getBalance() - amount);
                        targetUser.setBalance(targetUser.getBalance() + amount);
                        manager.sendPrivateMessage(event.getAuthor(), embedBuilder.setColor(messages.getRandomColor()).setDescription("You have successfully paid " + event.getMessage().getMentionedUsers().get(0).getName() + amount + " Coins <:coins:319890988933840896>").build());
                        manager.sendPrivateMessage(event.getMessage().getMentionedUsers().get(0), embedBuilder.setColor(messages.getRandomColor()).setDescription("You have been sent " + amount + " Coins from " + event.getAuthor().getName() + "<:coins:319890988933840896>").build());
                    } else {
                        manager.sendPrivateMessage(event.getAuthor(), embedBuilder.setColor(messages.getRandomColor()).setDescription("You do not have sufficient funds! <:coins:319890988933840896>").build());
                    }
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"send"};}

    @Override
    public String getName() {return "pay";}

    @Override
    public String getDescription() {return "Pay a Friend";}

    @Override
    public String getHelp() {return "pay";}

    @Override
    public Category getCategory() {return Category.ECONOMY;}
}