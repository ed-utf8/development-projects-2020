package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.time.format.DateTimeFormatter;

/**
 * Created by kdrew on 09/07/2017.
 */
public class UserCommand implements Command {

    private Messages messages;
    private Manager manager;

    public UserCommand(Messages messages, Manager manager) {
        this.messages = messages;
        this.manager = manager;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        if (content.length() == 0) {
            if (!event.isFromType(ChannelType.PRIVATE)) {
                DiscordUser discordUser = manager.getUser(author.getId());


                if (discordUser != null) {
                    Member member = event.getGuild().getMember(author);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(messages.getRandomColor())
                            .setThumbnail(event.getAuthor().getAvatarUrl())
                            .setAuthor(event.getAuthor().getName(), "https://discord.io/advance", event.getAuthor().getAvatarUrl())
                            .addField("Info", "Name: " + author.getAsMention() + "\n" + "Discriminator: #" + author.getDiscriminator(), true)
                            .addField("IDs", "ID: " + author.getId() + "\n" + "Database ID: " + String.valueOf(discordUser.getId()), true)
                            .addField("Avatar", "[Avatar Link](" + author.getAvatarUrl() + ")", true);

                    Game game = member.getGame();
                    if (game != null) {
                        String gameString = (game.getType() == Game.GameType.STREAMING ? "<:streaming:319890838870032384> " : "\uD83C\uDFB2 ") + game.getName();
                        embedBuilder.addField("Status", "User Status: " + userStatus(member.getOnlineStatus()) + "\nGame Status: " + gameString, true);
                    } else {
                        embedBuilder.addField("Status", "User Status: " + userStatus(member.getOnlineStatus()), true);
                    }

                    boolean hasRole = !event.getGuild().getMember(author).getRoles().isEmpty();
                    if (hasRole) {
                        StringBuilder sb = new StringBuilder();
                        for (Role role : member.getRoles()) {
                            sb.append("").append(role.getAsMention()).append(' ');
                        }
                        embedBuilder.addField("Roles", sb.toString() + "@everyone", true);
                    } else {
                        embedBuilder.addField("Roles", "@everyone", true);
                    }
                    embedBuilder.addField("Created On", MiscUtil.getCreationTime(event.getAuthor().getIdLong()).format(DateTimeFormatter.RFC_1123_DATE_TIME), true);
                    event.getMessage().getChannel().sendMessage(embedBuilder.build()).queue();                }
            }
        } else {
            User user = message.getMentionedUsers().get(0);
            DiscordUser discordUser = manager.getUser(user.getId());

            if (discordUser != null) {
                Member member = event.getGuild().getMember(user);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(messages.getRandomColor())
                        .setThumbnail(user.getAvatarUrl())
                        .setAuthor(user.getName(), "https://discord.io/advance", user.getAvatarUrl())
                        .addField("Info", "Name: " + user.getAsMention() + "\n" + "Discriminator: #" + user.getDiscriminator(), true)
                        .addField("IDs", "ID: " + user.getId() + "\n" + "Database ID: " + String.valueOf(discordUser.getId()), true)
                        .addField("Avatar", "[Avatar Link](" + user.getAvatarUrl() + ")", true);
                Game game = member.getGame();
                if (game != null) {
                    String gameString = (game.getType() == Game.GameType.STREAMING ? "<:streaming:319890838870032384> " : "\uD83C\uDFB2 ") + game.getName();
                    embedBuilder.addField("Status", "User Status: " + userStatus(member.getOnlineStatus()) + "\nGame Status: " + gameString, true);
                } else {
                    embedBuilder.addField("Status", "User Status: " + userStatus(member.getOnlineStatus()), true);
                }

                boolean hasRole = !event.getGuild().getMember(user).getRoles().isEmpty();
                if (hasRole) {
                    StringBuilder sb = new StringBuilder();
                    for (Role role : member.getRoles()) {
                        sb.append("").append(role.getAsMention()).append(' ');
                    }
                    embedBuilder.addField("Roles", sb.toString() + "@everyone", true);
                } else {
                    embedBuilder.addField("Roles", "@everyone", true);
                }
                embedBuilder.addField("Created On", MiscUtil.getCreationTime(user.getIdLong()).format(DateTimeFormatter.RFC_1123_DATE_TIME), true);

                event.getMessage().getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"userinfo", "member", "ui"};}

    @Override
    public String getName() {return "user";}

    @Override
    public String getDescription() {return "Get information from our database about a user";}

    @Override
    public String getHelp() {return "user | user @MENTION";}

    @Override
    public Category getCategory() {return Category.INFORMATION;}

    private String userStatus (OnlineStatus status) {
        if (status == OnlineStatus.ONLINE) return "Online <:online:397756451155738624>";
        if (status == OnlineStatus.IDLE) return "Idle <:away:397756480679313410>";
        if (status == OnlineStatus.DO_NOT_DISTURB) return "Do Not Disturb <:dnd:397756494550007809>";
        if (status == OnlineStatus.OFFLINE) return "Offline <:offline:397756536358699018>";
        if (status == OnlineStatus.INVISIBLE) return "Invisable <:invisible:397756547851354122>";
        return "Unknown Status. Are they even a Person?";
    }
}
