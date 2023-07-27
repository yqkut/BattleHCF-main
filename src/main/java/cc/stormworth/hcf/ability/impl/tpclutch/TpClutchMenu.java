package cc.stormworth.hcf.ability.impl.tpclutch;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Teleport;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class TpClutchMenu extends Menu {

  private final Team team;

  public TpClutchMenu(Team team) {
    this.team = team;
    setAutoUpdate(true);
  }

  @Override
  public String getTitle(Player player) {
    return "&aSelect a friend to teleport";
  }

  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    for (Player target : team.getOnlineMembers()) {
      if (target != player && target != null) {
        buttons.put(buttons.size(), new PlayerButton(target));
      }
    }

    return buttons;
  }

  @RequiredArgsConstructor
  public class PlayerButton extends Button {

    private final Player target;

    @Override
    public String getName(Player player) {
      return ChatColor.GREEN + target.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
      return null;
    }

    @Override
    public Material getMaterial(Player player) {
      return Material.SKULL_ITEM;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
      return new ItemBuilder(Material.SKULL_ITEM)
              .data((short) 3)
              .setSkullOwner(target.getName())
              .name(ChatColor.GREEN + target.getName())
              .addToLore("", CC.translate("&7Click to teleport!"))
              .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

      HCFProfile profile = HCFProfile.get(player);
      Teleport teleport = new Teleport(player, player.getLocation(), target.getLocation(), 10);

      teleport.setOnTeleport((other) -> other.addPotionEffect(
          new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 1)));

      teleport.setUuid(target.getUniqueId());
      teleport.setAbility(Ability.getByName("TpClutch"));
      profile.setTeleport(teleport);


      profile.setCountdown(teleport.start());

      player.sendMessage(CC.translate("&eTeleport to your faction member in &6&l10 seconds..."));
      player.sendMessage(CC.translate("&8[&c⚠&8]&c Stay still and don't take any damage"));
      player.sendMessage("");

      player.getNearbyEntities(50, 50, 50).stream().filter(entity -> entity instanceof Player)
          .forEach(entity -> {
            Player nearbyPlayer = (Player) entity;

            if (nearbyPlayer.getUniqueId().equals(player.getUniqueId())) {
              return;
            }

            nearbyPlayer.sendMessage(CC.translate("&c&l" + player.getName() + " &eis being teleported to their teammate!"));

          });

      player.closeInventory();
      CooldownAPI.setCooldown(player, "TpClutch", TimeUtil.parseTimeLong("8m"), "&aYou can now use " + Ability.getByName("TpClutch").getDisplayName() + "&a again.");
      consume(player);
    }
  }

  private void consume(Player player) {

    ItemStack item = player.getInventory().getItemInHand();

    if (item.getAmount() == 1) {
      player.getInventory().remove(item);
    } else {
      item.setAmount(item.getAmount() - 1);
    }

    player.updateInventory();
  }
}