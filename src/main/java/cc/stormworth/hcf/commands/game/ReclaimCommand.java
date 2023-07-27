package cc.stormworth.hcf.commands.game;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.MapHandler;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.ParticleMeta;
import cc.stormworth.hcf.util.ParticleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ReclaimCommand {

  @Command(names = {"reclaim", "claim"}, permission = "")
  public static void execute(final Player player) {
    HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
    if (hcfProfile.isReclaimed()) {
      player.sendMessage(ChatColor.RED + "You have already reclaimed this map.");
      return;
    }
    final Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());
    if (!MapHandler.getReclaims().containsKey(profile.getRank())) {
      player.sendMessage(
          ChatColor.RED + "It appears there is no reclaim found for your rank.");
      return;
    }
    for (final String command : MapHandler.getReclaims().get(profile.getRank())) {

      Main.getInstance().getServer()
          .dispatchCommand(Main.getInstance().getServer().getConsoleSender(),
              command.replace("%playername%", player.getDisplayName())
                  .replace("%player%", player.getName()));
    }
    hcfProfile.setReclaimed(true);

        /*online.sendMessage(CC.translate(
            "&7* " + profile.getRank().getColor() + player.getName() + " &fhas claimed his "
                + profile.getRank().getColor() + profile.getRank().getName()
                + " &fprizes &7(/reclaim)")));*/

    Main.getInstance().getServer().getOnlinePlayers().forEach(online -> online.sendMessage(
        CC.translate("&4&l♥ &6&l" + player.getName() + " &ehas just redeemed their "
            + profile.getRank().getColor() + profile.getRank().getName()
            + " Perks &6&l➠ &7(/claim)"
        )));

    new TitleBuilder(
            "&a&lPerks Claimed!",
            "&7&oThanks for supporting us!",
            5,
            20,
            5
    ).send(player);

    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

    for (int i = 0; i < 5; i++) {
      createParticleRing(player, 2.5);
      createParticleRing(player, 1.5);
      createParticleRing(player, 0);
    }
  }

  public static void createParticleRing(Player player, double y){

    double size = 1;

    for (int i = 0; i < 360; i++) {
      double angle = (i * Math.PI / 180);
      double x = size * Math.cos(angle);
      double z = size * Math.sin(angle);
      Location loc = player.getLocation().add(x, y, z);

      ParticleUtil.sendParticle(player, new ParticleMeta(loc, "flame", 0, 0, 0, 0, 1));
    }
  }
}