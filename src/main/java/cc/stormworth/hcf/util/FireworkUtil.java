package cc.stormworth.hcf.util;

import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkUtil {

  public static void launchFirework(Location loc) {
    Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
    FireworkMeta fwm = fw.getFireworkMeta();

    Random r = new Random();
    int rt = r.nextInt(4) + 1;
    FireworkEffect.Type type = FireworkEffect.Type.BALL;
    if (rt == 1) {
      type = FireworkEffect.Type.BALL;
    } else if (rt == 2) {
      type = FireworkEffect.Type.BALL_LARGE;
    } else if (rt == 3) {
      type = FireworkEffect.Type.BURST;
    } else if (rt == 4) {
      type = FireworkEffect.Type.STAR;
    }

    int r1i = r.nextInt(17) + 1;
    int r2i = r.nextInt(17) + 1;
    Color c1 = getColor(r1i);
    Color c2 = getColor(r2i);

    FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1)
        .withFade(c2).with(type).trail(r.nextBoolean()).build();
    fwm.addEffect(effect);

    int rp = r.nextInt(1) + 1;
    fwm.setPower(rp);
    fw.setFireworkMeta(fwm);
  }

  public static Color getColor(int i) {
    Color color = null;
    switch (i) {
      case 1:
        color = Color.AQUA;
        break;
      case 2:
        color = Color.BLACK;
        break;
      case 3:
        color = Color.BLUE;
        break;
      case 4:
        color = Color.FUCHSIA;
        break;
      case 5:
        color = Color.GRAY;
        break;
      case 6:
        color = Color.GREEN;
        break;
      case 7:
        color = Color.LIME;
        break;
      case 8:
        color = Color.MAROON;
        break;
      case 9:
        color = Color.NAVY;
        break;
      case 10:
        color = Color.OLIVE;
        break;
      case 11:
        color = Color.ORANGE;
        break;
      case 12:
        color = Color.PURPLE;
        break;
      case 13:
        color = Color.RED;
        break;
      case 14:
        color = Color.SILVER;
        break;
      case 15:
        color = Color.TEAL;
        break;
      case 16:
        color = Color.WHITE;
        break;
      case 17:
        color = Color.YELLOW;
        break;
      default:
        break;
    }
    return color;
  }
}