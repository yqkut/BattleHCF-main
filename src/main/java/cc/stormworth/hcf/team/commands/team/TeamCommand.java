package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names = {"team", "t", "f", "faction", "fac", "f help", "t help", "faction help", "team help"}, permission = "", requiresPlayer = true)
    public static void team(final Player sender) {

        final String[] msg = {
                " ",
                "§6§lGeneral Commands:",
                "",
                " §6» §e/f create [teamName] §f-§7 Create a new team",
                " §6» §e/f accept [teamName] §f-§7 Accept a pending invitation",
                " §6» §e/f lives add [amount] §f-§7 Irreversibly add lives to your faction",
                " §6» §e/f leave §f-§7 Leave your current team",
                " §6» §e/f home §f-§7 Teleport to your team home",
                " §6» §e/f stuck §f-§7 Teleport out of enemy territory",
                " §6» §e/f deposit [amount§7|§7all] §f-§7 Deposit money into your team balance",
                "",
                "§6§lInformation Commands:",
                "",
                " §6» §e/f who [player§7|§7teamName] §f-§7 Display team information",
                " §6» §e/f map §f-§7 Show nearby claims (identified by pillars)",
                " §6» §e/f list §f-§7 Show list of teams online (sorted by most online)",
                "",
                "§6§lCaptain Commands:",
                "",
                " §6» §e/f invite [player] §f-§7 Invite a player to your team",
                " §6» §e/f uninvite [player] §f-§7 Revoke an invitation",
                " §6» §e/f invites §f-§7 List all open invitations",
                " §6» §e/f kick [player] §f-§7 Kick a player from your team",
                " §6» §e/f claim §f-§7 Start a claim for your team",
                " §6» §e/f sethome §f-§7 Set your team's home at your current location",
                " §6» §e/f withdraw [amount] §f-§7 Withdraw money from your team's balance",
                " §6» §e/f announcement [message here] §f-§7 Set your team's announcement",
                "",
                "§6§lLeader Commands:",
                "",
                " §6» §e/f coleader [add|remove] [player] §f-§7 Add or remove a co-leader",
                " §6» §e/f captain [add|remove] [player] §f-§7 Add or remove a captain",
                " §6» §e/f revive [player] §f-§7 Revive a teammate using team lives",
                " §6» §e/f unclaim [all] §f-§7 Unclaim land",
                " §6» §e/f rename [newName] §f-§7 Rename your team",
                " §6» §e/f disband §f-§7 Disband your team",
                " "
        };
        sender.sendMessage(msg);
    }
}