package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.User.DiscordUser;
import eu.kiieranngd.advance.Utils.BotUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by kdrew on 09/07/2017.
 */
public class GuildLeaveCommand implements Command {

    private Manager manager;
    private Advance.Shards shards;
    private BotUtils botUtils;

    public GuildLeaveCommand (Manager manager, Advance.Shards shards, BotUtils botUtils) {
        this.manager = manager;
        this.shards = shards;
        this.botUtils = botUtils;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {

                if (content.length() >= 1) {
                    message.delete().queue();
                    String cool = content;

                    shards.getGuildById(cool).leave().queue();
                    botUtils.mlogChannel("<:minusOne:323375203390586881> Successfully Left Guild: " + event.getJDA().getGuildById(String.valueOf(cool)).getName());

                } else {
                    event.getMessage().addReaction("\uD83E\uDD37").queue();
                }
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"gl", "forceleave"};}

    @Override
    public String getName() {return "guildleave";}

    @Override
    public String getDescription() {return "Force Leave a Guild by ID";}

    @Override
    public String getHelp() {return "guildleave <guild id>";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}
