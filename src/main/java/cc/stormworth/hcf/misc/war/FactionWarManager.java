package cc.stormworth.hcf.misc.war;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.arena.FactionWarArena;
import cc.stormworth.hcf.misc.war.event.FactionWarEndEvent;
import cc.stormworth.hcf.misc.war.listener.FactionWarDisconnectListener;
import cc.stormworth.hcf.misc.war.listener.FactionWarMatchBuildListener;
import cc.stormworth.hcf.misc.war.listener.FactionWarMatchListener;
import cc.stormworth.hcf.misc.war.listener.FactionWarMatchSpectatingListener;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import cc.stormworth.hcf.misc.war.util.Task;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class FactionWarManager implements Listener {

    @Getter
    private final Set<FactionWarArena> arenas = Sets.newHashSet();

    @Getter
    @Setter
    private FactionWar activeWar;

    private final Main plugin;

    public static final int DEFAULT_WARS_SIZE = 2;

    public static final int MAX_BARDS = 1;
    public static final int MAX_ARCHERS = 1;
    public static final int MAX_ROGUES = 1;

    public static final int WIN_POINTS_REWARD = 250;

    public static final long DISCONNECT_TIME = 30L;

    public FactionWarManager(final Main plugin) {
        this.load();
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new FactionWarDisconnectListener(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new FactionWarMatchListener(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new FactionWarMatchBuildListener(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new FactionWarMatchSpectatingListener(), Main.getInstance());
    }

    public void load() {
        this.arenas.clear();

        ConfigFile arenasFile = Main.getInstance().getArenasConfig();

        if (arenasFile.getConfig().contains("FACTION_WAR_ARENAS")) {
            arenasFile.getConfig().getConfigurationSection("FACTION_WAR_ARENAS").getKeys(false).forEach(key -> {
                ConfigurationSection section = arenasFile.getConfig().getConfigurationSection("FACTION_WAR_ARENAS." + key);
                FactionWarArena arena = new FactionWarArena(key);

                if (section.contains("SPAWNS.TEAM_1")) {
                    arena.setTeam1Spawn(LocationUtil.convertLocation(section.getString("SPAWNS.TEAM_1")));
                }

                if (section.contains("SPAWNS.TEAM_2")) {
                    arena.setTeam2Spawn(LocationUtil.convertLocation(section.getString("SPAWNS.TEAM_2")));
                }

                arena.setEnabled(section.getBoolean("ENABLED"));

                this.arenas.add(arena);
            });
        }
    }

    public void save() {
        ConfigFile arenasFile = Main.getInstance().getArenasConfig();

        arenasFile.getConfig().set("FACTION_WAR_ARENAS", null);

        this.arenas.forEach(arena -> {
            ConfigurationSection section = arenasFile.getConfig().createSection("FACTION_WAR_ARENAS." + arena.getName());

            if (arena.getTeam1Spawn() != null) {
                section.set("SPAWNS.TEAM_1", LocationUtil.convertLocation(arena.getTeam1Spawn().toString()));
            }

            if (arena.getTeam2Spawn() != null) {
                section.set("SPAWNS.TEAM_2", LocationUtil.convertLocation(arena.getTeam2Spawn().toString()));
            }

            section.set("ENABLED", arena.isEnabled());
        });

        arenasFile.save();
    }

    public FactionWarArena getArena(String name) {
        return this.arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Set<FactionWarArena> getAllAvailableArenas() {
        return this.arenas.stream().filter(arena -> arena.isEnabled() && !arena.isInUse()).collect(Collectors.toSet());
    }

    public Optional<FactionWarArena> findAvailableArena() {
        List<FactionWarArena> enabledArenas = Lists.newArrayList(this.getAllAvailableArenas());

        if (enabledArenas.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size())));
    }

    public static void updateVisibility(Player player) {
        Task.runAsync(() -> {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (viewer == player) {
                    continue;
                }

                if (shouldSee(player, viewer)) {
                    viewer.showPlayer(player);
                } else {
                    viewer.hidePlayer(player);
                }

                if (shouldSee(viewer, player)) {
                    player.showPlayer(viewer);
                } else {
                    player.hidePlayer(viewer);
                }
            }
        });
    }

    private static boolean shouldSee(Player player, Player viewer) {
        FactionWar war = Main.getInstance().getFactionWarManager().getActiveWar();

        if (war == null) {
            return true;
        }

        FactionWarMatch playerMatch = war.getMatch(player);
        FactionWarMatch viewerMatch = war.getMatch(viewer);

        if (playerMatch != null && viewerMatch != null && playerMatch == viewerMatch) {
            boolean isPlayerSpectating = playerMatch.isSpectating(player);
            boolean isViewerSpectating = playerMatch.isSpectating(viewer);

            if (!isPlayerSpectating && isViewerSpectating) {
                return true;
            }

            if (isPlayerSpectating & !isViewerSpectating) {
                return false;
            }
        }

        return true;
    }

    @EventHandler
    public void onFactionWarEnd(FactionWarEndEvent event) {
        this.setActiveWar(null);
    }
}
