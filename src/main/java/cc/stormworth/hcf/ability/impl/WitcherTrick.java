package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.util.List;
import java.util.UUID;

public class WitcherTrick extends InteractAbility {

    public WitcherTrick() {
        super("WitcherTrick",
                "&5WitcherTrick",
                Lists.newArrayList(
                        "",
                        "&7Wanna see a bard's antonym?",
                        "&7Well, spawn a Witcher that will",
                        "&7debuff all enemies players around it",
                        "&7in a shorter radius",
                        ""
                ),
                new ItemBuilder(Material.FLINT_AND_STEEL)/*.spawnerType(EntityType.PIG_ZOMBIE)*/.build(), //FIX
                TimeUtil.parseTimeLong("2m"));

        SpigotConfig.disableEntityAi(EntityType.WITCH);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Witch witch = player.getWorld().spawn(player.getLocation(), Witch.class);

        witch.removeMetadata("enableai", Main.getInstance());
        witch.setMetadata("owner", new FixedMetadataValue(Main.getInstance(), player.getUniqueId()));
        witch.setCustomName(CC.translate("&5&lWitcher Trick"));
        witch.setCanPickupItems(false);

        witch.setCustomNameVisible(true);


        new BukkitRunnable() {

            final Material[] bardItem = {
                    Material.SPIDER_EYE, Material.WHEAT, Material.GHAST_TEAR
            };

            int count = 10;

            @Override
            public void run() {
                if (count <= 0 || witch.isDead()) {
                    cancel();

                    if (!witch.isDead()) witch.remove();
                    return;
                }

                Material randomMaterial = bardItem[(int) (Math.random() * bardItem.length)];

                witch.getEquipment().setItemInHand(new ItemStack(randomMaterial));

                Team team = Main.getInstance().getTeamHandler().getTeam(player);

                witch.getNearbyEntities(10, 50, 10)
                        .stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .filter(target -> team == null || !team.getMembers().contains(target.getUniqueId()))
                        .filter(target -> target != player)
                        .forEach(target -> getPotionEffects().forEach(target::addPotionEffect));

                count--;
            }
        }.runTaskTimer(Main.getInstance(), 0, 20L);

        super.onInteract(event);
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Witch) {
            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();
                Witch witch = (Witch) event.getEntity();

                if (witch.hasMetadata("owner")) {
                    UUID uuid = (UUID) witch.getMetadata("owner").get(0).value();

                    if (uuid != null) {
                      Player owner = Main.getInstance().getServer().getPlayer(uuid);

                      if (owner.equals(player)) {
                        event.setCancelled(true);
                        return;
                      }

                      Team team = Main.getInstance().getTeamHandler().getTeam(owner);

                      if (team == null) {
                        return;
                      }

                      if (team.isMember(player.getUniqueId())) {
                        event.setCancelled(true);
                      }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Witch)) {
            return;
        }

        if (!event.getEntity().hasMetadata("owner")) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player owner = Main.getInstance().getServer().getPlayer(UUID.fromString(event.getEntity().getMetadata("owner").get(0).asString()));
        Player player = (Player) event.getDamager();

        if (owner.equals(player)) {
            event.setCancelled(true);
            return;
        }

        Team team = Main.getInstance().getTeamHandler().getTeam(owner);

        if (team == null) {
            return;
        }

        if (team.isMember(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Witch) && !event.getEntity().hasMetadata("owner")) {
            return;
        }

        event.getDrops().clear();
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return Lists.newArrayList(
                new PotionEffect(PotionEffectType.POISON, 20 * 3, 1),
                new PotionEffect(PotionEffectType.WITHER, 20 * 3, 1),
                new PotionEffect(PotionEffectType.SLOW, 20 * 3, 1),
                new PotionEffect(PotionEffectType.WEAKNESS, 20 * 3, 1)
        );
    }
}