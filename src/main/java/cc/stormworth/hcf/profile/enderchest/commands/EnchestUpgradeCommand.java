package cc.stormworth.hcf.profile.enderchest.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.enderchest.EnderchestUpgradeMenu;
import cc.stormworth.hcf.profile.enderchest.EnderchestUpgrades;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnchestUpgradeCommand {

    @Command(names = {"enderchestupgrade", "ecupgrade"}, permission = "")
    public static void enchestupgrade(Player player) {
        new EnderchestUpgradeMenu(HCFProfile.get(player)).open(player);
    }


    @Command(names = "resetenderchest", permission = "op")
    public static void resetEnderchest(Player player, @Param(name = "target", defaultValue = "self") Player target) {
        HCFProfile.get(target).setEnderchestUpgrades(new EnderchestUpgrades());
        player.sendMessage(ChatColor.YELLOW + "Reset enderchest upgrades for " + target.getName());

    }
}
