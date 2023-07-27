package cc.stormworth.hcf.battleplayers.listeners;

import cc.stormworth.hcf.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class BattlePlayersListener implements Listener {

  private final Main plugin;

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (plugin.getBattlePlayers().getMap() != null &&
        plugin.getBattlePlayers().getMap().isStarted()) {

      plugin.getBattlePlayers().getMap().addPlayer(player);
    }
  }

}