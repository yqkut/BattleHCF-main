package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.menu.base.CreateBaseMenu;
import cc.stormworth.hcf.util.workload.types.TeamWorkdLoadType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static cc.stormworth.hcf.team.commands.team.TeamClaimCommand.SELECTION_WAND;

public class TeamBaseCommand {

    @Command(names = "togglebase" , permission = "op")
    public static void toggle(Player player){
        VisualClaim.base = !VisualClaim.base;
        player.sendMessage(CC.translate("&eBase claim mode is now " + (VisualClaim.base ? "&aenabled" : "&cdisabled") + "&e."));
    }

    @Command(names = "base", permission = "")
    public static void base(Player sender){

        if (!VisualClaim.base){
            sender.sendMessage(CC.translate("&cCommand is disabled."));
            return;
        }


        Profile coreProfile = Profile.getByPlayer(sender);

        /*if (coreProfile.getRank().isBelow(Rank.BATTLE) && !sender.hasPermission("hcf.command.base")){
            sender.sendMessage(CC.translate("&eYou must be a &6&lBattle Rank &eto access this command."));
            return;
        }*/

        HCFProfile profile = HCFProfile.get(sender);
        if(profile == null) return;
        if (profile.getTeam()  == null){
            sender.sendMessage(CC.translate("&cYou are not in a team."));
            return;
        }

        Team team = profile.getTeam();

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

            int slot = -1;

            for (int i = 0; i < 9; i++) {
                if (sender.getInventory().getItem(i) == null) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
                return;
            }

            if (team.getWorkloadRunnables().containsKey(TeamWorkdLoadType.BASE)){
                sender.sendMessage(CC.translate("&cYour base is already in progress!"));
                return;
            }

            if (team.isUseBase()){
                sender.sendMessage(CC.translate("&cYour base is already created!"));
                return;
            }

            new CreateBaseMenu(team).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}
