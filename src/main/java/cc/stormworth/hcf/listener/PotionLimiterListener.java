package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionLimiterListener implements Listener {

    private static final List<PotionLimit> potionLimits = new ArrayList<>();

    public PotionLimiterListener() {
        this.loadPotionLimits();
    }

    private void loadPotionLimits() {
        potionLimits.clear();

        ConfigurationSection section = Main.getInstance().getLimitersfile().getConfig().getConfigurationSection("POTION_LIMITER");

        section.getKeys(false).forEach(type -> {
            if (section.getInt(type + ".LEVEL") == -1) return;

            PotionLimit potionLimit = new PotionLimit();
            potionLimit.setType(PotionEffectType.getByName(type));
            potionLimit.setLevel(section.getInt(type + ".LEVEL"));
            potionLimit.setExtended(section.getBoolean(type + ".EXTENDED"));

            potionLimits.add(potionLimit);
        });
    }

    private PotionLimit getPotionLimit(PotionEffectType type) {
        return potionLimits.stream().filter(limit -> limit.getType() == type).findFirst().orElse(null);
    }

    public boolean shouldNotCancelEffect(Potion potion) {
        if (potion == null) return true;

        for (PotionEffect effect : potion.getEffects()) {
            PotionLimit limit = this.getPotionLimit(effect.getType());
            if (limit == null) return true;

            if (limit.getLevel() == 0 || (effect.getAmplifier() + 1) > limit.getLevel()) return false;
            if (potion.hasExtendedDuration() && !limit.isExtended()) return false;
        }

        return true;
    }

    public boolean shouldNotCancelEffect(PotionEffect effect) {
        PotionLimit limit = this.getPotionLimit(effect.getType());
        if (limit == null) return true;

        return limit.getLevel() != 0 && (effect.getAmplifier() + 1) <= limit.getLevel();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionBrew(BrewEvent event) {
        BrewerInventory brewer = event.getContents();
        ItemStack ingredient = brewer.getIngredient().clone();

        ItemStack[] potions = new ItemStack[3];

        for (int i = 0; i < 3; i++) {
            if (event.getContents().getItem(i) == null) continue;

            potions[i] = brewer.getItem(i).clone();
        }

        TaskUtil.run(Main.getInstance(), () -> {
            for (int i = 0; i < 3; i++) {
                if (brewer.getItem(i) == null || brewer.getItem(i).getDurability() == 0) continue;
                if (this.shouldNotCancelEffect(Potion.fromItemStack(brewer.getItem(i)))) continue;

                brewer.setItem(i, potions[i]);
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION || item.getDurability() == 0) return;
        if (this.shouldNotCancelEffect(Potion.fromItemStack(item))) return;

        event.setCancelled(true);

        Player player = event.getPlayer();

        //player.setItemInHand(new ItemStack(Material.AIR));
        player.sendMessage(CC.RED + "Effect disabled.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();
        if (this.shouldNotCancelEffect(Potion.fromItemStack(thrownPotion.getItem()))) return;

        event.setCancelled(true);

        if (!(thrownPotion.getShooter() instanceof Player)) return;
        Player player = (Player) thrownPotion.getShooter();

        //player.setItemInHand(new ItemStack(Material.AIR));
        player.sendMessage(CC.RED + "Effect disabled.");
    }

    @Getter
    @Setter
    static
    class PotionLimit {

        PotionEffectType type;
        int level;
        boolean extended;
    }
}