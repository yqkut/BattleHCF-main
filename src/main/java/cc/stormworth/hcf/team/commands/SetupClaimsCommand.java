package cc.stormworth.hcf.team.commands;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.listener.BorderListener;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.Claim;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SetupClaimsCommand {
    @Command(names = {"setupclaims"}, permission = "op")
    public static void claimall(final Player player, @Param(name = "spawnSize") final int spawnSize, @Param(name = "roadSize") final int roadSize) {
        if (!BorderListener.borders.containsKey(player.getWorld().getName()))
            BorderListener.setBorder(player.getWorld().getName(), player.getWorld().getEnvironment() == World.Environment.NORMAL ? 2000 : 1500);
        Team spawn = Main.getInstance().getTeamHandler().getTeam("Spawn");
        if (spawn != null) {
            Claim bufferclaim = new Claim(new Location(player.getWorld(), spawnSize, 0, -spawnSize), new Location(player.getWorld(), -spawnSize, 0, spawnSize));
            bufferclaim.setName(spawn.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
            bufferclaim.setY1(0);
            bufferclaim.setY2(256);

            LandBoard.getInstance().setTeamAt(bufferclaim, spawn);
            spawn.getClaims().add(bufferclaim);
            spawn.flagForSave();
        }

        Team north = Main.getInstance().getTeamHandler().getTeam("NorthRoad");
        Team south = Main.getInstance().getTeamHandler().getTeam("SouthRoad");
        Team west = Main.getInstance().getTeamHandler().getTeam("EastRoad");
        Team east = Main.getInstance().getTeamHandler().getTeam("WestRoad");
        claimTeam(north, new Location(player.getWorld(), -roadSize, 0, -spawnSize - 1), new Location(player.getWorld(), roadSize, 0, (-BorderListener.borders.get(player.getWorld().getName()) - 10)));
        claimTeam(south, new Location(player.getWorld(), roadSize, 0, spawnSize + 1), new Location(player.getWorld(), -roadSize, 0, (BorderListener.borders.get(player.getWorld().getName()) + 10)));
        claimTeam(east, new Location(player.getWorld(), -spawnSize - 1, 0, roadSize), new Location(player.getWorld(), (-BorderListener.borders.get(player.getWorld().getName()) - 10), 0, -roadSize));
        claimTeam(west, new Location(player.getWorld(), spawnSize + 1, 0, -roadSize), new Location(player.getWorld(), (BorderListener.borders.get(player.getWorld().getName()) + 10), 0, roadSize));
        player.sendMessage(CC.YELLOW + "You have claimed spawn and roads.");
    }

    public static void claimTeam(Team team, Location corner1, Location corner2) {
        if (team == null) return;
        Claim bufferclaim = new Claim(corner1, corner2);
        bufferclaim.setName(team.getName() + "_" + (100 + CorePlugin.RANDOM.nextInt(800)));
        bufferclaim.setY1(0);
        bufferclaim.setY2(256);

        LandBoard.getInstance().setTeamAt(bufferclaim, team);
        team.getClaims().add(bufferclaim);
        team.flagForSave();
    }
}