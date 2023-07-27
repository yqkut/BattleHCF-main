package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.server.support.SupportMenu;
import cc.stormworth.hcf.util.support.PartnerFaces;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SupportCommand {

  @Command(names = {"support",
      "redeem"}, description = "Pick up your favorite creator", permission = "")
  public static void support(final Player sender) {

    if(Main.getInstance().getMapHandler().isKitMap()){
      sender.sendMessage(CC.translate("&cThis command is not allowed on Kits."));
      return;
    }

    new SupportMenu().openMenu(sender);
  }

  @Command(names = {
      "support list"}, hidden = true, description = "Check top creator", permission = "op", async = true)
  public static void list(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis command can only be used on HCF."));
      return;
    }
    sender.sendMessage(CC.translate("&6&l&m-------------&f&l-----------&6&l-----------"));
    for (PartnerFaces partner : PartnerFaces.values()) {
      UUID uuid = UUID.fromString(partner.getUuid());
      String finalname = UUIDUtils.name(uuid);
      sender.sendMessage(
          ChatColor.YELLOW + finalname + " - §6" + Main.getInstance().getCreatorsCountMap()
              .getVotes(uuid) + " Votes.");
    }
    sender.sendMessage(CC.translate("&6&l&m-------------&f&l-----------&6&l-----------"));
  }

  @Command(names = {"support wipevotes"}, hidden = true, permission = "op", async = true)
  public static void wipevotes(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis command can only be used on HCF."));
      return;
    }
    Main.getInstance().getCreatorsCountMap().wipeVals();
    Main.getInstance().getSupportedMap().wipeVals();
    sender.sendMessage(CC.translate("&aSuccessfully wiped all votes!"));
  }

  @Command(names = {"support reset"}, permission = "op", async = true)
  public static void reset(final Player sender, @Param(name = "target") Player target) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis command can only be used on HCF."));
      return;
    }
    Main.getInstance().getSupportedMap().setSupported(target.getName(), false);
    sender.sendMessage(CC.translate("&aSuccessfully reset support to " + target.getName()));
  }
}