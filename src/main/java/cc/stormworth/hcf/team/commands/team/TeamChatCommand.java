package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.chat.ChatMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatCommand {
    @Command(names = {"team chat", "t chat", "f chat", "faction chat", "fac chat", "team c", "t c", "f c", "faction c", "fac c", "mc"}, permission = "")
    public static void teamChat(final Player sender, @Param(name = "chat mode", defaultValue = "toggle") final String chatMode) {
        ChatMode parsedChatMode = null;
        if (chatMode.equalsIgnoreCase("t") || chatMode.equalsIgnoreCase("team") || chatMode.equalsIgnoreCase("f") || chatMode.equalsIgnoreCase("fac") || chatMode.equalsIgnoreCase("faction") || chatMode.equalsIgnoreCase("fc")) {
            parsedChatMode = ChatMode.TEAM;
        } else if (chatMode.equalsIgnoreCase("g") || chatMode.equalsIgnoreCase("p") || chatMode.equalsIgnoreCase("global") || chatMode.equalsIgnoreCase("public") || chatMode.equalsIgnoreCase("gc")) {
            parsedChatMode = ChatMode.PUBLIC;
        } else if (chatMode.equalsIgnoreCase("a") || chatMode.equalsIgnoreCase("allies") || chatMode.equalsIgnoreCase("ally") || chatMode.equalsIgnoreCase("alliance") || chatMode.equalsIgnoreCase("ac")) {
            parsedChatMode = ChatMode.ALLIANCE;
        } else if (chatMode.equalsIgnoreCase("captain") || chatMode.equalsIgnoreCase("officer") || chatMode.equalsIgnoreCase("o") || chatMode.equalsIgnoreCase("c") || chatMode.equalsIgnoreCase("oc")) {
            parsedChatMode = ChatMode.OFFICER;
        }
        setChat(sender, parsedChatMode);
    }

    @Command(names = {"fc", "tc"}, permission = "")
    public static void fc(final Player sender) {
        setChat(sender, ChatMode.TEAM);
    }

    @Command(names = {"gc", "pc"}, permission = "")
    public static void gc(final Player sender) {
        setChat(sender, ChatMode.PUBLIC);
    }

    @Command(names = {"oc"}, permission = "")
    public static void oc(final Player sender) {
        setChat(sender, ChatMode.OFFICER);
    }

    private static void setChat(final Player player, final ChatMode chatMode) {
        HCFProfile hcfProfile = HCFProfile.get(player);

        if (chatMode != null) {
            final Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);
            if (chatMode != ChatMode.PUBLIC) {
                if (playerTeam == null) {
                    player.sendMessage(ChatColor.RED + "You must be on a team to use this chat mode.");
                    return;
                }
                if (chatMode == ChatMode.OFFICER && !playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be an officer or above in your team to use this chat mode.");
                    return;
                }
            }
            switch (chatMode) {
                case PUBLIC: {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;
                }
                case ALLIANCE: {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in alliance chat.");
                    break;
                }
                case TEAM: {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat.");
                    break;
                }
                case OFFICER: {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in officer chat.");
                    break;
                }
            }
           hcfProfile.setChatMode(chatMode);
        } else {
            switch (hcfProfile.getChatMode()) {
                case PUBLIC: {
                    final Team team = Main.getInstance().getTeamHandler().getTeam(player);
                    final boolean teamHasAllies = team != null && team.getAllies().size() > 0;
                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
                    break;
                }
                case ALLIANCE: {
                    setChat(player, ChatMode.TEAM);
                    break;
                }
                case TEAM: {
                    final Team team2 = Main.getInstance().getTeamHandler().getTeam(player);
                    final boolean isOfficer = team2 != null && (team2.isCaptain(player.getUniqueId()) || team2.isCoLeader(player.getUniqueId()) || team2.isOwner(player.getUniqueId()));
                    setChat(player, isOfficer ? ChatMode.OFFICER : ChatMode.PUBLIC);
                    break;
                }
                case OFFICER: {
                    setChat(player, ChatMode.PUBLIC);
                    break;
                }
            }
        }
    }
}
