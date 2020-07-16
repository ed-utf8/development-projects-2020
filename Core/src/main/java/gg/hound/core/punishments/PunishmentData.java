package gg.hound.core.punishments;

import gg.hound.core.util.PluginUtils;
import gg.hound.core.util.Report;
import org.bukkit.ChatColor;

import java.util.*;

public class PunishmentData {

    private final PluginUtils pluginUtils;

    private final HashMap<String, Reason> ipBanReasons;
    private final HashMap<String, Reason> banReasons;
    private final HashMap<String, Reason> muteReasons;
    private final HashMap<String, Reason> reportReasons;
    private final HashMap<String, Reason> kickReasons;

    private final List<AutomutePattern> automutePatterns;

    private final Map<UUID, Punishment> ipbanMap = new HashMap<>();
    private final List<UUID> otheripBan = new ArrayList<>();

    private final Map<UUID, Punishment> banMap = new HashMap<>();
    private final List<UUID> otherBan = new ArrayList<>();

    private final Map<UUID, Punishment> muteMap = new HashMap<>();
    private final List<UUID> otherMute = new ArrayList<>();

    private final Map<UUID, Punishment> kickMap = new HashMap<>();
    private final List<UUID> otherKick = new ArrayList<>();

    private final Map<UUID, Report> reportMap = new HashMap<>();
    private final List<UUID> otherReport = new ArrayList<>();

    private final Map<UUID, Long> automutedPlayers = new HashMap<>();
    private final List<String> automute = new ArrayList<>();

    public PunishmentData(PluginUtils pluginUtils) {
        this.pluginUtils = pluginUtils;
        ipBanReasons = new HashMap<>();
        banReasons = new HashMap<>();
        muteReasons = new HashMap<>();
        reportReasons = new HashMap<>();
        kickReasons = new HashMap<>();
        automutePatterns = new ArrayList<>();
    }

    public HashMap<String, Reason> getBanReasons() {
        return banReasons;
    }

    public Reason getBanReason(String string) {
        for (Reason reason : banReasons.values()) {
            if (ChatColor.stripColor(reason.getDisplayName()).equalsIgnoreCase(string))
                return reason;
        }
        return null;
    }

    public Reason getBanReason(int id) {
        for (Reason reason : banReasons.values()) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public HashMap<String, Reason> getIPBanReasons() {
        return ipBanReasons;
    }

    public Reason getIPBanReason(String string) {
        for (Reason reason : ipBanReasons.values()) {
            if (reason.getDisplayName().equals(string))
                return reason;
        }
        return null;
    }

    public Reason getIPBanReason(int id) {
        for (Reason reason : ipBanReasons.values()) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public HashMap<String, Reason> getMuteReasons() {
        return muteReasons;
    }

    public Reason getMuteReason(String string) {
        for (Reason reason : muteReasons.values()) {
            if (ChatColor.stripColor(reason.getDisplayName()).equalsIgnoreCase(string))
                return reason;
        }
        return null;
    }

    public Reason getMuteReason(int id) {
        for (Reason reason : muteReasons.values()) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public HashMap<String, Reason> getKickReasons() {
        return kickReasons;
    }

    public Reason getKickReason(String string) {
        for (Reason reason : kickReasons.values()) {
            if (ChatColor.stripColor(reason.getDisplayName()).equalsIgnoreCase(string))
                return reason;
        }
        return null;
    }

    public Reason getKickReason(int id) {
        for (Reason reason : kickReasons.values()) {
            if (reason.getId() == id)
                return reason;
        }
        return null;
    }

    public HashMap<String, Reason> getReportReasons() {
        return reportReasons;
    }

    public List<AutomutePattern> getAutomutePatterns() {
        return automutePatterns;
    }

    public Punishment getIPBan(UUID uuid) {
        return ipbanMap.get(uuid);
    }

    public void addIPBan(UUID uuid, Punishment ipban) {
        ipbanMap.put(uuid, ipban);
    }

    public void removeIPBan(UUID uuid) {
        ipbanMap.remove(uuid);
    }

    public void addOtherIPBan(UUID uuid) {
        otheripBan.add(uuid);
    }

    public void removeOtherIPBan(UUID uuid) {
        otheripBan.remove(uuid);
    }

    public List<UUID> getOtherIPBan() {
        return otheripBan;
    }

    public Punishment getBan(UUID uuid) {
        return banMap.get(uuid);
    }

    public void addBan(UUID uuid, Punishment ban) {
        banMap.put(uuid, ban);
    }

    public void removeBan(UUID uuid) {
        banMap.remove(uuid);
    }

    public void addOtherBan(UUID uuid) {
        otherBan.add(uuid);
    }

    public void removeOtherBan(UUID uuid) {
        otherBan.remove(uuid);
    }

    public List<UUID> getOtherBan() {
        return otherBan;
    }

    public Punishment getMute(UUID uuid) {
        return muteMap.get(uuid);
    }

    public void addMute(UUID uuid, Punishment mute) {
        muteMap.put(uuid, mute);
    }

    public void removeMute(UUID uuid) {
        muteMap.remove(uuid);
    }

    public void addOtherMute(UUID uuid) {
        otherMute.add(uuid);
    }

    public void removeOtherMute(UUID uuid) {
        otherMute.remove(uuid);
    }

    public List<UUID> getOtherMute() {
        return otherMute;
    }

    public Punishment getKick(UUID uuid) {
        return kickMap.get(uuid);
    }

    public void addKick(UUID uuid, Punishment mute) {
        kickMap.put(uuid, mute);
    }

    public void removeKick(UUID uuid) {
        kickMap.remove(uuid);
    }

    public void addOtherKick(UUID uuid) {
        otherKick.add(uuid);
    }

    public void removeOtherKick(UUID uuid) {
        otherKick.remove(uuid);
    }

    public List<UUID> getOtherKick() {
        return otherKick;
    }

    public Report getReport(UUID uuid) {
        return reportMap.get(uuid);
    }

    public void addReport(UUID uuid, Report report) {
        reportMap.put(uuid, report);
    }

    public void removeReport(UUID uuid) {
        reportMap.remove(uuid);
    }

    public void addOtherReport(UUID uuid) {
        otherReport.add(uuid);
    }

    public void removeOtherReport(UUID uuid) {
        otherReport.remove(uuid);
    }

    public List<UUID> getOtherReport() {
        return otherReport;
    }

    public void addWord(String word) {
        automute.add(word);
    }

    public boolean containsWord(String word) {
        for (String words : automute) {
            if (word.contains(words))
                return true;
        }
        return false;
    }

    public void removeWord(String word) {
        automute.remove(word);
    }

    public String getAutomutedWords() {
        if (automute.size() == 0)
            return ChatColor.RED + "No words are currently auto-muted.";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < automute.size(); i++) {
            stringBuilder.append(ChatColor.GRAY);
            stringBuilder.append("\u2022 ");
            stringBuilder.append(ChatColor.RED);
            stringBuilder.append(automute.get(i));
            if (i != automute.size() - 1)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void addAutomutedPlayer(UUID uuid) {
        automutedPlayers.put(uuid, pluginUtils.currentTime());
    }

    public boolean isPlayerAutomuted(UUID uuid) {
        if (!automutedPlayers.containsKey(uuid))
            return false;

        if (pluginUtils.currentTime() - automutedPlayers.get(uuid) >= 1000 * 60 * 5) {
            automutedPlayers.remove(uuid);
            return false;
        }
        return true;
    }

    public void removeAutomutedPlayer(UUID uuid) {
        automutedPlayers.remove(uuid);
    }


}
