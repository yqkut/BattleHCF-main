package cc.stormworth.hcf.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
@Getter
public class Effect {

    private final PotionEffectType type;
    private final int level;

    public static Effect getByPotionEffect(PotionEffect effect) {
        return new Effect(effect.getType(), effect.getAmplifier());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Effect) {
            Effect effect = (Effect) obj;
            return this.type.equals(effect.type) && this.level == effect.level;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type.getId() + this.level;
    }
}
