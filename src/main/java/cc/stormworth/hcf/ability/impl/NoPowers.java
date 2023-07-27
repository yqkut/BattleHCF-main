package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.pvpclasses.PvPClassHandler;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class NoPowers extends DamageableAbility {

    public NoPowers() {
        super("NoPowers",
                "&cNo Powers",
                Lists.newArrayList(
                        "",
                        "&7Hit your target &c3 times &7to prevent",
                        "&7them from using any ability",
                        "&7for 20 seconds,",
                        ""
                ),
                new ItemBuilder(Material.GOLD_NUGGET).build(),
                TimeUtil.parseTimeLong("2m30s"));
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        HCFProfile profileDamager = HCFProfile.get(damager);

        if(!PvPClassHandler.getEquippedKits().containsKey(damaged.getName())){
            damager.sendMessage(CC.translate("&cThis player is not in a class!"));
            return;
        }

        Hit hit = profileDamager.getHit();

        if (hit == null || hit.getUuid() != damaged.getUniqueId() || !isItem(hit.getItemStack())) {
            hit = new Hit(damaged.getUniqueId(), getItem());
            profileDamager.setHit(hit);
        }

        hit.setHits(hit.getHits() + 1);

        if ((3 - hit.getHits()) > 0) {
            damager.sendMessage(CC.translate("&6&l[&e✷&6&l] &eYou have to hit your enemy &6&l" + (3 - hit.getHits()) + " &emore times."));
        }

        if (hit.getHits() < 3) {
            return;
        }

        hit.setHits(0);

        CooldownAPI.setCooldown(damaged, "NoPowers", TimeUtil.parseTimeLong("20s"));
        CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");

        super.onEntityDamageByEntity(event);
    }


    @Override
    public List<PotionEffect> getPotionEffects() {
        return null;
    }
}
