package cc.stormworth.hcf.misc.war.listener;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import cc.stormworth.hcf.misc.war.event.FactionWarMatchTerminateEvent;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch.FactionWarMatchState;
import cc.stormworth.hcf.misc.war.util.Task;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public final class FactionWarMatchListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        FactionWarMatch match = war.getMatch(player);

        if (match == null || !match.isAlive(player)) {
            return;
        }

        Location location = player.getLocation().clone();

        Task.runLater(() -> {
            //player.spigot().respawn();
            player.teleport(location.add(0.0D, 0.25D, 0.0D));

            match.setDead(player);
            match.tryFinish();
        }, 1L);

        Task.runLater(() -> event.getDrops().clear(), 200L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        FactionWarMatch match = war.getMatch(player);

        if (match == null || !match.isAlive(player)) {
            return;
        }

        if (match.getState() == FactionWarMatchState.STARTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        this.onEntityDamage(event);
    }

    @EventHandler
    public void onFactionWarMatchTerminate(FactionWarMatchTerminateEvent event) {
        FactionWar war = event.getWar();
        FactionWarMatch match = event.getMatch();

        war.getActiveMatches().remove(match);

        FactionWarParticipant winnerTeam = event.getWinnerTeam();

        if (winnerTeam == null) {
            FactionWarParticipant team1 = match.getTeam1();
            FactionWarParticipant team2 = match.getTeam2();

            war.getFactionsMatchesCache().remove(team1.getFactionUUID());
            war.getFactionsMatchesCache().remove(team2.getFactionUUID());

            war.removeParticipant(team1, true);
            war.removeParticipant(team2, true);
        } else {
            FactionWarParticipant loserTeam = winnerTeam.equals(match.getTeam1()) ? match.getTeam2() : match.getTeam1();

            war.getFactionsMatchesCache().remove(winnerTeam.getFactionUUID());
            war.getFactionsMatchesCache().remove(loserTeam.getFactionUUID());

            war.removeParticipant(loserTeam, true);
        }

        war.tryFinishRound();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        FactionWarMatch match = war.getMatch(player);

        if (match == null || !match.isAlive(player)) {
            return;
        }

        match.getDroppedItemsCache().add(event.getItemDrop());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        FactionWarMatch match = war.getMatch(player);

        if (match == null || !match.isAlive(player)) {
            return;
        }

        Item item = event.getItem();

        if (match.getDroppedItemsCache().contains(item)) {
            match.getDroppedItemsCache().remove(item);
        }
    }
}
