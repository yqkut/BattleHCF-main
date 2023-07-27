package cc.stormworth.hcf.misc.war.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWar.FactionWarState;
import cc.stormworth.hcf.misc.war.FactionWarManager;
import cc.stormworth.hcf.misc.war.FactionWarParticipant;
import cc.stormworth.hcf.misc.war.event.FactionWarMatchEndEvent;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch.FactionWarMatchState;
import cc.stormworth.hcf.misc.war.util.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class FactionWarDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        if (war.getState() != FactionWarState.PLAYING) {
            FactionWarParticipant participant = war.getParticipantByPlayer(player.getUniqueId());

            if (participant == null) {
                return;
            }

            participant.getDisconnectedMembersMap().add(player.getUniqueId());
            participant.getFaction().sendMessage(CC.translate("&c" + player.getName() + " must log in before a match starts, otherwise your faction will be disqualified from the faction war."));
        } else {
            FactionWarMatch match = war.getMatch(player);

            if (match == null) {
                return;
            }

            FactionWarParticipant playerFaction = match.getParticipantsCache().get(player.getUniqueId());

            if (playerFaction == null) {
                return;
            }

            playerFaction.getDisconnectedMembersMap().add(player.getUniqueId());
            playerFaction.getFaction().sendMessage(CC.translate("&c" + player.getName() + " must reconnect within 30 seconds or before the match ends, otherwise your faction will be disqualified from the faction war."));

            Task.runLater(() -> {
                if (match.getState() != FactionWarMatchState.PLAYING) {
                    return;
                }

                if (!playerFaction.getDisconnectedMembersMap().contains(player.getUniqueId())) {
                    return;
                }

                playerFaction.getAliveInMatch().clear();
                playerFaction.getFaction().sendMessage(CC.translate("&cYour faction was disqualified from the faction war due to a member went offline!"));

                match.tryFinish();
            }, FactionWarManager.DISCONNECT_TIME * 20L);
        }
    }

    @EventHandler
    public void onFactionWarMatchEnd(FactionWarMatchEndEvent event) {
        FactionWarParticipant winner = event.getWinnerTeam();

        if (!winner.hasDisconnectedMembers()) {
            return;
        }

        FactionWarMatch match = event.getMatch();
        FactionWarParticipant loser = winner == match.getTeam1() ? match.getTeam2() : match.getTeam1();

        if (!loser.hasDisconnectedMembers()) {
            winner.getFaction().sendMessage(CC.translate("&cYour faction was disqualified from the faction war due to a member went offline!"));

            event.setWinnerTeam(loser);
        } else {
            winner.getFaction().sendMessage(CC.translate("&cYour faction was disqualified from the faction war due to a member went offline!"));
            loser.getFaction().sendMessage(CC.translate("&cYour faction was disqualified from the faction war due to a member went offline!"));

            event.setWinnerTeam(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        FactionWarManager.updateVisibility(player);

        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return;
        }

        FactionWarParticipant participant = war.getParticipantByPlayer(player.getUniqueId());

        if (participant == null) {
            return;
        }

        participant.getDisconnectedMembersMap().remove(player.getUniqueId());
    }
}
