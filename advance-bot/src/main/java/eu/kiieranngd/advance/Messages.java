package eu.kiieranngd.advance;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.ConfigManager;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Messages {

    private List<String> messages = new ArrayList<>();
    private Random random = new Random(messages.size());

    private List<Color> colors = new ArrayList<>();

    private Advance advance;
    private Manager manager;

    public Messages(Advance advance, Manager manager) {
        this.advance = advance;
        this.manager = manager;
        loadMessages();
        loadColors();
    }

    private void loadMessages() {
        messages.add("It is certain");
        messages.add("It is decidedly so");
        messages.add("Without a doubt");
        messages.add("Yes, definitely");
        messages.add("You may rely on it");
        messages.add("As I see it, yes");
        messages.add("Most likely");
        messages.add("Outlook good");
        messages.add("Yes");
        messages.add("Signs point to yes");
        messages.add("Reply hazy, try again");
        messages.add("Ask again later");
        messages.add("Better not tell you now");
        messages.add("Cannot answer right now");
        messages.add("Concentrate and try again");
        messages.add("Don't count on it");
        messages.add("My reply is no");
        messages.add("My sources say no");
        messages.add("Outlook not so good");
        messages.add("Very doubtful");
        messages.add("My CPU says yes");
    }

    private void loadColors() {
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);
        colors.add(Color.ORANGE);
        colors.add(Color.YELLOW);
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.PINK);
    }

    public MessageEmbed getHelp(CGuild cGuild) {
        ConfigManager configManager = cGuild.getConfigManager();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#488ED7")).setDescription("Command help. For extended usage please use "+configManager.getPrefix()+"help <commandname>.");
        if (cGuild != null) {
            if (cGuild.getConfigManager() != null) {
                ///////
                StringBuilder stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.BOTADMIN) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                /*embedBuilder.addField("\uD83D\uDDA5 | Bot Admin", stringBuilder.toString(), false);*/
                ///////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.ECONOMY) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("\uD83D\uDCB3 | Economy", stringBuilder.toString(), false);
                ///////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.FUN) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("\uD83C\uDFAE | Fun", stringBuilder.toString(), false);
                ///////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.GAMING) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");
                    }
                }
                embedBuilder.addField("<:gaming:397758999912775680> | Gaming", stringBuilder.toString(), false);
                ///////
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.GUILDADMIN) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("\uD83D\uDDA5 | Guild Admin", stringBuilder.toString(), false);
                //////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.INFORMATION) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("\uD83C\uDF10 | Informative ", stringBuilder.toString(), false);
                ///////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.MODERATION) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("<:stafficon:397770225640144900> | Moderation", stringBuilder.toString(), false);
                ///////
                stringBuilder = new StringBuilder();
                for (Command command : advance.getCommands().values()) {
                    if (command.getCategory() == Category.MUSIC) {
                        stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                    }
                }
                embedBuilder.addField("\uD83C\uDFA7 | Music", stringBuilder.toString(), false);
                ///////


                Map<String, Command> commandMap = manager.getGuildCommands().get(cGuild);
                stringBuilder = new StringBuilder();
                if (commandMap != null) {
                    if (commandMap.size() > 0) {
                        for (Command command : commandMap.values()) {
                            stringBuilder.append("`").append(configManager.getPrefix()).append(command.getName()).append("`").append(" ");                        }
                        embedBuilder.addField("» Custom", stringBuilder.toString(), false);
                    }
                }
                return embedBuilder.build();
            }
        }
        return embedBuilder.setDescription("❌ Error: I couldn't load the commands!").build();
    }

    public String getRandomMessage() {
        return messages.get(random.nextInt(messages.size()));
    }
    public Color getRandomColor() { return colors.get(random.nextInt(colors.size()));}

    public String getFormattedIntTime(long seconds) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        seconds -= minutes * 60;
        minutes -= hours * 60;
        hours -= days * 24;
        StringBuilder sb = new StringBuilder();
        if (days > 0)
            sb.append(days).append(" days ");
        if (hours > 0)
            sb.append(hours).append(" hours ");
        if (minutes > 0)
            sb.append(minutes).append(" minutes ");
        if (seconds > 0)
            sb.append(seconds).append(" seconds ");
        return sb.toString().substring(0, sb.toString().length() - 1);
    }
}
