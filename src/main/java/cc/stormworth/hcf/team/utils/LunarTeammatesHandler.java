package cc.stormworth.hcf.team.utils;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LunarTeammatesHandler implements Runnable {

    @Getter
    public static boolean teamview = true;

    @Override
    public void run() {
        if (!teamview) return;
        for (Player online : LunarClientAPI.getInstance().getPlayersRunningLunarClient()) {
            Team team = Main.getInstance().getTeamHandler().getTeam(online);
            Map<UUID, Map<String, Double>> players = new HashMap<>();
            if (team != null) {
                for (Player teammate : team.getOnlineMembers()) {
                    if (teammate == online) continue;
                    Map<String, Double> position = new HashMap<>();
                    position.put("x", teammate.getLocation().getX());
                    position.put("y", teammate.getLocation().getY());
                    position.put("z", teammate.getLocation().getZ());
                    players.put(teammate.getUniqueId(), position);
                }
                LunarClientAPI.getInstance().sendTeammates(online, new LCPacketTeammates(team.getOwner(), System.currentTimeMillis(), players));
            } else {
                LunarClientAPI.getInstance().sendTeammates(online, new LCPacketTeammates(online.getUniqueId(), System.currentTimeMillis(), players));
            }
        }
    }
}