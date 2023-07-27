package cc.stormworth.hcf.team.commands.lives;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LivesReviveCommand {

  @Command(names = {"pvp revive", "lives revive", "pvptimer revive", "timer revive", "pvp revive",
      "pvptimer revive", "timer revive", "pvp revive"}, permission = "")
  public static void pvpRevive(final Player sender, @Param(name = "player") final UUID player) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a HCF only command."));
      return;
    }

    if (Main.getInstance().getServerHandler().isPreEOTW()) {
      sender.sendMessage(ChatColor.RED + "You may not use lives while &4EOTW &cis active!");
      return;
    }

    HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());
    int lives = hcfProfile.getLives();

    if (lives <= 0) {
      sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
      return;
    }

    HCFProfile targetProfile = HCFProfile.getByUUID(player);

    if (targetProfile == null) {

      CompletableFuture<HCFProfile> future = HCFProfile.load(player);

      future.thenAccept(profileTarget -> {

        if (profileTarget == null) {
          sender.sendMessage(ChatColor.RED + "Player not found!");
          return;
        }

        if (!profileTarget.isDeathBanned()) {
          sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
          return;
        }

        hcfProfile.removeLives(1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "!");

        profileTarget.setDeathban(null);
        profileTarget.asyncSave();
      });

      return;
    }

    if (!targetProfile.isDeathBanned()) {
      sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
      return;
    }

    hcfProfile.removeLives(1);
    sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "!");
    targetProfile.getDeathban().revive(player);
  }

  @Command(names = {"uselive"}, permission = "")
  public static void uselive(final Player sender) {
    if (Main.getInstance().getMapHandler().isKitMap()) {
      sender.sendMessage(CC.translate("&cThis is a HCF only command."));
      return;
    }

    if (Main.getInstance().getServerHandler().isEOTW()) {
      sender.sendMessage(ChatColor.RED + "You may not use lives while &4EOTW &cis active!");
      return;
    }

    HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());

    if (hcfProfile.isDeathBanned()) {

      if (hcfProfile.getLives() > 0) {

        hcfProfile.removeLives(1);

        hcfProfile.getDeathban().revive(sender.getUniqueId());

        sender.sendMessage(CC.translate("&eYou have used a life! You now have &6" + hcfProfile.getLives() + " &elives&e."));
        return;
      }

      sender.sendMessage(CC.translate("&cYou have &l" + hcfProfile.getLives() + " lives&c."));
      sender.sendMessage(CC.translate("&cPurchase lives at &lstore.battle.rip."));
    } else {
      if (DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
        if (sender.getLocation().getWorld().getName().equalsIgnoreCase("void")){
          if (SpawnTagHandler.isTagged(sender)) {
            SpawnTagHandler.removeTag(sender);
          }

          sender.getInventory().clear();
          sender.getOpenInventory().getTopInventory().clear();
          sender.getInventory().setArmorContents(null);

          TaskUtil.run(Main.getInstance(), () -> {
            sender.teleport(Main.getInstance().getServerHandler().getSpawnLocation());
          });

          if (sender.hasMetadata("deathban")) {
            sender.removeMetadata("deathban", Main.getInstance());
          }

          Utils.removeThrownPearls(sender);
        }
      }
    }
  }
}