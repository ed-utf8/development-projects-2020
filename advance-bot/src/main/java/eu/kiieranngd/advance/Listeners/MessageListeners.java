package eu.kiieranngd.advance.Listeners;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Guilds.CGuild;
import eu.kiieranngd.advance.Manager.CommandManager;
import eu.kiieranngd.advance.Manager.ConfigManager;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.SQL.SQLManager;
import eu.kiieranngd.advance.User.DiscordUser;
import eu.kiieranngd.advance.Utils.BotUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageListeners extends ListenerAdapter {

    private SQLManager sqlManager;
    private Manager manager;
    private Advance advance;
    private BotUtils botUtils;
    private CommandManager commandManager;


    public MessageListeners(SQLManager sqlManager, Manager manager, Advance advance, BotUtils botUtils, CommandManager commandManager) {
        this.sqlManager = sqlManager;
        this.manager = manager;
        this.advance = advance;
        this.botUtils = botUtils;
        this.commandManager = commandManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final String message = event.getMessage().getContentRaw();
        String msg = event.getMessage().getContentDisplay();
        User author = event.getMessage().getAuthor();
        DiscordUser discordUser = manager.getUser(author.getId());
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

        //Log Executed Commands to Console
        if (event.getMessage().isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s]{Shard #%s}(%s)[#%s]<%s>: %s\n", timeStamp, advance.getShardId(event.getJDA()), event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), msg);
        }

        //Send private message to logging channel if a human sent it
        if (event.getMessage().isFromType(ChannelType.PRIVATE)) {
            if (author.isBot() || author.isFake()) {
                return;
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.red).setAuthor(author.getName() + "#" + author.getDiscriminator() + " (" + author.getId() + ")", null, author.getEffectiveAvatarUrl());
                embedBuilder.setDescription(msg);
                botUtils.logChannel(embedBuilder.build());
            }
        }

        if (discordUser != null) {
            if (!discordUser.isBlacklisted()) {
                if (discordUser.isDonator()) {
                    discordUser.setBalance(discordUser.getBalance() + 2);
                } else {
                    discordUser.setBalance(discordUser.getBalance() + 1);
                }
                if (event.getMessage().isFromType(ChannelType.TEXT)) {
                    sqlManager.addMessage(discordUser, manager.getGuild(event.getGuild().getId()), msg);
                }

                CGuild cGuild = manager.getGuild(event.getMessage().getGuild().getId());
                if (cGuild != null) {
                    ConfigManager configManager = cGuild.getConfigManager();
                    if (configManager != null) {

                        String prefix = configManager.getPrefix();

                        if (author.isBot() || author.isFake())
                            return;


                        if (event.getChannelType().isGuild() && !event.getGuild().isAvailable() ||
                                (event.getChannelType().isGuild() && !event.getTextChannel().canTalk()) ||
                                (!event.getChannelType().isGuild() && event.getPrivateChannel().isFake()))
                            return;

                        if (message.toLowerCase().startsWith(prefix.toLowerCase())) {
                            for (final Command c : advance.getCommands().values()) {
                                if (message.toLowerCase().startsWith(prefix.toLowerCase() + c.getName().toLowerCase() + ' ') || message.equalsIgnoreCase(prefix + c.getName())) {
                                    commandManager.executeCommand(c, c.getName(), prefix, event);
                                    return;
                                } else {
                                    for (final String alias : c.getAliases()) {
                                        if (message.toLowerCase().startsWith(prefix.toLowerCase() + alias.toLowerCase() + ' ') || message.equalsIgnoreCase(prefix + alias)) {
                                            commandManager.executeCommand(c, alias, prefix, event);
                                            return;
                                        }
                                    }
                                }
                            }
                        } else if (message.startsWith("<@" + event.getJDA().getSelfUser().getId() + ">")) {
                            for (final Command c : advance.getCommands().values()) {
                                if (message.toLowerCase().startsWith("<@" + event.getJDA().getSelfUser().getId() + "> " + c.getName().toLowerCase() + ' ') || message.equalsIgnoreCase("<@" + event.getJDA().getSelfUser().getId() + "> " + c.getName())) {
                                    commandManager.executeCommand(c, c.getName(), "<@" + event.getJDA().getSelfUser().getId() + "> ", event);
                                    return;
                                } else {
                                    for (final String alias : c.getAliases()) {
                                        if (message.toLowerCase().startsWith("<@" + event.getJDA().getSelfUser().getId() + "> " + alias.toLowerCase() + ' ') || message.equalsIgnoreCase("<@" + event.getJDA().getSelfUser().getId() + "> " + alias)) {
                                            commandManager.executeCommand(c, alias, "<@" + event.getJDA().getSelfUser().getId() + "> ", event);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}