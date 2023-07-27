package cc.stormworth.hcf.ability.impl.sacrifice;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.InteractAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class Sacrifice extends InteractAbility {

    public Sacrifice() {
        super("Sacrifice",
                "&cSacrifice",
                Lists.newArrayList(
                        "",
                        "&7Right click and decide who would you like",
                        "&7to sacrifice.",
                        "&7Once &eteleporter countdown &estarts",
                        "",
                        "&c(make sure to DO NOT move)",
                        "",
                        "&7Your teammate will start receiving &c1 heart damage",
                        "&7for every second happens during the TP",
                        "",
                        "&7Once you have been teleported, your damage will be",
                        "&esynchronized &7with your teammate.",
                        "",
                        "&7Both will receive positive effects",
                        ""
                ),
                new ItemStack(Material.WOOD_SWORD),
                TimeUtil.parseTimeLong("10m"));
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        HCFProfile profile = HCFProfile.get(player);

        Team team = profile.getTeam();

        if (team == null) {
            player.sendMessage(CC.translate("&cYou must be in a faction to use this Ability"));
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        new SacrificeMenu(team).openMenu(player);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        HCFProfile damagedProfile = HCFProfile.get(damaged);

        if (damagedProfile.getTeam() != null) {
            if (damagedProfile.getTeam().getMembers().contains(damager.getUniqueId())) {
                return;
            }
        }

        if (!CooldownAPI.hasCooldown(damaged, "damage_sync")) {

            if (damaged.hasMetadata("damage_sync")){
                damaged.removeMetadata("damage_sync", Main.getInstance());
            }

            return;
        }

        Player other = Bukkit.getPlayer(UUID.fromString(damaged.getMetadata("damage_sync").get(0).asString()));
        other.damage(event.getDamage());
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return null;
    }
}
