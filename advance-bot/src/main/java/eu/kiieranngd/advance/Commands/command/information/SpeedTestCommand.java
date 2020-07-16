package eu.kiieranngd.advance.Commands.command.information;

import eu.kiieranngd.advance.Commands.Category;
import eu.kiieranngd.advance.Commands.Command;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class SpeedTestCommand implements Command {
    @Override
    public void dispatch(User author, TextChannel channel, Message message, String content, MessageReceivedEvent event) throws Exception {
        SpeedTestSocket DSpeed = new SpeedTestSocket();
        SpeedTestSocket USpeed = new SpeedTestSocket();
        StringBuilder sb = new StringBuilder();
        message.delete().queue();
        Message msg = message.getTextChannel().sendMessage(new EmbedBuilder().setDescription("**Speedtest started...**\n\nTesting downstream...").build()).complete();
        DSpeed.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                sb.append("Downstream:  " + Math.round(report.getTransferRateBit().floatValue() / 1024 / 1024) + " MBit/s\n");
                msg.editMessage(new EmbedBuilder().setDescription("**Speedtest starting...**\n\nTesting upstream...").build()).queue();
                USpeed.startUpload("http://2.testdebit.info/", 1000000);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
            }

            @Override
            public void onError(SpeedTestError speedTestError, String s) {
                System.out.println(speedTestError);
            }

        });
        USpeed.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                sb.append("Upstream:    " + Math.round(report.getTransferRateBit().floatValue() / 1024 / 1024) + " MBit/s");
                msg.editMessage(new EmbedBuilder().setColor(new Color(72, 244, 66)).setDescription("**Test finished.**\n\n```" + sb.toString() + "```").build()).queue();
            }

            @Override
            public void onProgress(float v, SpeedTestReport speedTestReport) {

            }

            @Override
            public void onError(SpeedTestError speedTestError, String s) {
                System.out.println(speedTestError);
            }

        });

        DSpeed.startDownload("http://2.testdebit.info/10M.iso");
    }

    @Override
    public String[] getAliases() {
        return new String[]{"botspeed", "st"};
    }

    @Override
    public String getName() {
        return "speedtest";
    }

    @Override
    public String getDescription() {
        return "Do a speedtest of the botadmin-connection.";
    }

    @Override
    public String getHelp() {
        return "speedtest";
    }

    @Override
    public Category getCategory() {
        return Category.INFORMATION;
    }
}
