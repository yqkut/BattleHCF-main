package cc.stormworth.hcf.team.commands.team.chatspy;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyListCommand {
    @Command(names = {"team chatspy list", "t chatspy list", "f chatspy list", "faction chatspy list", "fac chatspy list"}, permission = "MODPLUS")
    public static void teamChatSpyList(final Player sender) {
        final StringBuilder stringBuilder = new StringBuilder();

        HCFProfile hcfProfileOther = HCFProfile.getByUUIDIfAvailable(sender.getUniqueId());

        for (ObjectId team : hcfProfileOther.getSpyTeam()) {

            Team teamObj = Main.getInstance().getTeamHandler().getTeam(team);

            if (teamObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(teamObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the team chat of: " + stringBuilder);
    }
}