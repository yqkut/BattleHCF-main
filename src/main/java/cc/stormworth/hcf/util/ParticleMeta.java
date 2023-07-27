package cc.stormworth.hcf.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
public class ParticleMeta {

    private final Location location;
    private final String particle;
    private final float deltaX;
    private final float deltaY;
    private final float deltaZ;
    private final float speed;
    private final int amount;

}
