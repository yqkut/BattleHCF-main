package cc.stormworth.hcf.giveaway;

import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.giveaway.listeners.GiveAwayListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Setter
@Getter
public class GiveAwayHandler {

  private GiveAway currentGiveAway;

  public GiveAwayHandler() {
    rCommandHandler.registerPackage(Main.getInstance(), "cc.stormworth.hcf.giveaway.commands");
    Bukkit.getPluginManager().registerEvents(new GiveAwayListener(Main.getInstance()), Main.getInstance());
  }

}