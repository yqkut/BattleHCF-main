package cc.stormworth.hcf.pvpclasses.pvpclasses.bard;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;

public class BardEffect {

    @Getter
    private final PotionEffect potionEffect;
    @Getter
    private final int energy;

    private BardEffect(PotionEffect potionEffect, int energy) {
        this.potionEffect = potionEffect;
        this.energy = energy;
    }

    public static BardEffect fromPotion(PotionEffect potionEffect) {
        return (new BardEffect(potionEffect, -1));
    }

    public static BardEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
        return (new BardEffect(potionEffect, energy));
    }

    public static BardEffect fromEnergy(int energy) {
        return (new BardEffect(null, energy));
    }
}