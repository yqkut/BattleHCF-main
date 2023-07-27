package cc.stormworth.hcf.events.mad;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.events.events.EventCapturedEvent;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.koth.events.KOTHControlLostEvent;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.util.player.Players;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedHashMap;

public class MadGame implements Listener {

    @Getter
    public static LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();
    public static final String EVENT_NAME = "Mad";
    private final int POINTS_TO_WIN = 5;
    @Getter private static boolean started;
    public static final String PREFIX = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[Mad]";

    public static BukkitTask task;

    public MadGame(){
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public static void start(){
        for (Event event : Main.getInstance().getEventHandler().getEvents()) {
            if (event.getType() != EventType.KOTH) {
                continue;
            }

            if (!event.getName().equals(EVENT_NAME)) {
                continue;
            }

            WaypointManager.updateKoTHWaypoint((KOTH) event, true);

            KOTH koth = (KOTH) event;

            koth.setCapTime(180);

            koth.activate();

            started = true;

            Team team = Main.getInstance().getTeamHandler().getTeam(EVENT_NAME);

            team.getClaims().forEach(coordinates ->
                    coordinates.getPlayers().forEach(player ->
                            Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0))));

            Bukkit.broadcastMessage(PREFIX + " " + ChatColor.GOLD + "Mad has started!");
            break;
        }

        TitleBuilder titleBuilder = new TitleBuilder( "&4&lMad Event", "&7has been &aactivated&7.", 20, 60, 20);

        for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
            titleBuilder.send(player);

            player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1F, 1F);
        }

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            Event event = Main.getInstance().getEventHandler().getEvent(EVENT_NAME);

            if(event == null || !event.isActive()){
                task.cancel();
                return;
            }

            Team team = Main.getInstance().getTeamHandler().getTeam(EVENT_NAME);

            if(team == null){
                task.cancel();
                return;
            }

            for (Player other : Bukkit.getOnlinePlayers()) {
                if(!other.hasMetadata("in_mad") && team.isInClaim(other)){
                    other.setMetadata("in_mad", new FixedMetadataValue(Main.getInstance(), true));
                    Main.getInstance().getEffectRestorer().setRestoreEffect(other, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                }else if (other.hasMetadata("in_mad") && !team.isInClaim(other)){
                    PotionEffect activeEffect = Players.getActivePotionEffect(other, PotionEffectType.INCREASE_DAMAGE);

                    if (activeEffect != null && activeEffect.getAmplifier() != 0) {
                        return;
                    }

                    other.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    other.removeMetadata("in_mad", Main.getInstance());
                }
            }

        }, 0L, 10L);

        Main.getInstance().getEventHandler().setScheduleEnabled(false);
    }

    public static void endGame(Team team, Player capper) {
        if (capper == null) {
            Bukkit.broadcastMessage(CC.translate(PREFIX + " &ehas ended."));
        } else {
            Bukkit.broadcastMessage(PREFIX + ChatColor.GOLD + ChatColor.BOLD + " " +  team.getName() + ChatColor.YELLOW + " has won Mad Event!");
        }


        if (team != null) {
            team.setMadCaptures(team.getMadCaptures() + 1);

            Player leader = Bukkit.getPlayer(team.getOwner());

            /*if (leader != null) {
                HCFProfile profile = HCFProfile.get(leader);

                profile.addGems(100);

                leader.sendMessage(CC.translate("&eYou have been awarded 400 &6Gems &efor capturing the &6&lMAD&e."));
            } else {
                if (capper != null) {
                    HCFProfile profile = HCFProfile.get(capper);

                    profile.addGems(100);

                    capper.sendMessage(CC.translate("&eYou have been awarded 400 &6Gems &efor capturing the &6&lMAD&e."));
                }
            }*/
        }


        Team teaEvent = Main.getInstance().getTeamHandler().getTeam(EVENT_NAME);
        teaEvent.getClaims().forEach(coordinates ->
                coordinates.getPlayers().forEach(player ->
                        Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0))));

        Event event = Main.getInstance().getEventHandler().getEvent(EVENT_NAME);

        Team teamEvent = Main.getInstance().getTeamHandler().getTeam(EVENT_NAME);


        teamEvent.getClaims().forEach(coordinates ->
                coordinates.getPlayers().forEach(player -> player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE)));

        if (event != null) {
            event.deactivate();
        }

        teamPoints.clear();
        task.cancel();
        task = null;
        started = false;
        Main.getInstance().getEventHandler().setScheduleEnabled(true);
    }

    @EventHandler
    public void onKOTHCaptured(EventCapturedEvent event) {

        if (!event.getEvent().getName().equalsIgnoreCase(EVENT_NAME)) {
            return;
        }

        Team team = Main.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team == null) {
            return;
        }

        if (teamPoints.containsKey(team.getUniqueId())) {
            teamPoints.put(team.getUniqueId(), teamPoints.get(team.getUniqueId()) + 1);
        } else {
            teamPoints.put(team.getUniqueId(), 1);
        }

        Bukkit.broadcastMessage(
                CC.translate(PREFIX + " &6&l" + team.getName() + " &ecaptured &4&lMad Event &eand earned a point! &b(" + teamPoints.get(team.getUniqueId())
                        + "/" + POINTS_TO_WIN + ")"));

        if (teamPoints.get(team.getUniqueId()) >= POINTS_TO_WIN) {
            endGame(team, event.getPlayer());
        } else {
            if (MadGame.isStarted()) {
                new BukkitRunnable() {
                    public void run() {
                        KOTH koth = (KOTH) event.getEvent();

                        koth.setCapTime(180);
                        event.getEvent().activate();
                    }
                }.runTaskLater(Main.getInstance(), 20L);
            }
        }
    }


    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().equalsIgnoreCase(EVENT_NAME)) {
            return;
        }

        if (event.getKOTH().getCurrentCapper() == null) {
            return;
        }

        Team team = Main.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));

        if (team == null) {
            return;
        }

        //teamPoints.remove(team.getUniqueId());

        team.sendMessage(PREFIX + ChatColor.GOLD + " " + event.getKOTH().getCurrentCapper() + ChatColor.YELLOW
                        + " was knocked off of " + ChatColor.DARK_RED + ChatColor.BOLD + "` Mad Event" + ChatColor.YELLOW
                        + "!");
    }

    //@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if (team == null) {
            return;
        }

        if (!team.getName().equals(EVENT_NAME)) {
            return;
        }

        if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null && Main.getInstance().getEventHandler().getEvent(EVENT_NAME).isActive()) {
            Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if(team == null){
            return;
        }

        if (!team.getName().equals(EVENT_NAME)) {
            return;
        }

        if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null) {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
    }

    //@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();

        Player player = event.getPlayer();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        Team teamTo = LandBoard.getInstance().getTeam(to);
        Team teamFrom = LandBoard.getInstance().getTeam(from);

        if (teamFrom != teamTo) {

            if (teamFrom != null && teamFrom.getName().equals(EVENT_NAME)) {
                if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
            }

           if (teamTo != null && teamTo.getName().equalsIgnoreCase(EVENT_NAME)) {

               if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null && Main.getInstance().getEventHandler().getEvent(EVENT_NAME).isActive()) {
                   Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
               }
           }
        }
    }

   // @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    //@EventHandler
    public void onDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            Team team = Main.getInstance().getTeamHandler().getTeam(player);

            if (team != null) {
                if (!team.getName().equals(EVENT_NAME)) {
                    return;
                }

                if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null && Main.getInstance().getEventHandler().getEvent(EVENT_NAME).isActive()) {
                    player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
            }
        } else {
            Team team = LandBoard.getInstance().getTeam(player.getLocation());

            if (team == null) {
                return;
            }

            if (team.isMember(player.getUniqueId()) && !team.isRaidable()) {
                if (!team.getName().equals(EVENT_NAME)) {
                    return;
                }

                if(Main.getInstance().getEventHandler().getEvent(EVENT_NAME) != null && Main.getInstance().getEventHandler().getEvent(EVENT_NAME).isActive()) {
                    Main.getInstance().getEffectRestorer().setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
                }
            }
        }
    }

}
