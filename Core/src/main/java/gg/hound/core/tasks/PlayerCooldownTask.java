package gg.hound.core.tasks;

import gg.hound.core.user.CoreUser;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCooldownTask extends BukkitRunnable {

    private ConcurrentHashMap<CoreUser, Integer> chatCooldown;
    private ConcurrentHashMap<CoreUser, Integer> reportCooldown;

    public PlayerCooldownTask() {
        this.chatCooldown = new ConcurrentHashMap<>();
        this.reportCooldown = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {

        if (chatCooldown.size() > 0) {
            for (Map.Entry<CoreUser, Integer> chats : chatCooldown.entrySet()) {
                if (chats.getValue() > 0)
                    chats.setValue(chats.getValue() - 1);
                else
                    chatCooldown.remove(chats.getKey());
            }
        }

        if (reportCooldown.size() > 0) {
            for (Map.Entry<CoreUser, Integer> reports : reportCooldown.entrySet()) {
                if (reports.getValue() > 0)
                    reports.setValue(reports.getValue() - 1);
                else
                    reportCooldown.remove(reports.getKey());
            }
        }

    }

    public void addChatCooldown(CoreUser coreUser) {
        chatCooldown.put(coreUser, 2);
    }

    public void addReportCooldown(CoreUser coreUser) {
        reportCooldown.put(coreUser, 20);
    }

    public boolean hasReportCooldown(CoreUser coreUser) {
        return reportCooldown.containsKey(coreUser);
    }

    public boolean hasChatCooldown(CoreUser coreUser) {
        return chatCooldown.containsKey(coreUser);
    }
}
