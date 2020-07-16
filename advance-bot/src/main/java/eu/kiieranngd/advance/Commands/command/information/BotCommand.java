package eu.kiieranngd.advance.Commands.command.information;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created by kdrew on 09/07/2017.
 */
public class BotCommand implements Command {

    private Manager manager;
    private Messages messages;
    private long startTime;

    public BotCommand (Manager manager, Messages messages, long startTime) {
        this.manager = manager;
        this.messages = messages;
        this.startTime = startTime;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {

        long upTime = System.currentTimeMillis() - startTime;
        long inSeconds = TimeUnit.MILLISECONDS.toSeconds(upTime);

        String lversion = PlayerLibrary.VERSION;
        String botInvite = "https://discordapp.com/oauth2/authorize?&client_id="+event.getJDA().getSelfUser().getId()+"&scope=botadmin&permissions=8";

        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;
        total /= 1024 * 1024;
        used /= 1024 * 1024;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setAuthor(event.getJDA().getSelfUser().getName(), "https://twitter.com/AdvanceHub", event.getJDA().getSelfUser().getAvatarUrl());

        embedBuilder.addField("❯ Bot", "<:botadmin:319880143441100801> " + event.getJDA().getSelfUser().getAsMention() + "\n<:jda:319900912154050561> " + JDAInfo.VERSION + "\n<:lavaplayer:319908416858488854> "+ lversion  , true);
        embedBuilder.addField("❯ Links", "<:support:319900008025554944> [Advance](https://discord.io/advance)" + "\n<:twitter:319890999931437059> [Twitter](https://twitter.com/advancehub)" + "\n<:botadmin:319880143441100801> [Bot Invite]("+botInvite+")" + "\n\uD83C\uDF10 [Website](https://advance.xyz)", true);
        embedBuilder.addField("❯ Developers", "<:developer:319890977206566912> [Kieran#0273](https://twitter.com/kieranadv)\n<:developer:319890977206566912> [Ed#8133](https://twitter.com/ed_utf8)", true);
        embedBuilder.addField("❯ System", "Threads:" + Thread.activeCount() + "\nCPU Cores: " + runtime.availableProcessors() + "\nRAM: " + used+"/"+total + "MB", true);
        embedBuilder.addField("❯ Information", "⏱ Uptime - " + messages.getFormattedIntTime(inSeconds) + "\n\uD83D\uDCC6 Creation - " + MiscUtil.getCreationTime(event.getJDA().getSelfUser()).format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
        embedBuilder.setFooter("Advance", event.getJDA().getSelfUser().getAvatarUrl());

        if (message.isFromType(ChannelType.TEXT)) {
            message.getChannel().sendMessage(embedBuilder.build()).queue();
        } else if (message.isFromType(ChannelType.PRIVATE)) {
            manager.sendPrivateMessage(author, embedBuilder.build());
        }
    }

    @Override
    public String getName() {return "bot";}

    @Override
    public String getDescription() {return "General Information about the Bot";}

    @Override
    public String getHelp() {return "bot";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}
}
