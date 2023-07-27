package cc.stormworth.hcf.listener;

import org.bukkit.event.Listener;

public class InvisibilityListener implements Listener {

    //public Map<UUID, PotionEffect> warzoneInvis = new LinkedHashMap<>();

    public InvisibilityListener() {
        /*new BukkitRunnable() {
            public void run() {
                if (EOTWCommand.isFfaEnabled()) {
                    cancel();
                    return;
                }
                *//*if (!warzoneInvis.isEmpty()) {
                    Iterator<Map.Entry<UUID, PotionEffect>> iterator = warzoneInvis.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<UUID, PotionEffect> next = iterator.next();
                        Player player = Bukkit.getPlayer(next.getKey());
                        if (player != null) {
                            PlayerInventory armor = player.getInventory();

                            boolean isWearingArmor = armor.getHelmet() == null || armor.getChestplate() == null || armor.getLeggings() == null || armor.getBoots() == null;
                            if (isWearingArmor || !Main.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                                player.addPotionEffect(next.getValue());
                                warzoneInvis.remove(next.getKey());
                            }
                        }
                    }
                }*//*
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getWorld().getName().equalsIgnoreCase("world")) {
                        if (Main.getInstance().getServerHandler().isWarzone(online.getLocation())) {
                            if (online.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                PlayerInventory armor = online.getInventory();
                                if (armor.getHelmet() != null &&
                                        armor.getChestplate() != null &&
                                        armor.getLeggings() != null &&
                                        armor.getBoots() != null) {
                                    for (final PotionEffect potionEffect : online.getActivePotionEffects()) {
                                        if (potionEffect.getType().equals(PotionEffectType.INVISIBILITY)) {
                                            //warzoneInvis.put(online.getUniqueId(), potionEffect);
                                            online.removePotionEffect(PotionEffectType.INVISIBILITY);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);*/
    }
}