package cc.stormworth.hcf.ability;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.impl.*;
import cc.stormworth.hcf.ability.impl.Invis.Invis;
import cc.stormworth.hcf.ability.impl.blinder.Blinder;
import cc.stormworth.hcf.ability.impl.merge.MergeAbility;
import cc.stormworth.hcf.ability.impl.pocketbard.PocketBard;
import cc.stormworth.hcf.ability.impl.sacrifice.Sacrifice;
import cc.stormworth.hcf.ability.impl.tpclutch.TpClutch;
import cc.stormworth.hcf.ability.listeners.AbilityListener;
import cc.stormworth.hcf.misc.lunarclient.cooldown.CooldownManager;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public abstract class Ability implements Listener {

  @Getter
  private static final List<Ability> abilities = Lists.newArrayList();

  private String name;
  private String displayName;
  private List<String> description;
  private ItemStack item;
  private long cooldown;
  private List<String> activationMessage;
  private LCCooldown lunarcooldown;
  private boolean enabled;

  public Ability(String name, String displayName, List<String> description, ItemStack item, long cooldown) {
    this.name = name;
    this.description = description;
    this.displayName = displayName;
    this.cooldown = cooldown;
    this.item = new ItemBuilder(item).name(getDisplayName())
        .setGlowing(true)
        .setLore(description)
        .addToLore("&eType &6/abilities &eto see this info translate")
        .build();

    this.enabled = true;

    if (cooldown > 0) {
      this.lunarcooldown = new LCCooldown(CC.translate(displayName), (int) cooldown,
          TimeUnit.MILLISECONDS, item.getType());
      LunarClientAPICooldown.registerCooldown(lunarcooldown);
    }

    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
  }

  public ItemStack getItem() {
    return item.clone();
  }

  public ItemStack getItemOriginal() {
    return item;
  }

  public static void init() {
    abilities.add(new MergeAbility());
    abilities.add(new Grenade());
    //abilities.add(new Dash());
    abilities.add(new Strength());
    abilities.add(new Resistance());
    abilities.add(new Speed());
    abilities.add(new NinjaStar());
    abilities.add(new HelmetDisarmer());
    abilities.add(new Antitrapper());
    abilities.add(new RadiusAntitrap());
    abilities.add(new MedKit());
    abilities.add(new LastPearl());
    abilities.add(new WildMode());
    abilities.add(new MiniBarder());
    abilities.add(new Switcher());
    abilities.add(new TryhardTeleport());
    abilities.add(new SecondChance());
    abilities.add(new InventoryCorrupt());
    abilities.add(new KnifesGun());
    abilities.add(new PocketBard());
    abilities.add(new Blinder());
    abilities.add(new TpClutch());
    abilities.add(new Freezer());
    abilities.add(new BowTeleporter());

    abilities.add(new JumpBoost());
    abilities.add(new Regeneration());
    abilities.add(new Invis());

    abilities.add(new AntiBard());
    abilities.add(new AntiFallTrap());

    abilities.add(new EvilBless());
    abilities.add(new WitcherTrick());
    abilities.add(new NoPowers());
    abilities.add(new AntiClass());
    abilities.add(new Frankeisten());
    abilities.add(new Sacrifice());

    rCommandHandler.registerPackage(Main.getInstance(), "cc.stormworth.hcf.ability.command");
    Bukkit.getPluginManager().registerEvents(new AbilityListener(), Main.getInstance());
  }

  public abstract List<PotionEffect> getPotionEffects();

  public boolean isItem(ItemStack item) {
    ItemStack abilityItem = getItemOriginal();

    if (item == null) return false;

    if (item.getType() != abilityItem.getType()) return false;

    if (!item.hasItemMeta()) return false;

    ItemMeta abilityMeta = abilityItem.getItemMeta();
    ItemMeta itemMeta = item.getItemMeta();

    return itemMeta.hasDisplayName() && itemMeta.getDisplayName().equalsIgnoreCase(abilityMeta.getDisplayName()) &&
            (itemMeta.hasLore() == abilityMeta.hasLore()) && itemMeta.getLore().equals(abilityMeta.getLore());
  }

  public void consume(Player player) {

    for (ItemStack item : player.getInventory().getContents()) {
      if (isItem(item)) {
        if (item.getAmount() == 1) {
          player.getInventory().remove(item);
        } else {
          item.setAmount(item.getAmount() - 1);
        }
        player.updateInventory();
        break;
      }
    }
  }


  public void sendActivation(Player player) {
    String left = DurationFormatUtils.formatDurationWords(this.cooldown, true, true);
    player.sendMessage(CC.translate(""));
    player.sendMessage(CC.translate("&6&l⚔ &eYou have successfully used &6&l" + getName()));
    player.sendMessage(CC.translate("&6&l⚔ &eNow on cooldown for &6&l" + left));
    player.sendMessage(CC.translate(""));
    CooldownManager.sendCooldown(player.getUniqueId(), lunarcooldown);
  }

  public void handleAbilityRefund(Player player, String message, boolean returnItem) {
    if (returnItem) {
      player.getInventory().addItem(this.getItem().clone());
      player.updateInventory();
    }
    if (message != null) {
      player.sendMessage(message);
    }

    CooldownAPI.removeCooldown(player, getName());
  }

  public static Ability getByName(String name) {
    return abilities.stream().filter(ability -> ability.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
  }

  public static Ability getByItem(ItemStack item) {
    return  abilities.stream().filter(ability -> ability.isItem(item)).findFirst().orElse(null);
  }
}