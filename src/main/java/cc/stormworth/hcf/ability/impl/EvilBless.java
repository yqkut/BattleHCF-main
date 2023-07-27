package cc.stormworth.hcf.ability.impl;

import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.DamageableAbility;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Hit;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.Lists;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public class EvilBless extends DamageableAbility {

    public EvilBless() {
        super("EvilBless",
                "&5Evil Bless",
                Lists.newArrayList(
                        "",
                        "&7Hit your target &c3 times &7to trick",
                        "&7their hearts status",
                        "&7basically, they won't know how many,",
                        "&7hearts left do they have",
                        ""
                ),
                new ItemBuilder(Material.GOLD_NUGGET).build(),
                TimeUtil.parseTimeLong("5m"));
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
            damager.sendMessage(CC.translate("&6&l[&e✷&6&l] &eYou have to hit your enemy &6&l" + (3 - hit.getHits()) + " &emore times."));
        }

        if (hit.getHits() < 3) {
            return;
        }

        hit.setHits(0);

        CooldownAPI.setCooldown(damager, getName(), getCooldown(), "&aYou can now use " + getDisplayName() + " &aability again.");
        getPotionEffects().forEach(damaged::addPotionEffect);
        giveHeart(damaged);
        super.onEntityDamageByEntity(event);

        TaskUtil.runLater(Main.getInstance(), () -> removeFakeHearts(damaged), 20 * 6);
    }

    private void giveHeart(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 7, 1));

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AttributeMapServer attributemapserver = (AttributeMapServer) entityPlayer.getAttributeMap();
        Set set = attributemapserver.getAttributes();

        for (Object genericInstance : set) {
            IAttribute attribute = ((AttributeInstance) genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
                set.remove(genericInstance);
                break;
            }
        }

        set.add(new AttributeModifiable(entityPlayer.getAttributeMap(), (new AttributeRanged("generic.maxHealth", 4, 0.0D, Float.MAX_VALUE)).a("Max Health").a(true)));

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(entityPlayer.getId(), set));
    }

    private void removeFakeHearts(Player player){
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AttributeMapServer attributemapserver = (AttributeMapServer) entityPlayer.getAttributeMap();
        Set set = attributemapserver.getAttributes();

        for (Object genericInstance : set) {
            IAttribute attribute = ((AttributeInstance) genericInstance).getAttribute();
            if (attribute.getName().equals("generic.maxHealth")) {
                set.remove(genericInstance);
                break;
            }
        }

        set.add(new AttributeModifiable(entityPlayer.getAttributeMap(), (new AttributeRanged("generic.maxHealth", 20, 0.0D, Float.MAX_VALUE)).a("Max Health").a(true)));

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(entityPlayer.getId(), set));

        player.removePotionEffect(PotionEffectType.WITHER);
    }


    @Override
    public List<PotionEffect> getPotionEffects() {
        return Lists.newArrayList(
                new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0)
        );
    }
}
