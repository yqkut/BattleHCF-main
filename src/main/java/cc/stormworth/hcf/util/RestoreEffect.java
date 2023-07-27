package cc.stormworth.hcf.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.function.Predicate;

@AllArgsConstructor
@Getter
public class RestoreEffect {

    private final PotionEffect effect;
    private final Predicate<Player> condition;

}
