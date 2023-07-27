package cc.stormworth.hcf.misc.map.killstreaks;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KillStreakMenu extends Menu {

  public KillStreakMenu() {
    this.setUpdateAfterClick(false);
    this.setAutoUpdate(false);
  }

  public String getTitle(final Player player) {
    return CC.YELLOW + "KillStreak Prizes";
  }

  public int size(final Map<Integer, Button> buttons) {
    return 27;
  }

  public Map<Integer, Button> getButtons(final Player player) {
    Map<Integer, Button> buttons = new HashMap<>();
    List<Object> streaks = Lists.newArrayList(
        Main.getInstance().getMapHandler().getKillstreakHandler().getKillstreaks());
    streaks.addAll(
        Main.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks());

    streaks.sort((first, second) -> {

      int firstNumber = first instanceof Killstreak ? ((Killstreak) first).getKills()[0]
          : ((PersistentKillstreak) first).getKillsRequired();
      int secondNumber = second instanceof Killstreak ? ((Killstreak) second).getKills()[0]
          : ((PersistentKillstreak) second).getKillsRequired();

      if (firstNumber < secondNumber) {
        return -1;
      }
      return 1;
    });

    for (Object ks : streaks) {
      String name = ks instanceof Killstreak ? ((Killstreak) ks).getName()
          : ((PersistentKillstreak) ks).getName();
      int kills = ks instanceof Killstreak ? ((Killstreak) ks).getKills()[0]
          : ((PersistentKillstreak) ks).getKillsRequired();
      ItemStack item = ks instanceof Killstreak ? ((Killstreak) ks).getItemStack()
          : ((PersistentKillstreak) ks).getItem();

      buttons.put(buttons.size(), new Button() {
        @Override
        public String getName(Player player) {
          return ChatColor.GOLD + name;
        }

        @Override
        public List<String> getDescription(Player player) {
          return CC.translate(
              Arrays.asList(CC.SEPARATOR, "&eRequired kills: &c" + kills, CC.SEPARATOR));
        }

        @Override
        public Material getMaterial(Player player) {
          return item.getType();
        }

        @Override
        public int getAmount(Player player) {
          return item.getAmount();
        }

        @Override
        public byte getDamageValue(Player player) {
          return (byte) item.getDurability();
        }
      });
    }
    buttons.put(26, new Button() {
      @Override
      public String getName(Player player) {
        return ChatColor.GOLD + "Active KillStreaks";
      }

      @Override
      public List<String> getDescription(Player player) {
        return CC.translate(
            Arrays.asList(CC.SEPARATOR, "&aClick to see the active killstreaks", CC.SEPARATOR));
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.ENCHANTED_BOOK;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
        new KillStreakActiveMenu().openMenu(player);
        playNeutral(player);
      }
    });
    return buttons;
  }
}