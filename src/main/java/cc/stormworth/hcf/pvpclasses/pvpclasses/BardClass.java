package cc.stormworth.hcf.pvpclasses.pvpclasses;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.listener.PlayerListener;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.pvpclasses.pvpclasses.bard.BardEffect;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.RestoreEffect;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import lombok.Getter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BardClass extends PvPClass implements Listener {

    @Getter
    public final static Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public static final int BARD_RANGE = 25;
    public static final int EFFECT_COOLDOWN = 10000;
    public static final float MAX_ENERGY = 120.0f;
    public static final float ENERGY_REGEN_PER_SECOND = 1.0f;
    @Getter
    private static final Map<String, Long> lastEffectUsage = new HashMap<>();
    @Getter
    public static final Map<String, Float> energy = new HashMap<>();
    private static final String ULTIMATE_METADATA = "BardUltimate";
    @Getter
    public static Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    public BardClass() {
        super("Bard", null, 2);

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));
        //BARD_CLICK_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, 0), 60));
        BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergy(25));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
        //BARD_PASSIVE_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0)));

        new BukkitRunnable() {
            public void run() {
                for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, BardClass.this) || HCFProfile.get(player).hasPvPTimer()) {
                        continue;
                    }
                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }
                        energy.put(player.getName(), FastMath.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }
                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Bard Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 15L, 20L);
    }

    public static void forceBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean self) {
        if (self) {
            smartAddPotion(source, bardEffect.getPotionEffect());
        } else {
            for (final Player player : getNearbyPlayers(source, friendly, BARD_RANGE)) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                    continue;
                }
                if (bardEffect.getPotionEffect() != null) {
                    smartAddPotion(player, bardEffect.getPotionEffect());
                }
            }
        }
    }

    public static void forceBardEffect(Player source, Entity entity, BardEffect bardEffect, boolean friendly, boolean self) {
        if (self) {
            smartAddPotion(source, bardEffect.getPotionEffect());
        } else {
            for (final Player player : getNearbyPlayers(source, entity, friendly, BARD_RANGE)) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                    continue;
                }
                if (bardEffect.getPotionEffect() != null) {
                    smartAddPotion(player, bardEffect.getPotionEffect());
                }
            }
        }
    }

    public static List<Player> getNearbyPlayers(Player player, Entity entity, boolean friendly, int radius) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Main.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity nearby : entity.getNearbyEntities(radius, radius / 2, radius)) {
            if (nearby instanceof Player) {
                Player nearbyPlayer = (Player) nearby;

                if (HCFProfile.get(nearbyPlayer).hasPvPTimer()) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                if (nearbyPlayer.hasMetadata("anti_bard")){
                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

    public static List<Player> getNearbyPlayers(Player player, boolean friendly, int radius) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Main.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(radius, radius / 2, radius)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (HCFProfile.get(nearbyPlayer).hasPvPTimer()) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                if (nearbyPlayer.hasMetadata("anti_bard")){
                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLD_HELMET &&
                armor.getChestplate().getType() == Material.GOLD_CHESTPLATE &&
                armor.getLeggings().getType() == Material.GOLD_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLD_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);

        if (HCFProfile.get(player).hasPvPTimer()) {
            player.sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
        }
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (!CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") || (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && CustomTimerCreateCommand.hasSOTWEnabled(player))) {
            if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType()) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                // CUSTOM
                if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE && getLastEffectUsage().containsKey(player.getName()) && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                    return;
                }

                if (CooldownAPI.hasCooldown(player, "anti_class")) {
                    return;
                }

                giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
            }
        }
        super.tick(player);
    }

    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        Map<Effect, RestoreEffect> entry = Main.getInstance().getEffectRestorer().getRestores().row(player.getUniqueId());

        for (Iterator<RestoreEffect> it = entry.values().iterator(); it.hasNext();) {
            RestoreEffect restore = it.next();
            PotionEffect potionEffect = restore.getEffect();

            if (potionEffect.getType().getName().equals("SPEED") && potionEffect.getAmplifier() == 1) {
                it.remove();
            } else if (potionEffect.getType().getName().equals("DAMAGE_RESISTANCE") && potionEffect.getAmplifier() == 1) {
                it.remove();
            } else if (potionEffect.getType().getName().equals("REGENERATION") && potionEffect.getAmplifier() == 0) {
                it.remove();
            }
        }

        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.SPEED)) {
                player.removePotionEffect(activePotionEffect.getType());
            }

            if(activePotionEffect.getAmplifier() == 1 && activePotionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                player.removePotionEffect(activePotionEffect.getType());
            }

            if(activePotionEffect.getAmplifier() == 0 && activePotionEffect.getType().equals(PotionEffectType.REGENERATION)) {
                player.removePotionEffect(activePotionEffect.getType());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer") && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer())) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        if (HCFProfile.get(event.getPlayer()).hasPvPTimer()) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            return;
        }

        if (CooldownAPI.hasCooldown(event.getPlayer(), "anti_class")) {
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = FastMath.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You don't have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && PlayerListener.DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime());
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        if (event.getPlayer().getItemInHand().getAmount() == 1) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
        } else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(final EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (player.hasMetadata(ULTIMATE_METADATA)) {
            if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                for (final Player nearby : getNearbyPlayers(player, false, 7)) {
                    if (DTRBitmask.SAFE_ZONE.appliesAt(nearby.getLocation())) {
                        continue;
                    }

                    KOTH activeKoth = (KOTH) Main.getInstance().getEventHandler().getActiveEvent();
                    if (activeKoth != null && activeKoth.getCurrentCapper() != null && nearby.getName() == activeKoth.getCurrentCapper())
                        continue;

                    nearby.setVelocity(player.getVelocity().setY(2.4));
                    nearby.setHealth(nearby.getHealth() - 3);
                    player.removeMetadata(ULTIMATE_METADATA, Main.getInstance());
                    player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 2.0F, 1.0F);
                    nearby.playSound(player.getLocation(), Sound.DIG_GRASS, 1.0F, 1.0F);
                    player.sendMessage(CC.translate("&cYour ultimate time has ended!"));
                }
            } else {
                player.sendMessage(CC.translate("&cUltimate cannot be used while in spawn."));
            }
        }
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean click) {
        for (Player player : getNearbyPlayers(source, friendly, BARD_RANGE)) {
            if (player == null) continue;

            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            if (player.hasMetadata("anti_bard")){
                continue;
            }

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null &&
                    bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }


            if (bardEffect.getPotionEffect() != null) {
                //smartAddPotion(player, bardEffect.getPotionEffect());
                Main.getInstance().getEffectRestorer().setRestoreEffect(player, bardEffect.getPotionEffect());
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case WHEAT:
                for (Player nearbyPlayer : getNearbyPlayers(player, true, BARD_RANGE)) {
                    nearbyPlayer.setFoodLevel(20);
                    nearbyPlayer.setSaturation(10F);
                }
                break;
            /*case FERMENTED_SPIDER_EYE:
                break;*/
            default:
                Main.getInstance().getLogger().warning("No custom Bard effect defined for " + material + ".");
        }
    }
}