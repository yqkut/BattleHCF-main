package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.enderchest.EnderchestUpgrades;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.util.Utils;
import org.bukkit.entity.Player;

public class EnderChestCommand {

    @Command(names = {"enderchest", "ec"}, permission = "", requiresPlayer = true, async = true)
    public static void enderchest(final Player sender, @Param(name = "target", defaultValue = "self") Player target) {

        EnderchestUpgrades enderchestUpgrades = HCFProfile.get(sender).getEnderchestUpgrades();

        Profile profile = Profile.getByUuidIfAvailable(sender.getUniqueId());

        if (!enderchestUpgrades.isCanUse() && profile.getRank().isBelow(Rank.HERO)) {
            sender.sendMessage(CC.translate("&cYou can't use your enderchest. You can buy this upgrade in &6/enderchestupgrade"));
            return;
        }

        if (CorePlugin.getInstance().getShutdownTask() != null && CorePlugin.getInstance().getShutdownTask().isEnabled() && CorePlugin.getInstance().getShutdownTask().getSecondsUntilShutdown() <= 5) {
            sender.sendMessage(CC.RED + "You cannot use enderchest events while a reboot in process.");
            return;
        }

        if (Utils.isEventLocated(sender, true)) {
            sender.sendMessage(CC.RED + "You cannot use the enderchest command while your team is in the event.");
            return;
        }

        if (!Profile.getByUuidIfAvailable(sender.getUniqueId()).getRank().isAboveOrEqual(Rank.ADMINISTRATOR) && target != sender) {
            sender.openInventory(sender.getEnderChest());
            return;
        }

        if(SpawnTagHandler.isTagged(sender) && !enderchestUpgrades.isCanUseInCombat() && profile.getRank().isBelow(Rank.HERO)) {
            sender.sendMessage(CC.translate("&cYou can't use your enderchest while tagged. You can buy this upgrade in &6/enderchestupgrade"));
            return;
        }

        sender.openInventory(target.getEnderChest());
    }
}