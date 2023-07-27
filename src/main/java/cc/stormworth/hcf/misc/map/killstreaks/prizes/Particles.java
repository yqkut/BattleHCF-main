package cc.stormworth.hcf.misc.map.killstreaks.prizes;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.killstreaks.PersistentKillstreak;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Particles extends PersistentKillstreak {

  public Particles() {
    super("Particles", 150, new ItemStack(Material.DIAMOND_BOOTS));
  }

  @Override
  public void apply(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    profile.setParticlesBattle(true);
    player.setMetadata("removeParticles", new FixedMetadataValue(Main.getInstance(), true));
  }
}