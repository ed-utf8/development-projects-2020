package eu.kiieranngd.advance;

import com.mashape.unirest.http.*;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;

import eu.kiieranngd.advance.Commands.Command;

import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Listeners.*;
import eu.kiieranngd.advance.Manager.*;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.Utils.*;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Advance {

    private JDA jda;
    private long startTime;

    public Shards[] shards;
    private int totalShards;

    private Config config;
    private Manager manager;
    private Messages messages;
    private BotUtils botUtils;
    private WebUtils webUtils;
    private SQLManager sqlManager;
    private MusicManager musicManager;
    private CommandManager commandManager;

    private List<JDA> jdaClients;
    private Map<Integer, JDA> shardMap = new HashMap<>();

    private Map<String, Command> commands = new HashMap<>();
    public Map<String, Command> getCommands() {return commands;}
    public void addCommand(String commandString, Command command) {
        commands.put(commandString, command);
    }

    public static void main(String[] args) {new Advance();}

    private Advance() {
        config = new Config();
        manager = new Manager();
        webUtils = new WebUtils(config);
        messages = new Messages(this, manager);
        botUtils = new BotUtils(this);
        sqlManager = new SQLManager(manager, this, musicManager);
        musicManager = new MusicManager();
        commandManager = new CommandManager(config, this, manager, botUtils, webUtils, messages, sqlManager, musicManager);

        totalShards = getRecommendedShards(config.getToken());

        if (sqlManager.fail()) {
            return;
        }
        startBot();
    }

    public void restartBot() {
        for (JDA jda : jdaClients) {
            jda.shutdown();
        }
        sqlManager.updateData();
        startBot();
    }

    public void shutdownBot() {
        sqlManager.updateData();
        for (JDA jda : jdaClients) {
            jda.shutdown();
        }
    }

    private void startBot() {
        String token = config.getBetaToken();
        int SHARD_COUNT = getRecommendedShards(token);
        jdaClients = new ArrayList<>(SHARD_COUNT);
        startTime = System.currentTimeMillis();

        try {
            if (SHARD_COUNT > 1) {
                for (int i = 0; i < SHARD_COUNT; i++) {
                    System.out.println("[Advance] Starting Shard #" + i + " of " + SHARD_COUNT);
                    JDA jda = new JDABuilder(AccountType.BOT)
                            .setToken(token).setStatus(OnlineStatus.DO_NOT_DISTURB).setGame(Game.watching("Initializing..."))
                            .addEventListener(new MessageListeners(sqlManager, manager, this, botUtils, commandManager),
                                              new BotListeners(sqlManager, manager, messages, jdaClients, this, botUtils, musicManager, commandManager))
                            .useSharding(i, SHARD_COUNT).setAutoReconnect(true).setAudioEnabled(true).setAudioSendFactory(new NativeAudioSendFactory()).buildBlocking();
                    jdaClients.add(jda);
                    shardMap.put(i, jda);
                    System.out.println("[Advance] Finished loading Shard #" + i);
                }
            } else {
                System.out.println("[Advance] Starting Shard #0 of " + SHARD_COUNT);
                jdaClients.add(new JDABuilder(AccountType.BOT)
                        .setToken(token).setStatus(OnlineStatus.DO_NOT_DISTURB).setGame(Game.watching("Initializing..."))
                        .addEventListener(new MessageListeners(sqlManager, manager, this, botUtils, commandManager),
                                          new BotListeners(sqlManager, manager, messages, jdaClients, this, botUtils, musicManager, commandManager))
                        .setAutoReconnect(true).setAudioEnabled(true).setAudioSendFactory(new NativeAudioSendFactory()).buildBlocking());
                System.out.println("[Advance] Finished loading Shard #0");
            }
            shards = new Shards[totalShards];
            botUtils.changeBotStatusStream("advance.xyz");
            //sendStats();

        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        commandManager.loadCommands();
        sqlManager.loadCommands();
        manager.setGuilds(sqlManager.getGuilds());

        for (CGuild cGuild : manager.getGuilds()) {
            if (cGuild != null) {
                for (JDA j : jdaClients) {
                    if (j != null) {
                        for (Member member : j.getGuildById(cGuild.getGuildID()).getMembers()) {
                            if (manager.getUser(member.getUser().getId()) == null) {
                                sqlManager.loadUser(member.getUser().getId(), cGuild.getGuildID());
                            }
                        }
                    }
                }
                sqlManager.loadCommands(cGuild);
            }
        }

        while (true) {
            try {
                sqlManager.updateData();
                TimeUnit.MINUTES.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getRecommendedShards(String token) {
        try {
            HttpResponse<JsonNode> request = Unirest.get("https://discordapp.com/api/gateway/botadmin")
                    .header("Authorization", "Bot " + token)
                    .header("Content-Type", "application/json")
                    .asJson();
            return Integer.parseInt(request.getBody().getObject().get("shards").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private void sendStats() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String botID = getSelfId();
        String DBotsID = "";
        String DListOrgID = "";

        JSONObject data = new JSONObject();
        data.put("server_count", getTotalGuilds());
        if (getSHARD_COUNT() > 1) { data.put("shard_count", getSHARD_COUNT()); }
        Unirest.post("https://bots.discord.pw/api/bots/"+ botID + "/stats")
                .header("Authorization", DBotsID)
                .header("Content-Type", "application/json")
                .body(data.toString())
                .asJsonAsync();
        Unirest.post("https://discordbots.org//api/bots/"+ botID + "/stats")
                .header("Authorization", DListOrgID)
                .header("Content-Type", "application/json")
                .body(data.toString())
                .asJsonAsync();

        System.out.println("Stats have been pushed to DiscordBots.org and bots.Discord.pw");
        botUtils.logChannel(embedBuilder.setColor(Color.GREEN).setDescription("**Pixl | Stats have been PUSHED** \uD83D\uDD25 \uD83C\uDF89").build());
    }

    public long getStartTime() {return startTime;}

    private Shards[] getShards() {return shards;}

    public int getShardId(JDA jda) {
        if (jda.getShardInfo() == null) return 0;
        return jda.getShardInfo().getShardId();
    }
    public Shards getShard(JDA jda) {return getShards()[getShardId(jda)];}
    public int getShardId(long discordGuildId) {return (int) ((discordGuildId >> 22) % totalShards); }
    public List<Shards> getShardList() {return Arrays.asList(getShards());}

    public int getSHARD_COUNT() {
        return shardMap.size();
    }
    public List<JDA> getBots() {
        return jdaClients;
    }

    public long getTotalUsers() {
        long total = 0;
        for (JDA jda : jdaClients)
            for (Guild guild : jda.getGuilds())
                total += guild.getMembers().size();
        return total;
    }

    private String getSelfId() {
        String Id = String.valueOf(0);
        for (JDA jda : jdaClients) {
            Id = jda.getSelfUser().getId();
        }
        return Id;
    }

    public long getTotalBots() {
        long total = 0;
        for (JDA jda : jdaClients)
            for (Guild guild : jda.getGuilds())
                total += guild.getMembers().stream().filter((u) -> (u.getUser().isBot())).count();
        return total;
    }

    public long getTotalGuilds() {
        long total = 0;
        for (JDA jda : jdaClients)
                total += jda.getGuilds().size();
        return total;
    }

    public long getTotalTextChannels() {
        long total = 0;
        for (JDA jda : jdaClients)
            for (Guild guild : jda.getGuilds())
                total += guild.getTextChannels().size();
        return total;
    }

    public long getTotalVoiceChannels() {
        long total = 0;
        for (JDA jda : jdaClients)
            for (Guild guild : jda.getGuilds())
                total += guild.getVoiceChannels().size();
        return total;
    }

    public class Shards {

        public JDA getJDA() {return jda;}

        public Guild getGuildById(String guildId) {
            for (JDA j : jdaClients) {
                Guild g;
                if ((g = j.getGuildById(guildId)) != null) {
                    return g;
                }
            }
            return null;
        }
    }
}