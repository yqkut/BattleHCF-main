package cc.stormworth.hcf.misc.rewards;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.core.util.inventory.InventorySerialization;
import cc.stormworth.hcf.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@Getter
@Setter
public class RewardsManager {

  private ItemStack[] rewards = new ItemStack[54];
  private final ConfigFile rewardsYML;

  public RewardsManager() {
    rewardsYML = new ConfigFile(Main.getInstance(), "rewards.yml");
    if (rewardsYML.getConfig().contains("rewards")) {
      try {
        rewards = InventorySerialization.itemStackArrayFromBase64(
            rewardsYML.getConfig().getString("rewards"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    Bukkit.getPluginManager().registerEvents(new RewardsListener(), Main.getInstance());
  }

  public void save() {
    rewardsYML.getConfig().set("rewards", InventorySerialization.itemStackArrayToBase64(rewards));
    rewardsYML.save();
  }

}