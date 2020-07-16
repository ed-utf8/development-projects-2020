package eu.kiieranngd.advance.Commands.command.fun;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import eu.kiieranngd.advance.Manager.Manager;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FMLCommand implements Command {

    private BlockingQueue<String> items;
    private Manager manager;

    public FMLCommand(Manager manager) {
        this.manager = manager;
        this.items = new LinkedBlockingQueue<>();
    }

    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        event.getChannel().sendTyping().queue();
        if (items.size() < 20) {
            getFMLItems();
        }
        if (!items.isEmpty()) {
            try {
                String item = StringEscapeUtils.unescapeHtml4(items.take());
                if (item.length() >= 2000) {
                    item = item.substring(0, 1999);
                }
                if (event.isFromType(ChannelType.PRIVATE)) {
                    manager.sendPrivateMessage(event.getAuthor(), item +" \uD83E\uDD26");
                } else {
                    event.getMessage().getChannel().sendMessage(item +" \uD83E\uDD26").queue();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            if (event.isFromType(ChannelType.PRIVATE)) {
                manager.sendPrivateMessage(event.getAuthor(), "I'm sorry, something went wrong!");
            } else {
                event.getMessage().getChannel().sendMessage("I'm sorry, something went wrong!").queue();
            }
        }
    }

    @Override
    public String[] getAliases() {return new String[]{"fuckmylife"};}

    @Override
    public String getName() {return "fml";}

    @Override
    public String getDescription() {return "Get a random quote from fmylife.com";}

    @Override
    public String getHelp() {return "fml";}

    @Override
    public Category getCategory() {return Category.FUN;}

    private void getFMLItems() {
        try {
            Document document = Jsoup.connect("http://fmylife.com/random").timeout(30_000).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36").get();
            if (document != null) {
                Elements fmls = document.select("p.block a[href^=/article/]");
                for (Element fml : fmls) {
                    items.add(fml.text().trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}