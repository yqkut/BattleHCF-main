package cc.stormworth.hcf.team;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.command.rCommandHandler;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.team.dtr.DTRBitmaskType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamHandler {

    private final Map<ObjectId, Team> teams = Maps.newHashMap();


    public TeamHandler() {

        rCommandHandler.registerParameterType(Team.class, new TeamType());
        rCommandHandler.registerParameterType(DTRBitmask.class, new DTRBitmaskType());

        CorePlugin.getInstance().runRedisCommand((redis) -> {
            for (final String key : redis.keys(Main.DATABASE_NAME + ".*")) {
                String loadString = redis.get(key);
                try {
                    Team team = new Team(key.split("\\.")[1]);
                    team.load(loadString);
                    TeamHandler.this.setupTeam(team);
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.getInstance().getLogger().severe("Could not load team from raw string: " + loadString);
                }
            }
            return null;
        });
    }

    public List<Team> getTeams() {
        return ImmutableList.copyOf(this.teams.values());
    }

    public Team getTeam(String teamName) {

        if (teamName == null || teamName.isEmpty()) {
            return null;
        }

        for (Team team : this.teams.values()) {
            if (team.getName().equalsIgnoreCase(teamName)) {
                return team;
            }
        }

        return null;
    }

    public Team getTeam(ObjectId teamUUID) {
        return (teamUUID == null) ? null : teams.get(teamUUID);
    }

    public Team getTeam(UUID playerUUID) {

        if (playerUUID == null) {
            return null;
        }

        HCFProfile profile = HCFProfile.getByUUID(playerUUID);

        if (profile == null) {

            for (Team team : this.teams.values()) {
                if (team.getMembers().contains(playerUUID)) {
                    return team;
                }
            }

            return null;
        }

        return profile.getTeam();
    }

    public Team getTeam(final Player player) {
        return this.getTeam(player.getUniqueId());
    }
    public void setTeam(UUID uuid, Team team) {
        HCFProfile profile = HCFProfile.getByUUID(uuid);

        if (profile == null) {

            CompletableFuture<HCFProfile> future = HCFProfile.load(uuid);

            future.thenAccept(profile1 -> {
                profile1.setTeam(team);
                profile1.asyncSave();
            });

            return;
        }

        profile.setTeam(team);
    }

    public void setTeam(final Player player, final Team team) {
        this.setTeam(player.getUniqueId(), team);
    }

    public void claimBufferKoth(Location corner1, Location corner2) {
        Team team = Main.getInstance().getTeamHandler().getTeam("RestrictedZone");

        Claim bufferclaim = new Claim(corner1, corner2);
        bufferclaim.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        bufferclaim.setY1(0);
        bufferclaim.setY2(256);

        LandBoard.getInstance().setTeamAt(bufferclaim, team);
        team.getClaims().add(bufferclaim);
        team.flagForSave();
    }

    public void setupTeam(final Team team) {
        /*this.teamNameMap.put(team.getName().toLowerCase(), team);
        this.teamUniqueIdMap.put(team.getUniqueId(), team);*/
        teams.put(team.getUniqueId(), team);
        for (final UUID member : team.getMembers()) {
            this.setTeam(member, team);
        }
    }

    public void removeTeam(final Team team) {
        /*this.teamNameMap.remove(team.getName().toLowerCase());
        this.teamUniqueIdMap.remove(team.getUniqueId());*/

        teams.remove(team.getUniqueId());
        for (final UUID member : team.getMembers()) {
            this.setTeam(member, null);
        }
    }

    public void recachePlayerTeams() {
        //this.playerTeamMap.clear();
        for (final Team team : Main.getInstance().getTeamHandler().getTeams()) {
            for (final UUID member : team.getMembers()) {
                this.setTeam(member, team);
            }
        }
    }
}