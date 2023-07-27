package cc.stormworth.hcf.deathmessage.objects;

import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Damage {

  private final Player damaged;
  private final double damage;
  private final long time;

  public Damage(final Player damaged, final double damage) {
    this.damaged = damaged;
    this.damage = damage;
    this.time = System.currentTimeMillis();
  }

  public abstract Clickable getDeathMessage();

  public String wrapName(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    int kills =
        Main.getInstance().getMapHandler().isKitMap() ? Main.getInstance().getMapHandler()
            .getStatsHandler().getStats(player).getKills() : profile.getKills();
    return ChatColor.RED + player.getName() + ChatColor.DARK_RED + "[" + kills + "]"
        + ChatColor.WHITE;
  }

  public Clickable getHoverStats(Player player) {
    HCFProfile profile = HCFProfile.get(player);
    int kills =
        Main.getInstance().getMapHandler().isKitMap() ? Main.getInstance().getMapHandler()
            .getStatsHandler().getStats(player).getKills() : profile.getKills();
    int deaths = Main.getInstance().getMapHandler().isKitMap() ? Main.getInstance()
        .getMapHandler().getStatsHandler().getStats(player).getDeaths() : profile.getDeaths();

    int killStreack = Main.getInstance().getMapHandler().isKitMap() ? Main.getInstance()
        .getMapHandler().getStatsHandler().getStats(player).getKillstreak() : 0;

    return new Clickable(wrapName(player),
        "" +
            "&eKills:&f " + kills + "\n" +
            "&eDeaths:&f " + deaths + "\n" +
            "&eKillStreak:&f " + killStreack,
        "");
  }

  public long getTimeDifference() {
    return System.currentTimeMillis() - this.time;
  }

  public Player getDamaged() {
    return this.damaged;
  }

  public double getDamage() {
    return this.damage;
  }

  public long getTime() {
    return this.time;
  }
}