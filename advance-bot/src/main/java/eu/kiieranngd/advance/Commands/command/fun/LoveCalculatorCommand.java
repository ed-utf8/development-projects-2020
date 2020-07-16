package eu.kiieranngd.advance.Commands.command.fun;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.Utils.BotUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LoveCalculatorCommand implements Command {

    private BotUtils botUtils;
    private Messages messages;

    public LoveCalculatorCommand(BotUtils botUtils, Messages messages) {
        this.botUtils = botUtils;
        this.messages = messages;
    }
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        User firstName = message.getMentionedUsers().get(0);
        User secondName = message.getMentionedUsers().get(1);

        if (firstName.equals(secondName)) {
            channel.sendMessage("Oh well, that's narcissistic.").queue();
            return;
        }
        if (firstName.getName().isEmpty() || secondName.getName().isEmpty()) {
            channel.sendMessage("I need 2 names to calculate love percentage :shrug:").queue();
            return;
        }
        int percentage = (firstName.getName().codePoints().sum() + secondName.getName().codePoints().sum()) % 101;

        String lMessage = sMessage(percentage);
        channel.sendMessage(new EmbedBuilder()
                .setDescription("\u2763 **LOVE CALCULATOR** \u2763\n\uD83D\uDC97 *`" + firstName.getAsMention() + "`*\n\uD83D\uDC97 *`" + secondName.getAsMention() + "`*\n**" + percentage + "%** `" + botUtils.getProgressBar(percentage, 100) + "` " + lMessage)
                .setColor(messages.getRandomColor()).build()).queue();

    }

    @Override
    public String[] getAliases() {
        return new String[]{"lovecalc", "lcalc"};
    }

    @Override
    public String getName() {
        return "lovecalculator";
    }

    @Override
    public String getDescription() {
        return "Calculates the love between two names";
    }

    @Override
    public String getHelp() {
        return "lovecalculator @Mention @Mention";
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }

    private String sMessage(int percentage) {
        if (percentage <= 20)
            return "Better luck next time.";
        else if (percentage <= 40)
            return "Not so bad.";
        else if (percentage == 69)
            return "( ͡° ͜ʖ ͡°)";
        else if (percentage <= 60)
            return "Pretty great!";
        else if (percentage <= 80)
            return "A lovely ship! \uD83E\uDD1E";
        else
            return "Perfect! \u2764";
    }
}
