package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RoleInfoCommand implements Command {

    private Messages messages;
    public RoleInfoCommand(Messages messages) {this.messages = messages;}

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        List<Role> mentionedRoles = message.getMentionedRoles();
        if (content.length() == 1 && mentionedRoles.size() == 0) {
            channel.sendMessage(author.getAsMention() + ", Mention one or more roles or type a role's name to see its info!").queue();
            return;
        }

        Guild guild = event.getGuild();
        User user = author;

        if (mentionedRoles.size() == 1) {
            channel.sendMessage(getRoleInformation(mentionedRoles.get(0), guild, author)).queue();
        } else {
            for (Role r : mentionedRoles) {
                channel.sendMessage(getRoleInformation(r, guild, user)).queue();
            }
        }

        String roleString = content;
        List<Role> retrievedRoles = guild.getRolesByName(roleString, true);

        if (retrievedRoles.size() == 0) {
            channel.sendMessage(author.getAsMention() + ", Mention one role or type the roles name to see its info!").queue();
        } else if (retrievedRoles.size() > 1) {
            channel.sendMessage(author.getAsMention() + ", Multiple roles were found... Please only mention one role.").queue();

                try {
                    Role role = retrievedRoles.get(Integer.parseInt(content) - 1);
                    channel.sendMessage(getRoleInformation(role, guild, user)).queue();
                } catch (Exception e) {
                    channel.sendMessage(author.getAsMention() + ", Invalid Argument").queue();
                }
        } else if (retrievedRoles.size() == 1) {
            channel.sendMessage(getRoleInformation(retrievedRoles.get(0), guild, user)).queue();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"role", "roledetails"};
    }

    @Override
    public String getName() {
        return "roleinfo";
    }

    @Override
    public String getDescription() {
        return "Displays a Roles Info";
    }

    @Override
    public String getHelp() {
        return "roleinfo <role name> or <@Mention Role>";
    }

    @Override
    public Category getCategory() {
        return Category.INFORMATION;
    }

    private MessageEmbed getRoleInformation(Role role, Guild guild, User user) throws Exception {
        EmbedBuilder builder = new EmbedBuilder();
        String title = "Role Info | Server Specific";
        builder.setAuthor(title, guild.getIconUrl(), guild.getIconUrl()).setColor(messages.getRandomColor());
        builder.addField("Role Name", role.getName(), true);
        builder.addField("Member # with Role", String.valueOf(guild.getMembers().stream().filter(member -> {
            boolean found = false;
            for (Role r : member.getRoles()) {
                if (r.getId().equals(role.getId())) found = true;
            }
            return found;
        }).count()), true);
        builder.addField("Creation Time", role.getCreationTime().toLocalDate().toString(), true);
        try {
            builder.addField("Hex Color", "#" + Integer.toHexString(role.getColor().getRGB()).substring(2)
                    .toUpperCase(), true);
        } catch (NullPointerException npe) {
            builder.addField("Hex Color", "#ffffff", true);
        }
        ArrayList<String> permissions = new ArrayList<>();
        role.getPermissions().forEach(permission -> permissions.add(permission.getName()));
        try {
            builder.addField("Permissions", listWithCommas(permissions), true);
        } catch (Exception ignored) {
        }
        builder.setFooter("Requested by " + user.getName() + "#" + user.getDiscriminator(), user.getAvatarUrl()).setTimestamp(Instant.now());
        return builder.build();
    }

    private String listWithCommas(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i < (strings.size() - 1)) sb.append(", ");
        }
        return sb.toString();
    }
}
