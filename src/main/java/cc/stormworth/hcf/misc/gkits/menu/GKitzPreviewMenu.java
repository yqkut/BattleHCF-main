package cc.stormworth.hcf.misc.gkits.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.uuid.MenuBackButton;
import cc.stormworth.hcf.misc.gkits.Kit;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class GKitzPreviewMenu extends Menu {

  private Kit kit;

  @Override
  public boolean isAutoUpdate() {
    return false;
  }

  @Override
  public boolean isUpdateAfterClick() {
    return false;
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = new HashMap<>();

    buttons.put(45, new MenuBackButton(p -> {
      new SelectKitTypeMenu().openMenu(p);
    }));

    buttons.put(53, new Button() {
      @Override
      public String getName(Player player) {
        return !kit.canUse(player) ? CC.RED + "You don't own this kit" : CC.GREEN + "You have access to this kit";
      }

      @Override
      public List<String> getDescription(Player player) {
        return !kit.canUse(player) ? Collections.singletonList(CC.translate("&eAvailable for purchase at &6store.battle.rip")) : null;
      }

      @Override
      public Material getMaterial(Player player) {
        return !kit.canUse(player) ? Material.REDSTONE : Material.EMERALD;
      }
    });

    ItemStack helmet = kit.getArmour()[3];
    ItemStack chestplate = kit.getArmour()[2];
    ItemStack leggings = kit.getArmour()[1];
    ItemStack boots = kit.getArmour()[0];
    if (helmet.getType() != Material.AIR) {
      buttons.put(47, new Button() {
        @Override
        public String getName(Player player) {
          return null;
        }

        @Override
        public List<String> getDescription(Player player) {
          return null;
        }

        @Override
        public Material getMaterial(Player player) {
          return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
          return helmet;
        }
      });
    }
    if (chestplate.getType() != Material.AIR) {
      buttons.put(48, new Button() {
        @Override
        public String getName(Player player) {
          return null;
        }

        @Override
        public List<String> getDescription(Player player) {
          return null;
        }

        @Override
        public Material getMaterial(Player player) {
          return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
          return chestplate;
        }
      });
    }
    if (leggings.getType() != Material.AIR) {
      buttons.put(50, new Button() {
        @Override
        public String getName(Player player) {
          return null;
        }

        @Override
        public List<String> getDescription(Player player) {
          return null;
        }

        @Override
        public Material getMaterial(Player player) {
          return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
          return leggings;
        }
      });
    }
    if (boots.getType() != Material.AIR) {
      buttons.put(51, new Button() {
        @Override
        public String getName(Player player) {
          return null;
        }

        @Override
        public List<String> getDescription(Player player) {
          return null;
        }

        @Override
        public Material getMaterial(Player player) {
          return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
          return boots;
        }
      });
    }

    int index = 0;
    if (kit.getItems() != null) {
      for (ItemStack item : kit.getItems()) {
        if (index >= 53) {
          break;
        }
        //if (inventoryUI.getItem(index) != null) index++;
        buttons.put(index, new Button() {
          @Override
          public String getName(Player player) {
            return null;
          }

          @Override
          public List<String> getDescription(Player player) {
            return null;
          }

          @Override
          public Material getMaterial(Player player) {
            return null;
          }

          @Override
          public ItemStack getButtonItem(Player player) {
            return item;
          }
        });
        index++;
      }
    }

    for (int i = 0; i <= 53; i++) {
      if (buttons.get(i) == null) {
        buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 1));
      }
    }

    return buttons;
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return 54;
  }

  @Override
  public String getTitle(Player player) {
    return CC.YELLOW + "Preview: " + kit.getName();
  }
}