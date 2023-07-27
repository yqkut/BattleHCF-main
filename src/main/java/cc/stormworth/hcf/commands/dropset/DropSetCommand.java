package cc.stormworth.hcf.commands.dropset;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class DropSetCommand {

  @Command(names = {"dropset"}, permission = "")
  public static void dropset(Player player, @Param(name = "target") Player target) {

    if (target == player) {
      player.sendMessage(CC.translate("&cYou cannot drop set yourself."));
      return;
    }

    if (player.getLocation().distance(target.getLocation()) > 8) {
      player.sendMessage(
          CC.translate("&cYou are too far away from " + target.getName() + " to dropset."));
      return;
    }

    Clickable clickable = new Clickable(
        "&6&l" + player.getName()
            + " &7(/dropset) &ewants to preview  their inventory &eso you can peacefully leave him. &7(Click here)",
        "Click here to preview inventory",
        "/_dropsetpreview " + player.getName());

    clickable.sendToPlayer(target);
    target.playSound(target.getLocation(), Sound.NOTE_PLING, 1, 1);
    target.setMetadata("dropset", new FixedMetadataValue(Main.getInstance(), player.getName()));

    player.sendMessage(
        CC.translate("&eYou have sent a request to &6&l" + target.getName()
            + " &eto drop set peacefully."));
  }

  @Command(names = {"_dropsetpreview"}, permission = "")
  public static void dropsetpreview(Player player, @Param(name = "target") Player target) {
    if (!player.hasMetadata("dropset")) {
      player.sendMessage(CC.translate("&cThat player has not sent you a request to drop set."));
      return;
    }

    if (!player.getMetadata("dropset").get(0).asString().equals(target.getName())) {
      player.sendMessage(CC.translate("&cThat player has not sent you a request to drop set."));
      return;
    }

    new ViewPlayerMenu(target).openMenu(player);

    player.removeMetadata("dropset", Main.getInstance());
  }
}