package cc.stormworth.hcf.events.koth;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.events.events.EventActivatedEvent;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.events.events.EventDeactivatedEvent;
import cc.stormworth.hcf.events.koth.events.EventControlTickEvent;
import cc.stormworth.hcf.events.koth.events.KOTHControlLostEvent;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KOTH implements Event {

  @Getter
  public boolean koth = true;
  @Getter
  @Setter
  boolean active;
  @Getter
  private final String name;
  @Getter
  private BlockVector capLocation;
  @Getter
  private String world;
  @Getter
  private int capDistance;
  @Getter
  private int capTime;
  @Getter
  private boolean hidden = false;
  @Getter
  private transient String currentCapper;
  @Getter
  private transient int remainingCapTime;
  @Getter
  @Setter
  private transient boolean terminate;
  @Getter
  private final EventType type = EventType.KOTH;

  public KOTH(String name, Location location) {
    this.name = name;
    this.capLocation = location.toVector().toBlockVector();
    this.world = location.getWorld().getName();
    this.capDistance = 3;
    this.capTime = 60 * 15;
    this.terminate = false;

    Main.getInstance().getEventHandler().getEvents().add(this);
    Main.getInstance().getEventHandler().saveEvents();
  }

  public void setLocation(Location location) {
    this.capLocation = location.toVector().toBlockVector();
    this.world = location.getWorld().getName();
    Main.getInstance().getEventHandler().saveEvents();
  }

  public void setCapDistance(int capDistance) {
    this.capDistance = capDistance;
    Main.getInstance().getEventHandler().saveEvents();
  }

  public void setCapTime(int capTime) {
    int oldCapTime = this.capTime;
    this.capTime = capTime;

    if (this.remainingCapTime > capTime) {
      this.remainingCapTime = capTime;
    } else if (remainingCapTime == oldCapTime) { // this will catch the time going up
      this.remainingCapTime = capTime;
    }

    Main.getInstance().getEventHandler().saveEvents();
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
    Main.getInstance().getEventHandler().saveEvents();
  }

  public boolean activate() {
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) {
      return (false);
    }
    if (!this.getName().contains("conquest")) {
      for (Event otherKoth : Main.getInstance().getEventHandler().getEvents()) {
        if (otherKoth.isActive() && !otherKoth.getName().contains("conquest")) {
          return (false);
        }
      }
    }
    if (active) {
      return (false);
    }

    Main.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));

    if (this.name.equalsIgnoreCase("citadel")) {
      Main.getInstance().getCitadelHandler().resetCappers();
    }

    this.active = true;
    this.currentCapper = null;
    this.remainingCapTime = this.capTime;
    this.terminate = false;

    return (true);
  }

  public boolean deactivate() {
    if (!active) {
      return (false);
    }

    Main.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));

    this.active = false;
    this.currentCapper = null;
    this.remainingCapTime = this.capTime;
    this.terminate = false;

    return (true);
  }

  public void startCapping(Player player) {
    if (currentCapper != null) {
      resetCapTime();
    }

    this.currentCapper = player.getName();
    this.remainingCapTime = capTime;
  }

  public boolean finishCapping() {
    Player capper = Main.getInstance().getServer().getPlayerExact(currentCapper);

    if (capper == null) {
      resetCapTime();
      return (false);
    }

    capper.setGameMode(GameMode.SURVIVAL);
    EventCapturedEvent event = new EventCapturedEvent(this, capper);
    Main.getInstance().getServer().getPluginManager().callEvent(event);

    if (event.isCancelled()) {
      resetCapTime();
      return (false);
    }

    deactivate();
    return (true);
  }

  public void resetCapTime() {
    Main.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));

    this.currentCapper = null;
    this.remainingCapTime = capTime;

    if (terminate) {
      deactivate();
      Bukkit.broadcastMessage(
          ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.GOLD + getName() + ChatColor.YELLOW
              + " has been terminated.");
    }
  }

  @Override
  public void tick() {

    if(CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")){
      return;
    }

    if (currentCapper != null) {
      Player capper = Main.getInstance().getServer().getPlayerExact(currentCapper);

      if (capper == null
          || !onCap(capper.getLocation())
          || capper.isDead()
          || capper.getGameMode() == GameMode.CREATIVE
          || capper.hasMetadata("invisible")
          || SpectatorListener.spectators.contains(capper.getUniqueId())
          || Main.getInstance().getTeamHandler().getTeam(capper) == null) {
        resetCapTime();
        if (capper != null) {
          capper.setGameMode(GameMode.SURVIVAL);
        }
      } else {
        if (remainingCapTime % 60 == 0 && remainingCapTime > 1 && !isHidden()) {
          Team team = Main.getInstance().getTeamHandler().getTeam(capper);

          if (team != null) {
            for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
              if (team.isMember(player.getUniqueId()) && capper != player) {
                if (!this.getName().equals(MadGame.EVENT_NAME)) {
                  player.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Your team is controlling " + ChatColor.GOLD + getName() + ChatColor.YELLOW + ".");
                }
              }
            }
          }
        }

        if (remainingCapTime % 10 == 0 && remainingCapTime > 1 && !isHidden()) {

          if (!this.getName().equals(MadGame.EVENT_NAME)) {
            capper.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Attempting to control " + ChatColor.GOLD + getName() + ChatColor.YELLOW + ".");

            for (Player staff : Bukkit.getOnlinePlayers()){
              if(staff.getGameMode() == GameMode.CREATIVE){

                if(currentCapper != null){
                  staff.sendMessage(ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " " + capper.getName() + ChatColor.YELLOW + " is attempting to control " + ChatColor.GOLD + getName() + ChatColor.YELLOW + ".");
                }
              }
            }
          }
        }

        if (remainingCapTime <= 0) {
          finishCapping();
        } else {
          Main.getInstance().getServer().getPluginManager().callEvent(new EventControlTickEvent(this));
        }

        this.remainingCapTime--;
      }
    } else {
      List<Player> onCap = new ArrayList<>();

      for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {

        if (onCap(player.getLocation()) && !player.isDead()
            && player.getGameMode() != GameMode.CREATIVE
            && !player.hasMetadata("invisible")
            && !SpectatorListener.spectators.contains(player.getUniqueId())
            && !HCFProfile.get(player).hasPvPTimer()) {
          onCap.add(player);
          //player.setGameMode(GameMode.ADVENTURE);
        }
      }

      Collections.shuffle(onCap);

      if (onCap.size() != 0) {
        startCapping(onCap.get(0));
      }
    }

    List<Player> onCap = new ArrayList<>();

    for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {

      if (onCap(player.getLocation()) && !player.isDead()
              && player.getGameMode() != GameMode.CREATIVE
              && !player.hasMetadata("invisible")
              && !SpectatorListener.spectators.contains(player.getUniqueId())
              && !HCFProfile.get(player).hasPvPTimer()) {
        onCap.add(player);
      }
    }

    for (Player staff : Bukkit.getOnlinePlayers()){
      if(staff.getGameMode() == GameMode.CREATIVE){
        HCFProfile profile = HCFProfile.get(staff);

        if(currentCapper != null){

          if (profile.isOnlyShowCaper()){
            onCap.forEach(player -> {
              if(!player.getName().equals(currentCapper)){
                staff.hidePlayer(player);
              }else {
                staff.showPlayer(player);
              }
            });
          }

        }
      }
    }

  }

  public boolean onCap(Location location) {
    if (!location.getWorld().getName().equalsIgnoreCase(world)) {
      return (false);
    }

    int xDistance = FastMath.abs(location.getBlockX() - capLocation.getBlockX());
    int yDistance = FastMath.abs(location.getBlockY() - capLocation.getBlockY());
    int zDistance = FastMath.abs(location.getBlockZ() - capLocation.getBlockZ());

    return xDistance <= capDistance && yDistance <= 5 && zDistance <= capDistance;
  }
}