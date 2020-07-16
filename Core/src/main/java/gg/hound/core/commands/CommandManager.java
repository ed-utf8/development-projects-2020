package gg.hound.core.commands;

import gg.hound.core.CorePlugin;
import gg.hound.core.commands.permissions.ListGroupsCommand;
import gg.hound.core.commands.permissions.PermissionsCommand;
import gg.hound.core.commands.permissions.PlayerOpCommand;
import gg.hound.core.commands.punishment.BanCommand;
import gg.hound.core.commands.punishment.MuteCommand;
import gg.hound.core.commands.punishments.AltsCommand;
import gg.hound.core.commands.punishment.ConsoleBanCommand;
import gg.hound.core.commands.punishment.ConsoleUnbanCommand;
import gg.hound.core.commands.punishment.ConsoleUnmuteCommand;
import gg.hound.core.commands.punishment.IPBanCommand;
import gg.hound.core.commands.punishments.IPUnbanCommand;
import gg.hound.core.commands.punishment.KickCommand;
import gg.hound.core.commands.punishments.LookupCommand;
import gg.hound.core.commands.punishments.StaffLookupCommand;
import gg.hound.core.commands.punishment.UnbanCommand;
import gg.hound.core.commands.punishment.UnmuteCommand;
import gg.hound.core.commands.staff.AdminChatCommand;
import gg.hound.core.commands.staff.AnnounceCommand;
import gg.hound.core.commands.staff.AnnounceUHCCommand;
import gg.hound.core.commands.staff.AutoMuteCommand;
import gg.hound.core.commands.staff.ClearChatCommand;
import gg.hound.core.commands.staff.GlobalRebootCommand;
import gg.hound.core.commands.staff.InfoCommand;
import gg.hound.core.commands.staff.MuteChatCommand;
import gg.hound.core.commands.staff.StaffChatCommand;
import gg.hound.core.commands.staff.StaffToggleCommand;
import gg.hound.core.commands.staff.UserInfoCommand;
import gg.hound.core.commands.troll.FakeOPCommand;
import gg.hound.core.commands.user.ColorCommand;
import gg.hound.core.commands.user.DiscordLinkCommand;
import gg.hound.core.commands.user.DisguiseCommand;
import gg.hound.core.commands.user.IgnoreCommand;
import gg.hound.core.commands.user.MessageReplyCommand;
import gg.hound.core.commands.user.PmToggleCommand;
import gg.hound.core.commands.user.ReportCommand;
import gg.hound.core.disguise.DisguiseManager;
import gg.hound.core.group.GroupManager;
import gg.hound.core.punishments.PunishmentData;
import gg.hound.core.redis.RedisManager;
import gg.hound.core.sql.SQLManager;
import gg.hound.core.tasks.PlayerCooldownTask;
import gg.hound.core.user.UserManager;
import gg.hound.core.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

public class CommandManager {

    public CommandManager(CorePlugin corePlugin, UserManager userManager, DisguiseManager disguiseManager, SQLManager sqlManager, RedisManager redisManager, PlayerCooldownTask playerCooldownTask, PunishmentData punishmentData, GroupManager groupManager, PluginUtils pluginUtils) {

        MessageReplyCommand messageReplyCommand = new MessageReplyCommand(corePlugin, userManager, pluginUtils);
        DisguiseCommand disguiseCommand = new DisguiseCommand(corePlugin, userManager, disguiseManager, sqlManager);

        register("staffchat", new StaffChatCommand(corePlugin, userManager, redisManager));
        register("announce", new AnnounceCommand(corePlugin, redisManager));
        register("announceuhc", new AnnounceUHCCommand(corePlugin, redisManager));
        register("message", messageReplyCommand);
        register("reply", messageReplyCommand);
        register("disguise", disguiseCommand);
        register("undisguise", disguiseCommand);
        register("addskin", disguiseCommand);
        register("disguisecheck", disguiseCommand);
        register("disguiselist", disguiseCommand);
        register("chatclear", new ClearChatCommand(corePlugin));
        register("report", new ReportCommand(corePlugin, userManager, playerCooldownTask, punishmentData));
        register("adminchat", new AdminChatCommand(corePlugin, userManager, redisManager));
        register("globalmute", new MuteChatCommand(corePlugin));
        register("listgroups", new ListGroupsCommand(corePlugin, groupManager));
        register("permissions", new PermissionsCommand(corePlugin, groupManager, sqlManager, userManager));
        register("globalreboot", new GlobalRebootCommand(corePlugin, redisManager));
        register("colour", new ColorCommand(corePlugin, pluginUtils));
        register("discord", new DiscordLinkCommand(corePlugin, sqlManager, userManager));
        register("pmtoggle", new PmToggleCommand(corePlugin, userManager));
        register("staffnotifications", new StaffToggleCommand(corePlugin, userManager));

        register("fakeop", new FakeOPCommand(corePlugin));
        register("opplayer", new PlayerOpCommand(corePlugin, userManager));

        register("ban", new BanCommand(corePlugin, userManager, sqlManager, punishmentData, pluginUtils));

        register("unban", new UnbanCommand(sqlManager, pluginUtils, userManager, corePlugin));

        register("mute", new MuteCommand(corePlugin, userManager, sqlManager, punishmentData, pluginUtils));
        register("unmute", new UnmuteCommand(sqlManager, pluginUtils, userManager, redisManager, corePlugin));

        register("kick", new KickCommand(userManager, corePlugin, punishmentData));

        register("lookup", new LookupCommand(corePlugin, sqlManager));
        register("ips", new AltsCommand(corePlugin, sqlManager));
        register("userinfo", new UserInfoCommand(corePlugin, sqlManager));
        register("slookup", new StaffLookupCommand(corePlugin, sqlManager));

        register("consoleban", new ConsoleBanCommand(corePlugin, sqlManager, pluginUtils, punishmentData, redisManager));
        register("consoleunban", new ConsoleUnbanCommand(sqlManager, pluginUtils, corePlugin));
        register("consoleunmute", new ConsoleUnmuteCommand(sqlManager, pluginUtils, redisManager, corePlugin));

        register("banip", new IPBanCommand(corePlugin, punishmentData, sqlManager, userManager, pluginUtils));
        register("unbanip", new IPUnbanCommand(sqlManager, pluginUtils, userManager, corePlugin));

        register("info", new InfoCommand(corePlugin, sqlManager));
        register("ignore", new IgnoreCommand(corePlugin, userManager, sqlManager));

        register("automute", new AutoMuteCommand(corePlugin, punishmentData));
    }

    private void register(String command, CommandExecutor commandExecutor) {
        Bukkit.getServer().getPluginCommand(command).setExecutor(commandExecutor);
    }
}
