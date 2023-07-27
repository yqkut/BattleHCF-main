package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.menu.falltrap.FalltrapMenu;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamFalltrapCommand {

    @Command(names = "falltrap", permission = "")
    public static void base(Player sender){

        if (!VisualClaim.base){
            sender.sendMessage(CC.translate("&cCommand is disabled."));
            return;
        }

        Profile coreProfile = Profile.getByPlayer(sender);

        /*if (coreProfile.getRank().isBelow(Rank.BATTLE) && !sender.hasPermission("hcf.command.falltrap")){
            sender.sendMessage(CC.translate("&eYou must be a &6&lBattle Rank &eto access this command."));
            return;
        }*/

        HCFProfile profile = HCFProfile.get(sender);

        if (profile.getTeam() == null){
            sender.sendMessage(CC.translate("&cYou are not in a team."));
            return;
        }

        Team team = profile.getTeam();

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())) {

            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.FALL_TRAP)){
                sender.sendMessage(CC.translate("&cYour falltrap is already in progress!"));
                return;
            }

            new FalltrapMenu(team).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}
