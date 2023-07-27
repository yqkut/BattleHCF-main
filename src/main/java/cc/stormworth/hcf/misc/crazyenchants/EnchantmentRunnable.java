package cc.stormworth.hcf.misc.crazyenchants;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.crazyenchants.utils.enums.CEnchantments;
import cc.stormworth.hcf.profile.EnchantmentEffect;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.util.Effect;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class EnchantmentRunnable implements Runnable{

    private final EnchantmentsManager enchantmentsManager;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());

            if (profile == null) {
                continue;
            }

            for (CEnchantments enchantment : enchantmentsManager.getEnchantmentPotions().keySet()) {
                if (enchantment.isActivated()) {
                    for (int index = 0; index < player.getInventory().getArmorContents().length; index++) {
                        ItemStack item = player.getInventory().getArmorContents()[index];
                        if (profile.getEnchantments().containsKey(enchantment)) {
                            if (!enchantmentsManager.hasEnchantment(item, enchantment.getEnchantment())){
                                for(Iterator<EnchantmentEffect> iterator = profile.getEnchantments().get(enchantment).iterator(); iterator.hasNext();) {
                                    EnchantmentEffect enchantmentEffect = iterator.next();

                                    if (enchantmentEffect.getIndex() != index) {
                                        break;
                                    }

                                    PotionEffect toRemove = enchantmentEffect.getEffect();
                                    PotionEffect effect = Players.getActivePotionEffect(player, toRemove.getType());

                                    if (effect != null && effect.getAmplifier() != toRemove.getAmplifier()) {
                                        continue;
                                    }

                                    Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), Effect.getByPotionEffect(toRemove));

                                    player.removePotionEffect(toRemove.getType());

                                    if (profile.getEnchantments().get(enchantment).size() == 1) {
                                        profile.getEnchantments().remove(enchantment);
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }else if (enchantmentsManager.hasEnchantment(item, enchantment.getEnchantment())){ //Has piece of armor effect but no the correct effect

                                Map<PotionEffectType, Integer> effects = enchantmentsManager.getUpdatedEffects(player, item, new ItemStack(Material.AIR), enchantment);

                                for (Map.Entry<PotionEffectType, Integer> type : effects.entrySet()) {
                                    if (type.getValue() >= 0) {

                                        if (!Main.getInstance().getMapHandler().isKitMap() &&
                                                type.getKey() == PotionEffectType.INVISIBILITY &&
                                                Main.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                                            continue;
                                        }

                                        PotionEffect activeEffect = Players.getActivePotionEffect(player, type.getKey());
                                        PotionEffect effect = new PotionEffect(type.getKey(), Integer.MAX_VALUE, type.getValue());

                                        if (activeEffect != null && activeEffect.getAmplifier() >= effect.getAmplifier()) {
                                            continue;
                                        }

                                        player.addPotionEffect(effect, true);

                                        List<EnchantmentEffect> effectsList = Lists.newArrayList();

                                        if (profile.getEnchantments().containsKey(enchantment)) {
                                            effectsList = profile.getEnchantments().get(enchantment);
                                        }

                                        effectsList.add(new EnchantmentEffect(item, effect, index));

                                        profile.getEnchantments().put(enchantment, effectsList);
                                    }
                                }
                            }else{
                                for(Iterator<EnchantmentEffect> iterator = profile.getEnchantments().get(enchantment).iterator(); iterator.hasNext();){
                                    EnchantmentEffect enchantmentEffect = iterator.next();
                                    if(enchantmentEffect.getIndex() == index){
                                        iterator.remove();

                                        PotionEffect toRemove = enchantmentEffect.getEffect();
                                        PotionEffect effect = Players.getActivePotionEffect(player, toRemove.getType());
                                        if (effect != null && effect.getAmplifier() != toRemove.getAmplifier()) {
                                            continue;
                                        }

                                        Main.getInstance().getEffectRestorer().getRestores().remove(player.getUniqueId(), Effect.getByPotionEffect(toRemove));

                                        player.removePotionEffect(toRemove.getType());
                                        if (profile.getEnchantments().get(enchantment).size() == 1) {
                                            profile.getEnchantments().remove(enchantment);
                                        } else {
                                            iterator.remove();
                                        }
                                    }
                                }
                            }
                        }else if (!profile.getEnchantments().containsKey(enchantment) && enchantmentsManager.hasEnchantment(item, enchantment.getEnchantment())) {

                            Map<PotionEffectType, Integer> effects = enchantmentsManager.getUpdatedEffects(player, item, new ItemStack(Material.AIR), enchantment);
                            for (Map.Entry<PotionEffectType, Integer> type : effects.entrySet()) {
                                if (type.getValue() >= 0) {

                                    if (!Main.getInstance().getMapHandler().isKitMap() && type.getKey() == PotionEffectType.INVISIBILITY
                                            && Main.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                                        continue;
                                    }

                                    PotionEffect activeEffect = Players.getActivePotionEffect(player, type.getKey());
                                    PotionEffect effect = new PotionEffect(type.getKey(), Integer.MAX_VALUE, type.getValue());

                                    if (activeEffect != null && activeEffect.getAmplifier() >= effect.getAmplifier()) {
                                        continue;
                                    }

                                    player.addPotionEffect(effect, true);

                                    List<EnchantmentEffect> effectsList = Lists.newArrayList();

                                    if (profile.getEnchantments().containsKey(enchantment)) {
                                        effectsList = profile.getEnchantments().get(enchantment);
                                    }

                                    effectsList.add(new EnchantmentEffect(item, effect, index));

                                    profile.getEnchantments().put(enchantment, effectsList);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
