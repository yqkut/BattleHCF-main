package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OresCommand {
    @Command(names = {"minedstats"}, permission = "")
    public static void ores(final Player sender, @Param(name = "player") final Player player) {
        HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        sender.sendMessage(ChatColor.AQUA + "Diamond mined: " + ChatColor.WHITE + hcfProfile.getDiamond());
        sender.sendMessage(ChatColor.GREEN + "Emerald mined: " + ChatColor.WHITE + hcfProfile.getEmerald());
        sender.sendMessage(ChatColor.RED + "Redstone mined: " + ChatColor.WHITE + hcfProfile.getRedstone());
        sender.sendMessage(ChatColor.GOLD + "Gold mined: " + ChatColor.WHITE + hcfProfile.getGold());
        sender.sendMessage(ChatColor.GRAY + "Iron mined: " + ChatColor.WHITE + hcfProfile.getIron());
        sender.sendMessage(ChatColor.BLUE + "Lapis mined: " + ChatColor.WHITE + hcfProfile.getLapis());
        sender.sendMessage(ChatColor.DARK_GRAY + "Coal mined: " + ChatColor.WHITE + hcfProfile.getCoal());
        sender.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
    }
}