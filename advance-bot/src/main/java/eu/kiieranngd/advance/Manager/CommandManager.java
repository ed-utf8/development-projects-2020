package eu.kiieranngd.advance.Manager;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Commands.command.guildadmin.*;
import eu.kiieranngd.advance.Commands.command.botadmin.*;
import eu.kiieranngd.advance.Commands.command.economy.*;
import eu.kiieranngd.advance.Commands.command.fun.*;
import eu.kiieranngd.advance.Commands.command.gaming.*;
import eu.kiieranngd.advance.Commands.command.information.*;
import eu.kiieranngd.advance.Commands.command.miscellaneous.*;
import eu.kiieranngd.advance.Commands.command.moderation.*;
import eu.kiieranngd.advance.Commands.command.music.*;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.Utils.*;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandManager {

    private Config config;
    private Advance advance;
    private Manager manager;
    private BotUtils botUtils;
    private WebUtils webUtils;
    private Messages messages;
    private SQLManager sqlManager;
    private MusicManager musicManager;

    public CommandManager(Config config, Advance advance, Manager manager, BotUtils botUtils, WebUtils webUtils, Messages messages, SQLManager sqlManager, MusicManager musicManager) {
        this.config = config;
        this.advance = advance;
        this.manager = manager;
        this.messages = messages;
        this.sqlManager = sqlManager;
        this.botUtils = botUtils;
        this.webUtils = webUtils;
        this.musicManager = musicManager;
    }

    public ExecutorService pool = Executors.newCachedThreadPool();

    private boolean registerCommand(final Command c) {
        if (c.getName().contains(" ")) {
            throw new IllegalArgumentException("Command Name must not have spaces!");
        } else {
            advance.getCommands().put(c.getName(), c);
            return true;
        }
    }

    public void executeCommand(final Command c, final String alias, final String prefix, final MessageReceivedEvent event) {
        try {
            String content = removePrefix(alias, prefix, event);
            System.out.println("Dispatching command '" + c.getName().toLowerCase() + "' with: " + content);
            c.dispatch(event.getAuthor(), event.getTextChannel(), event.getMessage(), content, event);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private String removePrefix(final String c, final String prefix, final MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        content = content.substring(c.length() + prefix.length());
        if (content.startsWith(" "))
            content = content.substring(1);
        return content;
    }

    /*public Command getByName(String name) {
        for (Command command : advance.getCommands().values())
            if (command.getName().equalsIgnoreCase(name))
                return command;
        for (Command command : advance.getCommands().values())
            for (String alt : command.getAliases())
                if (alt.equalsIgnoreCase(name))
                    return command;
        return null;
    }

    public Command getCommandByName(String name) {
        for (Command command : advance.getCommands().values()) {
            Command cmd = getByName(name);
            if (command != null)
                return cmd;
        }
        return null;
    }*/

    public void loadCommands() {
        /* Bot Admin Commands */
        registerCommand(new AnnouncementCommand(messages, manager, advance));
        registerCommand(new BlacklistCommand(manager, sqlManager));
        registerCommand(new EvalCommand(advance, manager, messages));
        registerCommand(new GuildLeaveCommand(manager, advance.shards[advance.getSHARD_COUNT()], botUtils));
        registerCommand(new RestartCommand(manager, advance));
        registerCommand(new ShardInfoCommand(advance));
        registerCommand(new ShutdownCommand(manager, advance));
        registerCommand(new UsernameCommand(botUtils, manager));
        /* Economy Commands */
        registerCommand(new BalanceCommand(manager, messages));
        registerCommand(new PayCommand(manager, messages));
        /* Fun Commands */
        registerCommand(new AsciiCommand(messages));
        registerCommand(new CatCommand(messages));
        registerCommand(new DogCommand(messages));
        registerCommand(new EightBallCommand(manager, messages));
        registerCommand(new FMLCommand(manager));
        registerCommand(new GifCommand(config, messages));
        registerCommand(new JokeCommand(webUtils, messages));
        registerCommand(new LoveCalculatorCommand(botUtils, messages));
        registerCommand(new TextToBrickCommand());
        registerCommand(new UrbanCommand(manager));
        registerCommand(new YoMommaJokeCommand());
        /* Gaming Commands */
        registerCommand(new SteamStatusCommand(messages));
        /* Guild Admin Commands */
        registerCommand(new AdminCommand(manager, sqlManager));
        registerCommand(new ModCommand(manager, sqlManager));
        registerCommand(new PrefixCommand(manager));
        registerCommand(new RolesCommand(messages));
        /* Information Commands */
        registerCommand(new AvatarCommand(manager, messages));
        registerCommand(new BotCommand(manager, messages, advance.getStartTime()));
        registerCommand(new EmoteCommand());
        registerCommand(new GuildCommand(messages, manager));
        registerCommand(new HelpCommand(messages, manager));
        registerCommand(new InviteCommand(messages, manager));
        registerCommand(new PingCommand(manager));
        registerCommand(new RoleInfoCommand(messages));
        registerCommand(new SpeedTestCommand());
        registerCommand(new StatsCommand(messages, manager, advance));
        registerCommand(new UptimeCommand(messages, manager, advance.getStartTime()));
        registerCommand(new UserCommand(messages, manager));
        /* Miscellaneous Commands */
        //registerCommand(new AboutCommand());
        registerCommand(new BotsCommand());
        registerCommand(new LMGTFYCommand(messages));
        registerCommand(new ShortenCommand(manager, webUtils));
        registerCommand(new WeatherCommand(config, messages));
        /* Moderation Commands */
        registerCommand(new BanCommand());
        registerCommand(new KickCommand());
        //registerCommand("mute", new MuteCommand());
        registerCommand(new PurgeCommand(manager));
        //registerCommand("warn", new WarnCommand());
        /* Music Commands */
        registerCommand(new LeaveCommand(manager, messages));
        registerCommand(new NPCommand(manager, messages, musicManager));
        registerCommand(new PauseCommand(manager, messages, musicManager));
        registerCommand(new PlayCommand(manager, messages, musicManager));
        registerCommand(new QueueCommand(manager, musicManager, messages));
        registerCommand(new RepeatCommand(manager, messages, musicManager));
        registerCommand(new ReplayCommand(manager, messages, musicManager));
        registerCommand(new ShuffleCommand(manager, musicManager));
        registerCommand(new SkipCommand(manager, messages, musicManager));
        //registerCommand(new SoundcloudSearch(manager, messages, musicManager));
        registerCommand(new StopCommand(manager, messages, musicManager));
        registerCommand(new VolumeCommand(manager, messages, musicManager));
    }
}
