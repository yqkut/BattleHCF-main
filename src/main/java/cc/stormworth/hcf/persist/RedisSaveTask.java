package cc.stormworth.hcf.persist;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class RedisSaveTask implements Runnable {

    public static int save(final CommandSender issuer, final boolean forceAll) {
        final long startMs = System.currentTimeMillis();
        if(Main.getInstance().getBattlePlayers() != null) Main.getInstance().getBattlePlayers().save();
        final int teamsSaved = CorePlugin.getInstance().runRedisCommand(redis -> {
            int changed = 0;
            for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
                if (team.isNeedsSave() || forceAll) {
                    ++changed;
                    redis.set(Main.DATABASE_NAME + "." + team.getName().toLowerCase(), team.saveString(true));
                }
                if (forceAll) {
                    for (UUID member : team.getMembers()) {
                        Main.getInstance().getTeamHandler().setTeam(member, team);
                    }
                }
            }
            if (issuer != null && forceAll) {
                redis.save();
            }
            return changed;
        });
        final int time = (int) (System.currentTimeMillis() - startMs);
        if (teamsSaved != 0) {
            System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
            if (issuer != null) {
                issuer.sendMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
            }
        }
        return teamsSaved;
    }

    public void run() {
        save(null, false);
    }
}