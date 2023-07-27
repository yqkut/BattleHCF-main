package cc.stormworth.hcf.util.player;

import net.minecraft.server.v1_7_R4.EntityFireworks;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworksUtil {

    private static final FireworkEffect[] FIREWORK_EFFECTS = new FireworkEffect[]{
            FireworkEffect.builder().withColor(Color.RED, Color.PURPLE, Color.MAROON).build(),
            FireworkEffect.builder().withColor(Color.BLUE, Color.AQUA, Color.NAVY, Color.TEAL).build(),
            FireworkEffect.builder().withColor(Color.FUCHSIA, Color.AQUA, Color.ORANGE).build(),
            FireworkEffect.builder().withColor(Color.FUCHSIA, Color.WHITE).withTrail().build(),
            FireworkEffect.builder().withColor(Color.GRAY, Color.SILVER, Color.GREEN).build(),
            FireworkEffect.builder().withColor(Color.GREEN, Color.LIME).build(),
            FireworkEffect.builder().withColor(Color.RED, Color.YELLOW).build(),
            FireworkEffect.builder().withColor(Color.GREEN, Color.GRAY, Color.FUCHSIA).build(),
            FireworkEffect.builder().withColor(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.AQUA).build(),
            FireworkEffect.builder().withColor(Color.PURPLE, Color.GREEN, Color.YELLOW).build(),
            FireworkEffect.builder().withColor(Color.BLUE, Color.MAROON, Color.WHITE).withTrail().build(),
            FireworkEffect.builder().withColor(Color.BLUE, Color.YELLOW).withTrail().build(),
            FireworkEffect.builder().withColor(Color.FUCHSIA, Color.NAVY, Color.RED).withTrail().build(),
            FireworkEffect.builder().withColor(Color.LIME, Color.ORANGE, Color.TEAL).withTrail().build(),
            FireworkEffect.builder().withColor(Color.GRAY, Color.MAROON, Color.NAVY).withTrail().build(),
            FireworkEffect.builder().withColor(Color.AQUA, Color.RED, Color.FUCHSIA).withTrail().build()
    };

    public static void play(Location loc, FireworkEffect fe) {
        net.minecraft.server.v1_7_R4.World world = ((CraftWorld) loc.getWorld()).getHandle();
        EntityFireworks fw = new EntityFireworks(world);
        FireworkMeta data = ((Firework) fw.getBukkitEntity()).getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        ((Firework) fw.getBukkitEntity()).setFireworkMeta(data);
        fw.expectedLifespan = 1;
        fw.setPosition(loc.getX(), loc.getY(), loc.getZ());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(fw);
    }

    public static void playRandom(Location loc) {
        play(loc, getRandomEffect());
    }

    public static void launch(Location loc, FireworkEffect fe) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        fw.setFireworkMeta(data);
    }

    public static void launchRandom(Location loc) {
        launch(loc, getRandomEffect());
    }

    public static FireworkEffect getRandomEffect() {
        return RandomUtils.of(FIREWORK_EFFECTS);
    }
}

