package cc.stormworth.hcf.pvpclasses.pvpclasses;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class MinerClass extends PvPClass implements Listener {

    public Map<String, Integer> noDamage = Maps.newHashMap();
    public static Map<String, Integer> invis = Maps.newHashMap();
    public static BukkitTask minertask;

    public MinerClass() {
        super("Miner", null, -1);
        minertask = new BukkitRunnable() {
            public void run() {
                for (String key : noDamage.keySet()) {
                    int left = noDamage.remove(key);
                    Player player = Main.getInstance().getServer().getPlayerExact(key);
                    if (player == null) {
                        continue;
                    }
                    if (left == 0) {
                        if (player.getLocation().getY() > 20.0) {
                            continue;
                        }
                        invis.put(player.getName(), 10);
                        player.sendMessage(ChatColor.BLUE + "Miner Ghost" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                    } else {
                        noDamage.put(player.getName(), left - 1);
                    }
                }
                for (String key : invis.keySet()) {
                    Player player2 = Main.getInstance().getServer().getPlayerExact(key);
                    if (player2 != null) {
                        int secs = invis.get(player2.getName());
                        if (secs == 0) {
                            if (player2.getLocation().getY() > 20.0 || player2.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            player2.sendMessage(ChatColor.BLUE + "Miner Ghost" + ChatColor.YELLOW + " has been enabled!");
                            player2.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                        } else {
                            invis.put(player2.getName(), secs - 1);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);
    }

    public static Map<String, Integer> getInvis() {
        return invis;
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return this.wearingAllArmor(armor) && armor.getHelmet().getType() == Material.IRON_HELMET &&
                armor.getChestplate().getType() == Material.IRON_CHESTPLATE &&
                armor.getLeggings().getType() == Material.IRON_LEGGINGS &&
                armor.getBoots().getType() == Material.IRON_BOOTS;
    }

    @Override
    public void apply(final Player player) {
        super.apply(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(final Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        }
        if (!Main.getInstance().getMapHandler().isKitMap()) {
            HCFProfile hcfProfile = HCFProfile.getByUUIDIfAvailable(player.getUniqueId());
            final int diamonds = hcfProfile == null ? 0 : hcfProfile.getDiamond();
            int level = 1;
            if (diamonds > 125) {
                level = 3;
            } else if (diamonds > 50) {
                level = 2;
            }
            if (this.shouldApplyPotion(player, PotionEffectType.FAST_DIGGING, level)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, level), true);
            }
            level = -1;
            if (diamonds > 400) {
                level = 1;
            } else if (diamonds > 100) {
                level = 0;
            }
            if (level != -1 && this.shouldApplyPotion(player, PotionEffectType.SPEED, level)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level), true);
            }
            if (diamonds > 250 && this.shouldApplyPotion(player, PotionEffectType.FIRE_RESISTANCE, 0)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
            }
            if (diamonds > 600 && this.shouldApplyPotion(player, PotionEffectType.REGENERATION, 0)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
            }
            if (diamonds >= 1000 && this.shouldApplyPotion(player, PotionEffectType.SATURATION, 0)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0), true);
            }
        } else {
            if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
            }
            if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
            }
        }
        super.tick(player);
    }

    public boolean shouldApplyPotion(Player player, PotionEffectType eff, int level) {
        int potionLevel = -1;
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(eff)) {
                potionLevel = effect.getAmplifier();
                break;
            }
        }
        return !player.hasPotionEffect(eff) || potionLevel < level;
    }

    @Override
    public void remove(Player player) {
        PvPClass.removeInfiniteEffects(player);
        noDamage.remove(player.getName());
        invis.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getEntity();
        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }
        noDamage.put(player.getName(), 15);
        if (invis.containsKey(player.getName()) && MinerClass.invis.get(player.getName()) != 0) {
            invis.put(player.getName(), 10);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(ChatColor.BLUE + "Miner Ghost" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player) event.getDamager();
        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }
        noDamage.put(player.getName(), 15);
        if (invis.containsKey(player.getName()) && invis.get(player.getName()) != 0) {
            invis.put(player.getName(), 10);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(ChatColor.BLUE + "Miner Ghost" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }
}