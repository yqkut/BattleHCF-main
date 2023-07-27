package cc.stormworth.hcf.misc.war.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class PlayerCache {

    private final UUID uuid;

    private final ItemStack[] contents;
    private final ItemStack[] armorContents;
    private final Set<PotionEffect> activeEffects;
    private final GameMode gameMode;
    private final double maxHealth;
    private final double health;
    private final int level;
    private final float experience;
    private final float saturation;

    public PlayerCache(Player player) {
        this.uuid = player.getUniqueId();

        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.activeEffects = ImmutableSet.copyOf(player.getActivePotionEffects());
        this.gameMode = player.getGameMode();
        this.maxHealth = player.getMaxHealth();
        this.health = player.getHealth();
        this.level = player.getLevel();
        this.experience = player.getExp();
        this.saturation = player.getSaturation();
    }

    public void restore(Player player) {
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(this.armorContents);

        this.activeEffects.forEach(player::addPotionEffect);

        player.setGameMode(this.gameMode);
        player.setMaxHealth(this.maxHealth);
        player.setHealth(this.health);
        player.setLevel(this.level);
        player.setExp(this.experience);
        player.setSaturation(this.saturation);

        player.updateInventory();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PlayerCache)) {
            return false;
        }

        return ((PlayerCache) obj).uuid.equals(this.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}
