package cc.stormworth.hcf.commands.staff;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetEnderpearlCommand {

  @Command(names = {"resetenderpearl", "resetpearl"}, permission = "op")
  public static void resetEnderpearlCommand(final CommandSender sender,
      @Param(name = "player") final Player player) {
    CooldownAPI.removeCooldown(player, "enderpearl");
  }
}