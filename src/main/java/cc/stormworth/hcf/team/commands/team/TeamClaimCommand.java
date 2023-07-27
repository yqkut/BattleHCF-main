package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.server.ServerHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TeamClaimCommand {

    public static final ItemStack SELECTION_WAND = new ItemStack(Material.GOLD_HOE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§6§lClaiming Wand");
        meta.setLore(Arrays.asList(
                "§7Select your claim corners",
                "",
                "§6[Right/Left] §eClick to select",
                "§eyour claim's corners.",
                "",
                "§6[Right] §eClick to cancel",
                "§eyour current claim selection.",
                "",
                "§6[Shift + Left] §eClick to",
                "§epurchase claim selection."

        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names = {"team claim", "t claim", "f claim", "faction claim", "fac claim"}, permission = "")
    public static void teamClaim(final Player sender) {
        Team team = Main.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (Main.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You are currently in the Warzone and can't claim land here. The Warzone ends at " + ServerHandler.WARZONE_RADIUS + ".");
            return;
        }

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

            new VisualClaim(sender, team, VisualClaimType.CREATE, false, false).draw(false);

            sender.sendMessage(ChatColor.GREEN + "Gave you a claim wand.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}