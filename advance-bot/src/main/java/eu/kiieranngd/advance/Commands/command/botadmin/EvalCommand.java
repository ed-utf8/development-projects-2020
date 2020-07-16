package eu.kiieranngd.advance.Commands.command.botadmin;

import eu.kiieranngd.advance.Advance;
import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import eu.kiieranngd.advance.Messages;
import eu.kiieranngd.advance.User.DiscordUser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by kdrew on 09/07/2017.
 */
public class EvalCommand implements Command {

    private Advance advance;
    private Manager manager;
    private Messages messages;

    public EvalCommand (Advance advance, Manager manager, Messages messages) {
        this.advance = advance;
        this.manager = manager;
        this.messages = messages;
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {

        DiscordUser discordUser = manager.getUser(event.getAuthor().getId());
        if (discordUser != null) {
            if (discordUser.isAdmin()) {

                if (content.length() == 0) {
                    event.getChannel().sendMessage("Error").queue();
                    return;
                } else {
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");

                        /* Imports */
                    try {
                        engine.eval("var imports = new JavaImporter(java.io,"
                                + "java.lang,"
                                + "java.util,"
                                + "Packages.net.dv8tion.jda.core,"
                                + "Packages.net.dv8tion.jda.core.utils,"
                                + "Packages.net.dv8tion.jda.core.entities,"
                                + "Packages.net.dv8tion.jda.core.entities.impl,"
                                + "Packages.net.dv8tion.jda.core.managers,"
                                + "Packages.net.dv8tion.jda.core.managers.impl,");

                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                        /* Put string representations */
                    engine.put("advance", advance);
                    engine.put("jda", event.getJDA());
                    engine.put("api", event.getJDA());

                    engine.put("message", event.getMessage());
                    engine.put("guild", event.getGuild());
                    engine.put("server", event.getGuild());
                    engine.put("channel", event.getChannel());
                    engine.put("tc", event.getTextChannel());
                    engine.put("pm", event.getPrivateChannel());
                    engine.put("vc", event.getMember().getVoiceState().getChannel());

                    engine.put("author", event.getAuthor());
                    engine.put("member", event.getMember());
                    engine.put("self", event.getGuild().getSelfMember().getUser());
                    engine.put("selfmem", event.getGuild().getSelfMember());

                    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
                    ScheduledFuture<?> future = service.schedule(() -> {

                            /* Initialize Objects */
                        long startExec = System.currentTimeMillis();
                        Object out;
                        EmbedBuilder eb = new EmbedBuilder()
                                .setColor(messages.getRandomColor()).setAuthor("Advance Eval", null, event.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl())
                                .setFooter("Requested by " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), event.getAuthor().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now());

                        try {
                                /* Input */
                            eb.addField("\uD83D\uDCE5 Input", "```java\n\n" + content + "```", false);

                                /* Output */
                            out = engine.eval(content);
                            eb.addField("\uD83D\uDCE4 Output", "```java\n\n" + out.toString() + "```", false);

                            /* Exception */
                        } catch (Exception ex) {
                            eb.addField("❌ Error", "```java\n\n" + ex.getMessage() + "```", false);
                        }

                            /* Timer */
                        eb.addField("⏱ Timing", System.currentTimeMillis() - startExec + " milliseconds", false);
                        event.getChannel().sendMessage(eb.build()).queue();

                        service.shutdownNow();
                    }, 0, TimeUnit.MILLISECONDS);
                }
            } else {
                event.getChannel().sendMessage(" This command is for `Bot Owners` only!").queue();
            }
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"eval", "run"};
    }

    @Override
    public String getName() {return "eval";}

    @Override
    public String getDescription() {return "Evaluating a Java Code Snippet";}

    @Override
    public String getHelp() {return "eval <Code>";}

    @Override
    public Category getCategory() {return Category.BOTADMIN;}
}
