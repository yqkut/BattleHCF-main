package cc.stormworth.hcf.events.dtc;

import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.events.events.EventActivatedEvent;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.events.events.EventDeactivatedEvent;
import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DTC implements Event {

    @Getter
    public static int startingPoints = 500;
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
    private boolean hidden = false;
    @Getter
    private final Map<Team, Integer> CoresBroken;
    @Getter
    private int currentPoints = 0;
    @Getter
    private long lastBlockBreak = -1L;
    @Getter
    private long lastPointIncrease = -1L;

    @Getter
    private final EventType type = EventType.DTC;

    public DTC(String name, Location location) {
        this.name = name;
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        this.currentPoints = startingPoints;
        CoresBroken = new HashMap<>();
        CoresBroken.clear();
        Main.getInstance().getEventHandler().getEvents().add(this);
        Main.getInstance().getEventHandler().saveEvents();
    }

    public void setLocation(Location location) {
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        Main.getInstance().getEventHandler().saveEvents();
    }

    @Override
    public void tick() {
        if (this.currentPoints == startingPoints) {
            return;
        } else if (startingPoints <= this.currentPoints) {
            this.currentPoints = startingPoints;
        }

        long timeSinceLastBlockBreak = System.currentTimeMillis() - lastBlockBreak;
        long timeSinceLastPointIncrease = System.currentTimeMillis() - lastPointIncrease;

        if (TimeUnit.SECONDS.toMillis(5) <= timeSinceLastBlockBreak && TimeUnit.SECONDS.toMillis(5) <= timeSinceLastPointIncrease) {
            this.currentPoints++;
            this.lastPointIncrease = System.currentTimeMillis();

            if (this.currentPoints % 10 == 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is regenerating &9[" + this.getCurrentPoints() + "]"));
            }
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        Main.getInstance().getEventHandler().saveEvents();
    }

    public boolean activate() {
        if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")) return (false);
        if (active) {
            return (false);
        }

        Main.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));

        this.active = true;
        this.currentPoints = startingPoints;

        return (true);
    }

    public boolean deactivate() {
        if (!active) {
            return (false);
        }

        Main.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));

        this.active = false;
        this.currentPoints = startingPoints;

        return (true);
    }

    public void blockBroken(Player player) {
        if (--this.currentPoints <= 0) {
            TaskUtil.run(Main.getInstance(), () -> Main.getInstance().getServer().getPluginManager().callEvent(new EventCapturedEvent(this, player)));
            for (Team team : CoresBroken.keySet()) {
                if (team != null) {
                    team.addPoints((CoresBroken.get(team) / 10) * 2);
                }
            }
            deactivate();
        }

        Team team = Main.getInstance().getTeamHandler().getTeam(player);
        if (team != null) {
            CoresBroken.put(team, CoresBroken.getOrDefault(team, 1));
        }
        if (this.currentPoints % 10 == 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is being broken &9[" + this.getCurrentPoints() + "]"));
        }

        this.lastBlockBreak = System.currentTimeMillis();
    }
}