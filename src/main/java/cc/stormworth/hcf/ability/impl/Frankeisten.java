package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Frankeisten extends DamageableAbility {

    public Frankeisten() {
        super("Frankeisten",
                "&2Frankeisten",
                Lists.newArrayList(
                        "",
                        "&7Hit your target &c3 times &7to invoke",
                        "&7a thunder rain every 3 hits",
                        "&7you hit your target",
                        ""
                ),
                new ItemStack(Material.ROTTEN_FLESH),
                TimeUtil.parseTimeLong("2m30s"));
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if(Main.getInstance().getTeamHandler().getTeam(damager).isMember(damaged.getUniqueId())) return;
        HCFProfile profileDamager = HCFProfile.get(damager);

        Hit hit = profileDamager.getHit();

        if (hit == null || hit.getUuid() != damaged.getUniqueId() || !isItem(hit.getItemStack())) {
            hit = new Hit(damaged.getUniqueId(), getItem());
            profileDamager.setHit(hit);
        }

        hit.setHits(hit.getHits() + 1);

        if ((3 - hit.getHits()) > 0) {
            damager.sendMessage(
                    CC.translate("&6&l[&e✷&6&l] &eYou have to hit your enemy &6&l" + (3 - hit.getHits())
                            + " &emore times."));
        }

        if (hit.getHits() < 3) {
            return;
        }

        CooldownAPI.setCooldown(damager, "mode_frankeisten", TimeUtil.parseTimeLong("8s"));
        CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
        hit.setHits(0);

        super.onEntityDamageByEntity(event);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Team team = Main.getInstance().getTeamHandler().getTeam(damager);
        if(team != null && team.isMember(damaged.getUniqueId())) return;
//        if(Main.getInstance().getTeamHandler().getTeam(damager).isMember(damaged.getUniqueId())) return;

        if (!CooldownAPI.hasCooldown(damager, "mode_frankeisten")){
            return;
        }

        HCFProfile profileDamager = HCFProfile.get(damager);

        HCFProfile damagedProfile = HCFProfile.get(damaged);

        if (damagedProfile.getTeam() != null) {
            if (damagedProfile.getTeam().getMembers().contains(damager.getUniqueId())) {
                return;
            }
        }


        Hit hit = profileDamager.getHit();

        if (hit == null) {
            hit = new Hit(damaged.getUniqueId(), damager.getItemInHand());
            profileDamager.setHit(hit);
        }

        hit.setHits(hit.getHits() + 1);

        if (hit.getHits() < 3) {
            return;
        }

        World world = damager.getWorld();

        world.strikeLightningEffect(damaged.getLocation());
        damaged.setHealth(damaged.getHealth() - 2);
        hit.setHits(0);
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return null;
    }
}
