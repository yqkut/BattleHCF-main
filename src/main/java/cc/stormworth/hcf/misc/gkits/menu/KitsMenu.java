package cc.stormworth.hcf.misc.gkits.menu;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.GlowGlassButton;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.core.uuid.MenuBackButton;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.misc.gkits.KitType;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.Utils;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.ChatPaginator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class KitsMenu extends Menu {

  public final int finalSlots = Main.getInstance().getMapHandler().isKitMap() ? 45 : 54;
  public final int[] glassslots =
      finalSlots == 54 ? new int[]{0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53}
          : new int[]{0, 1, 7, 8, 9, 17, 27, 35, 36, 37, 43, 44};

  private final KitType kitType;

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
    HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());
    Map<Integer, Button> buttons = new HashMap<>();

    for (int slot : glassslots) {
      buttons.put(slot, new GlowGlassButton((byte) 1));
    }

    for (Kit kit : Main.getInstance().getKitManager().getKits()) {
      if (!kit.isInMenu()) {
        continue;
      }

      if (kit.getType() != kitType) {
        continue;
      }

      List<String> description = Lists.newArrayList();

      int minPlaytime = (int) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis());
      int playtimeTime = (int) TimeUnit.MILLISECONDS.toSeconds(profile.getTotalPlayTime());

      if (kit.canUse(player)) {
        if (kit.isEnabled()) {
          description.add("");
          description.add(CC.translate("&7&m---------------------"));
          if (kit.getDescription() != null) {
            for (final String part : ChatPaginator.wordWrap(kit.getDescription(), 24)) {
              description.add(
                  ChatColor.YELLOW + "⁕" + ChatColor.GOLD + " Description" + ChatColor.GOLD
                      + " ➟ " + ChatColor.WHITE + CC.translate(part));
            }
          }
          if (!profile.canUseKit(kit)) {
            if (kit.getMaxUses() > 0) {
              description.add(
                  ChatColor.YELLOW + "⁕" + ChatColor.GOLD + " Uses" + ChatColor.GOLD + " ➟ "
                      + ChatColor.WHITE
                      + profile.getKitUses(kit) + "/" + kit.getMaxUses());
            } else {
              final long remaining = profile.getRemainingKitCooldown(kit);
              long millisLeft = remaining - System.currentTimeMillis();
              String msg = TimeUtils.formatIntoDetailedString((int) millisLeft / 1000);
              description.add(
                  ChatColor.YELLOW + "⁕" + ChatColor.GOLD + " Cooldown" + ChatColor.GOLD + " ➟ "
                      + ChatColor.WHITE + msg);
            }
          } else {
            if (kit.getMaxUses() > 0) {
              description.add(ChatColor.GOLD + "Uses" + ChatColor.GOLD + " » " + ChatColor.WHITE
                  + profile.getKitUses(kit) + "/" + kit.getMaxUses());
            }
            if (kit.getDelayMillis() > 0L) {
              description.add(
                  ChatColor.YELLOW + "⁕" + ChatColor.GOLD + " Cooldown" + ChatColor.GOLD + " ➟ "
                      + ChatColor.WHITE
                      + kit.getDelayWords());
            }
          }

          if (kit.getMinPlaytimeMillis() != 0 && playtimeTime < minPlaytime) {
            description.add(CC.translate("&cYou need minimum " + TimeUtils.formatIntoHHMMSS(
                    (int) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis())) + " playtime"));
          }

          description.add(CC.translate("&c"));
          description.add(CC.translate("&7&l(&6!&7) &6Right click to preview"));
          description.add(CC.translate("&7&m---------------------"));
          description.add("");
        } else {
          description.add("");
          description.add(CC.translate("&7&m---------------------"));
          description.add(ChatColor.RED + "This kit is currently disabled");
          description.add(CC.translate("&7&m---------------------"));
          description.add("");
        }
      } else {
        if (kit.isEnabled()) {
          description.add("");
          description.add(CC.translate("&7&m---------------------"));
          description.add(CC.translate("&cYou don't own this kit"));
          if (kit.getMinPlaytimeMillis() != 0 && playtimeTime < minPlaytime) {
            description.add(CC.translate("&cYou need minimum " + TimeUtils.formatIntoHHMMSS(
                (int) TimeUnit.MILLISECONDS.toSeconds(kit.getMinPlaytimeMillis())) + " playtime"));
          } else {
            description.add(CC.translate("&cPurchase at &e&ostore.battle.rip"));
          }
          description.add(CC.translate("&c"));
          description.add(CC.translate("&7(&6!&7) &6Right click to preview"));
          description.add(CC.translate("&7&m---------------------"));
          description.add("");
        } else {
          description.add("");
          description.add(CC.translate("&7&m---------------------"));
          description.add(ChatColor.RED + "This kit is currently disabled");
          description.add(CC.translate("&7&m---------------------"));
          description.add("");
        }
      }
      buttons.put((int) (kit.getSlot() - 1), new Button() {
        @Override
        public String getName(Player player) {
          return CC.GREEN + kit.getName();
        }

        @Override
        public List<String> getDescription(Player player) {
          return description;
        }

        @Override
        public Material getMaterial(Player player) {
          return kit.getImage().getType();
        }

        @Override
        public byte getDamageValue(Player player) {
          return (byte) kit.getImage().getDurability();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
          if (EOTWCommand.isFfaEnabled()) {
            player.closeInventory();
            return;
          }
          if (Utils.isEventLocated(player, true)) {
            player.sendMessage(
                CC.RED + "You cannot use this while in warzone and your team is in the event.");
            player.closeInventory();
            return;
          }
          if (clickType == ClickType.RIGHT) {
            if (kit.isEnabled() && kit.isInMenu()) {
              player.closeInventory();
              new GKitzPreviewMenu(kit).openMenu(player);
            }
          } else {
            if (kit.canUse(player)) {
              kit.applyTo(player, false, true);
            }
          }
        }
      });
    }

    if (!Main.getInstance().getMapHandler().isKitMap()) {
      buttons.put(49, new MenuBackButton(p -> new SelectKitTypeMenu().openMenu(player)));
    }

    return buttons;
  }

  @Override
  public int size(Map<Integer, Button> buttons) {
    return finalSlots;
  }

  @Override
  public String getTitle(Player player) {
    return CC.YELLOW + "Gkits";
  }
}