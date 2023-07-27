package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class AntiBard extends DamageableAbility {

    public AntiBard() {
        super("AntiBard",
                "&6Anti Bard",
                Lists.newArrayList(
                        "",
                        "&7Hit your target &c3 times &7to prevent",
                        "&7them from receiving bard effects",
                        "&7for 1 minute.",
                        ""
                ),
                new ItemStack(Material.GOLD_INGOT),
                TimeUtil.parseTimeLong("2m"));
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

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

        CooldownAPI.setCooldown(damaged, "anti_bard", TimeUtil.parseTimeLong("1m"));
        CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
        hit.setHits(0);

        damaged.sendMessage("");
        damaged.sendMessage(CC.translate("&cYou have been hit with &e&lAnti Bard &cby &e" + damager.getName()));
        damaged.sendMessage(CC.translate("&cYou will not be able to receive bard effects for &e1 minute."));
        damaged.sendMessage("");
        super.onEntityDamageByEntity(event);
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        return null;
    }
}
