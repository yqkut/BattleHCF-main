package cc.stormworth.hcf.misc.lunarclient.waypoint;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.region.glowmtn.GlowHandler;
import cc.stormworth.hcf.listener.SetListener;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.event.LCPlayerRegisterEvent;
import com.lunarclient.bukkitapi.event.LCPlayerUnregisterEvent;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaypointManager implements Listener {

    @Getter private static Map<PlayerWaypointType, LunarClientWaypoint> waypoints;

    @Getter private static Map<PlayerWaypointType, LCWaypoint> globalWaypoints;
    @Getter private static Map<String, LCWaypoint> kothWaypoints;

    @Getter private static Table<UUID, PlayerWaypointType, LCWaypoint> playerWaypoints;

    private static PlayerWaypointType[] waypointTypes;
    private static LCPacketServerRule serverRulePacket;

    public WaypointManager() {
        waypoints = new HashMap<>();

        globalWaypoints = new HashMap<>();
        kothWaypoints = new HashMap<>();

        playerWaypoints = HashBasedTable.create();

        waypointTypes = PlayerWaypointType.values();
        serverRulePacket = new LCPacketServerRule(ServerRule.SERVER_HANDLES_WAYPOINTS, true);

        setupWaypoints();

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public static void updatePlayerFactionChange(Player player) {
        updateWaypoint(player, PlayerWaypointType.FACTION_RALLY);
        updateWaypoint(player, PlayerWaypointType.FACTION_HQ);
        updateWaypoint(player, PlayerWaypointType.FOCUSED_FACTION_HOME);
    }

    public static void registerPlayerWaypoints(Player player) {
        for (PlayerWaypointType type : waypointTypes) {
            updateWaypoint(player, type);
        }

        for (LCWaypoint waypoint : kothWaypoints.values()) {
            addWaypoint(player, waypoint);
        }
    }

    public static void addGlobalWaypoint(PlayerWaypointType type, Location location) {
        globalWaypoints.put(type, waypoints.get(type).createWaypoint(location));
    }

    public static void updateKoTHWaypoint(KOTH koth, boolean add) {
        if (!waypoints.containsKey(PlayerWaypointType.KOTH)) return;
        String name = koth.getName();
        if (add) {
            LCWaypoint waypoint = waypoints.get(PlayerWaypointType.KOTH).createWaypoint(koth.getCapLocation().toLocation(Bukkit.getWorld(koth.getWorld())), name);
            for (UUID uuid : Main.getInstance().getLunarClientManager().getPlayers()) {
                addWaypoint(Bukkit.getPlayer(uuid), waypoint);
            }
            kothWaypoints.put(name, waypoint);
        } else if (kothWaypoints.containsKey(name)) {
            LCWaypoint waypoint = kothWaypoints.remove(name);

            for (UUID uuid : Main.getInstance().getLunarClientManager().getPlayers()) {
                removeWaypoint(Bukkit.getPlayer(uuid), waypoint);
            }
        }
    }

    public static void updateGlobalWaypoints(PlayerWaypointType type, boolean update) {
        if (!waypoints.containsKey(type)) return;

        if (update) {
            if (globalWaypoints.containsKey(type)) {
                for (UUID uuid : Main.getInstance().getLunarClientManager().getPlayers()) {
                    removeWaypoint(Bukkit.getPlayer(uuid), globalWaypoints.get(type));
                }
            }
            globalWaypoints.remove(type);
        }

        switch (type) {
            case SPAWN: {
                Location spawn = Main.getInstance().getServerHandler().getSpawnLocation();
                if (spawn != null) {
                    addGlobalWaypoint(type, spawn);
                }
                break;
            }
            case NETHER_SPAWN: {
                if (Bukkit.getWorld("world_nether") == null) return;
                Location spawn = Bukkit.getWorld("world_nether").getSpawnLocation();
                if (spawn != null) {
                    addGlobalWaypoint(type, spawn);
                }
                break;
            }
            case END_SPAWN: {
                if (Bukkit.getWorld("world_the_end") == null) return;
                Location spawn = Bukkit.getWorld("world_the_end").getSpawnLocation();
                if (spawn != null) {
                    addGlobalWaypoint(type, spawn);
                }
                break;
            }
            case END_RETURN: {
                Location spawn = SetListener.getEndreturn();
                if (spawn != null) {
                    addGlobalWaypoint(type, spawn);
                }
                break;
            }
            case GLOWSTONE: {
                if (Main.getInstance().getGlowHandler() == null) return;
                Team team = Main.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());
                if (team == null) return;
                Location glowstone = team.getHQ();
                if (glowstone != null) {
                    addGlobalWaypoint(type, glowstone);
                }
                break;
            }
            case SUPPLY_DROP: {

                if (!Main.getInstance().getMapHandler().isKitMap()){
                    break;
                }

            	Location location = Main.getInstance().getSupplyDropManager().getLastLocation();
            	
            	if (location != null) {
            		addGlobalWaypoint(type, location);
            	}
            	
            	break;
            }
        }

        if (update) {
            for (UUID uuid : Main.getInstance().getLunarClientManager().getPlayers()) {
                for (PlayerWaypointType pwt : globalWaypoints.keySet()) {
                    updateWaypoint(Bukkit.getPlayer(uuid), pwt);
                }
            }
        }
    }

    public static void updateWaypoint(Player player, PlayerWaypointType type) {
        if (player == null) return;

        LCWaypoint lcWaypoint = playerWaypoints.remove(player.getUniqueId(), type);

        if (lcWaypoint != null) {
            removeWaypoint(player, lcWaypoint);
        }

        if (!waypoints.containsKey(type)) return;

        LCWaypoint waypoint = null;
        LunarClientWaypoint typeWaypoint = waypoints.get(type);
        Team faction = Main.getInstance().getTeamHandler().getTeam(player);

        switch (type) {
            case SPAWN:
            case NETHER_SPAWN:
            case END_SPAWN:
            case END_RETURN:
            case DTC:
            case SUPPLY_DROP:
            case GLOWSTONE:
            case CONQUEST_RED:
            case CONQUEST_BLUE:
            case CONQUEST_GREEN:
            case CONQUEST_YELLOW: {
                waypoint = globalWaypoints.get(type);
                break;
            }
            case FACTION_RALLY: {
                if (faction != null && faction.getRally() != null) {
                    waypoint = typeWaypoint.createWaypoint(faction.getRally());
                }
                break;
            }
            case FACTION_HQ: {
                if (faction != null && faction.getHQ() != null) {
                    waypoint = typeWaypoint.createWaypoint(faction.getHQ());
                }
                break;
            }
            case FOCUSED_FACTION_HOME: {
                if (faction != null && faction.getFocus() != null) {
                    Team focusedFaction = Main.getInstance().getTeamHandler().getTeam(faction.getFocus());

                    if (focusedFaction != null && focusedFaction.getHQ() != null) {
                        waypoint = typeWaypoint.createWaypoint(focusedFaction.getHQ(), focusedFaction.getName());
                    }
                }
                break;
            }
        }

        if (waypoint != null) {
            addWaypoint(player, waypoint);
            playerWaypoints.put(player.getUniqueId(), type, waypoint);
        }
    }

    private static void addWaypoint(Player player, LCWaypoint waypoint) {
        if (player == null) return;
        LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
    }

    public static void removeWaypoint(Player player, LCWaypoint waypoint) {
        if (player == null) return;
        LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
    }

    public void disable() {
        waypoints.clear();

        globalWaypoints.clear();
        kothWaypoints.clear();

        playerWaypoints.clear();
    }

    private void setupWaypoints() {

        waypoints.put(PlayerWaypointType.SPAWN, new LunarClientWaypoint(ChatColor.GREEN + "Spawn", 7077719));
        waypoints.put(PlayerWaypointType.NETHER_SPAWN, new LunarClientWaypoint("Nether Spawn", 16711680));
        waypoints.put(PlayerWaypointType.END_SPAWN, new LunarClientWaypoint("End Spawn", 7029482));
        waypoints.put(PlayerWaypointType.END_RETURN, new LunarClientWaypoint("End Return", 7029482));
        waypoints.put(PlayerWaypointType.GLOWSTONE, new LunarClientWaypoint("Glowstone", 15832106));
        waypoints.put(PlayerWaypointType.SUPPLY_DROP, new LunarClientWaypoint("Supply Drop", 9215));
        
        waypoints.put(PlayerWaypointType.KOTH, new LunarClientWaypoint("%name%", 7049706));

        waypoints.put(PlayerWaypointType.FACTION_RALLY, new LunarClientWaypoint("Faction Rally", 52936));
        waypoints.put(PlayerWaypointType.FACTION_HQ, new LunarClientWaypoint("HQ", 7077719));
        waypoints.put(PlayerWaypointType.FOCUSED_FACTION_HOME, new LunarClientWaypoint("%name% HQ", 16711680));

        for (PlayerWaypointType type : waypointTypes) {
            updateGlobalWaypoints(type, false);
        }
    }

    @EventHandler
    public void onPlayerRegisterLCEvent(LCPlayerRegisterEvent event) {
        Player player = event.getPlayer();
        LunarClientAPI.getInstance().sendPacket(player, serverRulePacket);
        //CorePlugin.getInstance().setMinimapStatus(player, MinimapStatus.NEUTRAL);
        registerPlayerWaypoints(player);
    }

    @EventHandler
    public void onPlayerUnregisterLC(LCPlayerUnregisterEvent event) {
        Player player = event.getPlayer();

        if (playerWaypoints.containsRow(player.getUniqueId())) {
            for (LCWaypoint waypoint : playerWaypoints.values()) {
                removeWaypoint(player, waypoint);
            }

            playerWaypoints.row(player.getUniqueId()).clear();
        }

        for (LCWaypoint waypoint : kothWaypoints.values()) {
            removeWaypoint(player, waypoint);
        }
    }

    private PlayerWaypointType getTypeByEnvironment(Environment environment) {
        switch (environment) {
            case NETHER:
                return PlayerWaypointType.NETHER_SPAWN;
            case THE_END:
                return PlayerWaypointType.END_SPAWN;
            default:
                return PlayerWaypointType.SPAWN;
        }
    }
}