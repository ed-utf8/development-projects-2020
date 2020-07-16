package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShardInfoCommand implements Command {

    private Advance advance;

    public ShardInfoCommand (Advance advance) {
        this.advance = advance;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        List<String> headers = new ArrayList<>();
        headers.add("Shard ID");
        headers.add("Status");
        headers.add("Users");
        headers.add("Ping");
        headers.add("Guild Count");
        headers.add("Audio Connections");

        List<List<String>> table = new ArrayList<>();
        List<JDA> shards = new ArrayList<>(event.getJDA().asBot().getShardManager().getShards());
        Collections.reverse(shards);
        for (JDA jda : shards) {
            List<String> row = new ArrayList<>();
            row.add( (jda.getShardInfo().getShardId() + 1 ) +
                    (event.getJDA().getShardInfo().getShardId() == jda.getShardInfo().getShardId() ? " (current)" : ""));
            row.add(WordUtils.capitalizeFully(jda.getStatus().toString().replace("_", " ")));
            row.add(String.valueOf(jda.getUsers().size()));
            row.add(String.valueOf(jda.getPing()));
            row.add(String.valueOf(jda.getGuilds().size()));
            row.add(String.valueOf(jda.getVoiceChannels().stream().filter(vc -> vc.getMembers().contains(vc.getGuild()
                    .getSelfMember())).count()));
            table.add(row);
            if (table.size() == 20) {
                channel.sendMessage(makeAsciiTable(headers, table)).queue();
                table = new ArrayList<>();
            }
        }
        if (table.size() > 0) {
            channel.sendMessage(makeAsciiTable(headers, table)).queue();
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"si"};
    }

    @Override
    public String getName() {
        return "shardinfo";
    }

    @Override
    public String getDescription() {
        return "Returns information about shards";
    }

    @Override
    public String getHelp() {
        return "shardinfo";
    }

    @Override
    public Category getCategory() {
        return Category.BOTADMIN;
    }

    private String makeAsciiTable(java.util.List<String> headers, java.util.List<java.util.List<String>> table) {
        StringBuilder sb = new StringBuilder();
        int padding = 1;
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0;
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length();
            }
        }
        for (java.util.List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }
        sb.append("```").append("prolog").append("\n");
        StringBuilder formatLine = new StringBuilder("║");
        for (int width : widths) {
            formatLine.append(" %-").append(width).append("s ║");
        }
        formatLine.append("\n");
        sb.append(appendSeparatorLine("╔", "╦", "╗", padding, widths));
        sb.append(String.format(formatLine.toString(), headers.toArray()));
        sb.append(appendSeparatorLine("╠", "╬", "╣", padding, widths));
        for (java.util.List<String> row : table) {
            sb.append(String.format(formatLine.toString(), row.toArray()));
        }
        sb.append(appendSeparatorLine("╚", "╩", "╝", padding, widths));
        sb.append("```");
        return sb.toString();
    }

    private String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(StringUtils.repeat("═", size + padding * 2));
            } else {
                ret.append(middle).append(StringUtils.repeat("═", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }

}
