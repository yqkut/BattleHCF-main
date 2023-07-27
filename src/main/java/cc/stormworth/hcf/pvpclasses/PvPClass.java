package cc.stormworth.hcf.pvpclasses;

import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class PvPClass implements Listener {

    private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();
    @Getter
    String name;
    @Getter
    List<Material> consumables;
    @Getter
    private final Map<ObjectId, Integer> limits;
    @Getter
    private int limit = -1;
    @Getter
    private final List<UUID> limitMessage = new ArrayList<>();

    public PvPClass(String name, List<Material> consumables, int limit) {
        this.name = name;
        this.consumables = consumables;
        this.limits = new HashMap<org.bson.types.ObjectId, Integer>();
        this.limit = limit;
    }

    public boolean hasLimit(Team team) {
        return limit > 0 && this.limits.getOrDefault(team.getUniqueId(), 0) >= limit;
    }

    public void addLimit(Team team) {
        if (limit <= -1) return;
        limits.put(team.getUniqueId(), limits.getOrDefault(team.getUniqueId(), 0) + 1);
    }

    public void removeLimit(Team team) {
        if (limit <= -1) return;
        limits.put(team.getUniqueId(), limits.getOrDefault(team.getUniqueId(), 1) - 1);
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
        HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());
        if (profile != null) profile.getEnchantments().clear();
    }

    public static void smartAddPotion(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        if (player == null) return;
        if (player.getActivePotionEffects().isEmpty()) {
            player.addPotionEffect(effect, true);
            return;
        }
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (0 < active.getDuration() && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    public void apply(Player player) {
    }

    public void tick(Player player) {
    }

    public void remove(Player player) {
    }

    public boolean canApply(Player player) {
        return (true);
    }

    public boolean itemConsumed(Player player, Material type) {
        return (true);
    }

    public abstract boolean qualifies(PlayerInventory armor);

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            PotionEffect previous = restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());
                if (profile != null) profile.getEnchantments().clear();
                if (previous.getDuration() > 1_000_000) return;
                event.setCancelled(true);
                player.addPotionEffect(previous, true);
            }
        }
    }

    @AllArgsConstructor
    public static class SavedPotion {
        @Getter
        private final boolean perm;
        @Getter
        PotionEffect potionEffect;
        @Getter
        long time;
    }
}