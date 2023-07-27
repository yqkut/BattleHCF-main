package cc.stormworth.hcf.commands.particles;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.particles.ParticleRunnable;
import org.bukkit.entity.Player;

public class ParticlesCommand {

  @Command(names = {"battleparticles"}, permission = "op")
  public static void battleparticles(Player player) {
    player.sendMessage(CC.translate("&cThis command is currently disabled."));
    /*HCFProfile profile = HCFProfile.get(player);

    profile.setParticles(!profile.isParticles());
    player.sendMessage(CC.translate(
        "&6&lParticles &ehas been " + (profile.isParticles() ? "&aenabled" : "&cdisabled") + "&e!"));*/
  }

  @Command(names = {"toggleparticles"}, permission = "DEVELOPER")
  public static void removeParticles(Player player) {
    ParticleRunnable.setEnable(!ParticleRunnable.isEnable());
    player.sendMessage(CC.translate(
        "&eAll particles have been " + (ParticleRunnable.isEnable() ? "&aenable" : "&cdisable")
            + "&e!"));
  }

}