package cc.stormworth.hcf.lff;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import cc.stormworth.hcf.util.number.NumberUtils;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class LFFMenu extends Menu {

  public LFFMenu() {
    setUpdateAfterClick(true);
  }

  @Override
  public String getTitle(Player player) {
    return CC.translate("&bPlease a class");
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    short orangeData = 1;
    short yellowData = 4;

    ItemBuilder glass = new ItemBuilder(Material.STAINED_GLASS_PANE, 1)
        .name(" ")
        .setGlowing(true);

    for (int i = 0; i < 9; i++) {
      buttons.put(i,
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(0, 1), Button.fromItem(glass.data(yellowData).build()));

    buttons.put(getSlot(8, 1), Button.fromItem(glass.data(orangeData).build()));

    for (int i = 0; i < 9; i++) {
      buttons.put(getSlot(i, 2),
          Button.fromItem(glass.data(NumberUtils.isEven(i) ? orangeData : yellowData).build()));
    }

    buttons.put(getSlot(1, 1), new ClassButton("Diamond", ChatColor.AQUA, Material.DIAMOND_HELMET));
    buttons.put(getSlot(2, 1), new ClassButton("Bard", ChatColor.GOLD, Material.GOLD_HELMET));
    buttons.put(getSlot(3, 1), new ClassButton("Rouge", ChatColor.GRAY, Material.CHAINMAIL_HELMET));
    buttons.put(getSlot(4, 1), new ClassButton("Archer", ChatColor.LIGHT_PURPLE, Material.LEATHER_HELMET));

    buttons.put(getSlot(5, 1), new ClassButton("Miner", ChatColor.WHITE, Material.IRON_HELMET));
    buttons.put(getSlot(6, 1),
        new ClassButton("Brewer", ChatColor.YELLOW, Material.BREWING_STAND_ITEM));

    buttons.put(getSlot(8, 1), new Button() {
      @Override
      public String getName(Player player) {
        return CC.translate("&a&lConfirm selected classes");
      }

      @Override
      public List<String> getDescription(Player player) {
        return CC.translate(Lists.newArrayList(
            "",
            "&f[&a✓&f] &aClick to confirm selected classes"
        ));
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.WOOL;
      }

      @Override
      public byte getDamageValue(Player player) {
        return 5;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {

        HCFProfile profile = HCFProfile.get(player);

        if (profile.getSelectedClasses().isEmpty()) {
          player.sendMessage(CC.translate("&cYou must select at least one class!"));
          player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
          player.closeInventory();
          return;
        }

        Players.playSoundForAll(Sound.NOTE_PLING);
        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------------"));
        Bukkit.broadcastMessage("");

        Clickable clickable = new Clickable(CC.translate("&6&l● "));

        clickable.add(player.getDisplayName(), "&7[Click to invite]",
            "/team invite " + player.getName());

        clickable.add(CC.translate(" &eIs looking to join a &6&lFaction"));

        for (Player other : Bukkit.getOnlinePlayers()) {
          clickable.sendToPlayer(other);
        }
        Bukkit.broadcastMessage(
            CC.translate("&7» &6&lClasses: &f" + profile.getSelectedClasses()));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&7&m----------------------------------------"));

        CooldownAPI.setCooldown(player, "LFF", TimeUtil.parseTimeLong("5m"));

        player.closeInventory();
      }
    });

    return buttons;
  }

  @AllArgsConstructor
  public class ClassButton extends Button {

    private String className;
    private ChatColor chatColor;
    private Material material;

    @Override
    public String getName(Player player) {
      return chatColor + ChatColor.BOLD.toString() + className + " Class";
    }

    @Override
    public List<String> getDescription(Player player) {
      return CC.translate(Lists.newArrayList(
          "",
          chatColor + "Click to " +
              (HCFProfile.get(player).getSelectedLffClasses().contains(className) ?
                  "select"
                  : "deselect") + "!"
      ));
    }

    @Override
    public Material getMaterial(Player player) {
      HCFProfile profile = HCFProfile.get(player);

      if (profile.getSelectedLffClasses().contains(className)) {
        return Material.REDSTONE_BLOCK;
      }

      return material;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      return new ItemBuilder(super.getButtonItem(player)).setGlowing(true).build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
      HCFProfile profile = HCFProfile.get(player);

      if (profile.getSelectedLffClasses().contains(className)) {
        profile.getSelectedLffClasses().remove(className);
      } else {
        profile.getSelectedLffClasses().add(className);
      }

      player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    }
  }
}