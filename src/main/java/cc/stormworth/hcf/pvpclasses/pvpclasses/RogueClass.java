package cc.stormworth.hcf.pvpclasses.pvpclasses;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.deathmessage.DeathMessageHandler;
import cc.stormworth.hcf.deathmessage.objects.PlayerDamage;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.util.RestoreEffect;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RogueClass extends PvPClass {

  private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
  private static final Map<String, Long> lastJumpUsage = new HashMap<>();
  private static final Map<String, Long> backstabCooldown = new HashMap<>();
  public static PotionEffect ROGUE_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 4);
  public static PotionEffect ROGUE_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 7);

  public RogueClass() {
    super("Rogue", Arrays.asList(Material.SUGAR, Material.FEATHER), 2);
  }

  @Override
  public boolean qualifies(final PlayerInventory armor) {
    return this.wearingAllArmor(armor)
        && armor.getHelmet().getType() == Material.CHAINMAIL_HELMET
        && armor.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE
        && armor.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS
        && armor.getBoots().getType() == Material.CHAINMAIL_BOOTS;
  }

  @Override
  public void apply(final Player player) {
    super.apply(player);
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    player.addPotionEffect(
        new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
  }

  @Override
  public void tick(final Player player) {
    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    }
    if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
      player.addPotionEffect(
          new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
    }
  }

  @Override
  public void remove(final Player player) {
    super.remove(player);


    Map<cc.stormworth.hcf.util.Effect, RestoreEffect> entry = Main.getInstance().getEffectRestorer().getRestores().row(player.getUniqueId());

    for (Iterator<RestoreEffect> it = entry.values().iterator(); it.hasNext();) {
      RestoreEffect restore = it.next();
      PotionEffect potionEffect = restore.getEffect();

      if (potionEffect.getType().getName().equals("SPEED") && potionEffect.getAmplifier() == 2) {
        it.remove();
      } else if (potionEffect.getType().getName().equals("JUMP") && potionEffect.getAmplifier() == 1) {
        it.remove();
      } else if (potionEffect.getType().getName().equals("DAMAGE_RESISTANCE") && potionEffect.getAmplifier() == 1) {
        it.remove();
      }
    }

    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
      if(activePotionEffect.getAmplifier() == 2 && activePotionEffect.getType().equals(PotionEffectType.SPEED)) {
        player.removePotionEffect(activePotionEffect.getType());
      }

      if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.JUMP)) {
        player.removePotionEffect(activePotionEffect.getType());
      }

      if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
        player.removePotionEffect(activePotionEffect.getType());
      }
    }
  }

  @Override
  public boolean itemConsumed(final Player player, final Material material) {
    if (material == Material.SUGAR) {
      if (RogueClass.lastSpeedUsage.containsKey(player.getName())
          && RogueClass.lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
        final Long millisLeft =
            (RogueClass.lastSpeedUsage.get(player.getName()) - System.currentTimeMillis())
                / 1000L
                * 1000L;
        final String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000L));
        player.sendMessage(
            ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
        return false;
      }
      RogueClass.lastSpeedUsage.put(player.getName(),
          System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
      player.addPotionEffect(RogueClass.ROGUE_SPEED_EFFECT, true);
    } else {
      if (RogueClass.lastJumpUsage.containsKey(player.getName())
          && RogueClass.lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
        final Long millisLeft =
            (RogueClass.lastJumpUsage.get(player.getName()) - System.currentTimeMillis())
                / 1000L
                * 1000L;
        final String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000L));
        player.sendMessage(
            ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
        return false;
      }
      RogueClass.lastJumpUsage.put(player.getName(),
          System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
      player.addPotionEffect(RogueClass.ROGUE_JUMP_EFFECT, true);
    }
    return true;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDamage(final EntityDamageByEntityEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
      final Player damager = (Player) event.getDamager();
      final Player victim = (Player) event.getEntity();
      if (damager.getItemInHand() != null
          && damager.getItemInHand().getType() == Material.GOLD_SWORD
          && PvPClassHandler.hasKitOn(
          damager, this)) {

        if (RogueClass.backstabCooldown.containsKey(damager.getName())
            && RogueClass.backstabCooldown.get(damager.getName())
            > System.currentTimeMillis()) {
          return;
        }

        if (CooldownAPI.hasCooldown(damager, "anti_class")) {
          return;
        }

        RogueClass.backstabCooldown.put(damager.getName(),
            System.currentTimeMillis() + 1500L);

        final Vector playerVector = damager.getLocation().getDirection();
        final Vector entityVector = victim.getLocation().getDirection();
        playerVector.setY(0.0f);
        entityVector.setY(0.0f);
        final double degrees = playerVector.angle(entityVector);

        if (FastMath.abs(degrees) < 1.4) {

          consumeSword(damager);
          damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
          damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND,
              Material.REDSTONE_BLOCK);

          if (victim.getHealth() - 6.2 <= 0.0) {
            event.setCancelled(true);
          } else {
            event.setDamage(0.0);
          }

          DeathMessageHandler.addDamage(victim, new BackstabDamage(victim, 6.2, damager));

          victim.setHealth(FastMath.max(0.0, victim.getHealth() - 6.2));
          damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
        } else {
          damager.sendMessage(ChatColor.RED + "Backstab failed!");
        }
      }
    }
  }

  public void consumeSword(Player player) {
    ItemStack hand = player.getItemInHand();

    if (hand.getAmount() == 1) {
      player.setItemInHand(new ItemStack(Material.AIR));
    } else {
      hand.setAmount(hand.getAmount() - 1);
    }

    player.updateInventory();
  }

  public class BackstabDamage extends PlayerDamage {

    public BackstabDamage(final Player damaged, final double damage, final Player damager) {
      super(damaged, damage, damager);
    }

    public String getDescription() {
      return "Backstabbed by " + this.getDamager();
    }

    @Override
    public Clickable getDeathMessage() {

      Clickable clickable = getHoverStats(this.getDamaged());

      clickable.add(CC.translate(" &ewas backstabbed by "));

      clickable.add(getHoverStats(this.getDamager()));

      clickable.add(ChatColor.YELLOW + ".");

      return clickable;
    }
  }
}