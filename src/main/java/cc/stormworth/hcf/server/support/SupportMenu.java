package cc.stormworth.hcf.server.support;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.util.support.PartnerFaces;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class SupportMenu extends Menu {

  boolean adminview = false;

  public SupportMenu() {
    this.setAutoUpdate(false);
    this.setUpdateAfterClick(true);
  }

  public Map<Integer, Button> getButtons(final Player player) {
    Map<Integer, Button> buttons = new HashMap<>();
    if (player.isOp()) {
      buttons.put(0, new Button() {
        @Override
        public String getName(Player player) {
          return SupportMenu.this.adminview ? CC.translate("&cDisable Adminview")
              : CC.translate("&aEnable Adminview");
        }

        @Override
        public List<String> getDescription(Player player) {
          return SupportMenu.this.adminview ? Collections.singletonList(CC.translate("&7Click to disable it"))
              : Collections.singletonList(CC.translate("&7Click to enable it"));
        }

        @Override
        public Material getMaterial(Player player) {
          return SupportMenu.this.adminview ? Material.QUARTZ : Material.FEATHER;
        }

        public void clicked(Player player, int slot, ClickType clickType) {
          if (player.isOp()) {
            playNeutral(player);
            SupportMenu.this.adminview = !SupportMenu.this.adminview;
          }
        }
      });
    }

    int slot = 10;
    int index = 0;

    List<PartnerFaces> partnerFaces = Arrays.asList(PartnerFaces.values());

    while (slot < 54 - 10 && partnerFaces.size() > index) {
      PartnerFaces partnerFace = partnerFaces.get(index);

      buttons.put(slot++, new SupportButton(partnerFace, this.adminview));

      index++;

      if ((slot - 8) % 9 == 0) {
        slot += 2;
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
    return CC.GOLD + "Support";
  }
}