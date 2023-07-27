package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.kt.util.TimeUtils;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.entity.Player;

public class EnderpearlCommand {

  @Command(names = {"pearlcd set"}, permission = "op")
  public static void enderPearlAdd(final Player sender) {
    long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(
        sender.getLocation()) ? TimeUtils.parseTimeToLong("30s")
        : TimeUtils.parseTimeToLong("16s");

    CooldownAPI.setCooldown(sender, "enderpearl", timeToApply);
  }

  @Command(names = {"pearlcd remove"}, permission = "op")
  public static void enderPearlRemove(final Player sender) {
    CooldownAPI.removeCooldown(sender, "enderpearl");
  }
}
