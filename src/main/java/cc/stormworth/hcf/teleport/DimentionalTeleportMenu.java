package cc.stormworth.hcf.teleport;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.util.number.NumberUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DimentionalTeleportMenu extends Menu {

  @Override
  public String getTitle(Player player) {
    return "&5&lDimensional Teleport";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    for (int i = 0; i < 9; i++) {
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(orangeData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(2, 1), Button.fromItem(new ItemBuilder(Material.ENDER_PORTAL_FRAME)
            .name("&5&lEnder Portal &7(Teleport)")
            .setGlowing(true)
            .addToLore(
                "",
                "&7Teleport to the end where you can farm",
                "&7pearls and lead teamfights against the void.",
                "",
                "&5[Click here]&7 to teleport to the end"
            )
            .build(), (other) -> {

          other.closeInventory();

          other.teleport(Bukkit.getWorld("world_the_end").getSpawnLocation());

          other.sendMessage(CC.translate("&eYou have been teleported to the &6&lEnd World"));
        }
    ));

    buttons.put(getSlot(6, 1), Button.fromItem(new ItemBuilder(Material.QUARTZ_ORE)
        .name("&4&lNether &7(Teleport)")
        .setGlowing(true)
        .addToLore(
            "",
            "&7Teleport to the nether where lava reigns",
            "&7the surroundings and running in most",
            "&7common thing to do here.",
            "",
            "&4[Click here]&7 to teleport to the nether"
        )
        .build(), (other) -> {

      other.closeInventory();
      other.teleport(Bukkit.getWorld("world_nether").getSpawnLocation());

      other.sendMessage(CC.translate("&eYou have been teleported to the &6&lEnd World"));
    }));

    return buttons;
  }
}