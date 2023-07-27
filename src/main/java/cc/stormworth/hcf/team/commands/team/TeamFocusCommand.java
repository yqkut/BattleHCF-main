package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamFocusCommand {

    @Command(names = {"team focus", "t focus", "f focus", "faction focus", "fac focus"}, permission = "", async = true)
    public static void teamfocus(final Player sender, @Param(name = "team") final Team focusteam) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (focusteam == Main.getInstance().getTeamHandler().getTeam(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou cannot focus your own faction."));
            return;
        }
        if (Main.getInstance().getTeamHandler().getTeam(sender.getUniqueId()).getAllies().contains(focusteam.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou cannot focus your allies."));
            return;
        }
        if (Main.getInstance().getTeamHandler().getTeam(sender.getUniqueId()).getFocus() == focusteam.getName()) {
            sender.sendMessage(CC.translate("&cYour faction is already focus on " + focusteam.getName() + "."));
            return;
        }
        team.setFocus(focusteam.getName());
        final FormatingMessage focusmsg = new FormatingMessage(sender.getName()).color(ChatColor.GOLD).then(" set the focus to ").color(ChatColor.YELLOW).then(focusteam.getName(sender)).color(ChatColor.GOLD).command("/t i " + focusteam.getName()).tooltip(ChatColor.GREEN + "View team info");
        team.sendMessage(focusmsg);
        for (Player player : team.getOnlineMembers()) {
            WaypointManager.updateWaypoint(player, PlayerWaypointType.FOCUSED_FACTION_HOME);
        }
    }

    @Command(names = {"team unfocus", "t unfocus", "f unfocus", "faction unfocus", "fac unfocus"}, permission = "", async = true)
    public static void teamunfocus(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (Main.getInstance().getTeamHandler().getTeam(sender.getUniqueId()).getFocus() == null) {
            sender.sendMessage(CC.translate("&cYour team don't have any focus team."));
            return;
        }
        team.setFocus(null);
        team.sendMessage(CC.translate("&6Team Focus &ehas been reset by &6" + sender.getName() + "&e."));
        for (Player player : team.getOnlineMembers()) {
            WaypointManager.updateWaypoint(player, PlayerWaypointType.FOCUSED_FACTION_HOME);
        }
    }
}