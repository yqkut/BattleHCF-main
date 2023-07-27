package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.VisualClaim;
import cc.stormworth.hcf.team.claims.VisualClaimType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TeamResizeCommand {
    public static final ItemStack SELECTION_WAND;

    static {
        SELECTION_WAND = new ItemStack(Material.STONE_HOE);
        final ItemMeta meta = TeamResizeCommand.SELECTION_WAND.getItemMeta();
        meta.setDisplayName("§c§lResize Wand");
        meta.setLore(Arrays.asList(
                "§7Resize your current selection",
                "",
                "§6[Right/Left] §eClick to resize",
                "§eyour current claim selection.",
                "",
                "§6[Right] §eClick to cancel",
                "§eyour current claim selection.",
                "",
                "§6[Shift + Left] §eClick to",
                "§epurchase claim selection."

        ));
        TeamResizeCommand.SELECTION_WAND.setItemMeta(meta);
    }

    @Command(names = {"team resize", "t resize", "f resize", "faction resize", "fac resize"}, permission = "op")
    public static void teamResize(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }
        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            sender.getInventory().remove(TeamResizeCommand.SELECTION_WAND);
            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not resize land while your faction is raidable!");
                return;
            }

            new VisualClaim(sender, team, VisualClaimType.RESIZE, false, false).draw(false);
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }
}
