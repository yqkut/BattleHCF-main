package cc.stormworth.hcf.team.duel;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.util.Task;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.duel.arena.FactionDuelArena;
import cc.stormworth.hcf.team.duel.event.FactionDuelMatchTerminateEvent;
import cc.stormworth.hcf.team.duel.listener.FactionDuelMatchBuildListener;
import cc.stormworth.hcf.team.duel.listener.FactionDuelMatchListener;
import cc.stormworth.hcf.team.duel.listener.FactionDuelMatchSpectatingListener;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatch;
import cc.stormworth.hcf.team.duel.match.FactionDuelMatchTeam;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class FactionDuelManager implements Listener {

	private final Set<FactionDuelArena> arenas = Sets.newHashSet();

	private final Main plugin;

	private final Set<FactionDuelMatch> activeMatches = Sets.newHashSet();
	private final Map<ObjectId, FactionDuelMatch> matchesCache = Maps.newHashMap();

	private final Map<ObjectId, ObjectId> invites = Maps.newHashMap();
	@Setter private boolean factionDuelEnabled;
	
	public FactionDuelManager(final Main plugin) {
		factionDuelEnabled = true;
		this.load();

		this.plugin = plugin;

		if(plugin.getMapHandler().isKitMap()){
			Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
			Bukkit.getPluginManager().registerEvents(new FactionDuelMatchListener(), Main.getInstance());
			Bukkit.getPluginManager().registerEvents(new FactionDuelMatchBuildListener(), Main.getInstance());
			Bukkit.getPluginManager().registerEvents(new FactionDuelMatchSpectatingListener(), Main.getInstance());
		}
	}
	
	public void load() {
		this.arenas.clear();

		ConfigFile arenasFile = Main.getInstance().getArenasDuelConfig();
		
		if (arenasFile.getConfig().contains("FACTION_DUEL_ARENAS")) {
			arenasFile.getConfig().getConfigurationSection("FACTION_DUEL_ARENAS").getKeys(false).forEach(key -> {
				ConfigurationSection section = arenasFile.getConfig().getConfigurationSection("FACTION_DUEL_ARENAS." + key);
				FactionDuelArena arena = new FactionDuelArena(key);
				
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
		ConfigFile arenasFile = Main.getInstance().getArenasDuelConfig();
		
		arenasFile.getConfig().set("FACTION_DUEL_ARENAS", null);
		
		this.arenas.forEach(arena -> {
			ConfigurationSection section = arenasFile.getConfig().createSection("FACTION_DUEL_ARENAS." + arena.getName());
			
			if (arena.getTeam1Spawn() != null) {
				section.set("SPAWNS.TEAM_1", LocationUtil.parseLocation(arena.getTeam1Spawn()));
			}
			
			if (arena.getTeam2Spawn() != null) {
				section.set("SPAWNS.TEAM_2", LocationUtil.parseLocation(arena.getTeam2Spawn()));
			}
			
			section.set("ENABLED", arena.isEnabled());
		});
		
		arenasFile.save();
	}
	
	public FactionDuelArena getArena(String name) {
		return this.arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public List<FactionDuelArena> getAllAvailableArenas() {
		return this.arenas.stream().filter(arena -> arena.isEnabled() && !arena.isInUse()).collect(Collectors.toList());
	}
	
	public FactionDuelMatch getMatch(ObjectId factionUUID) {
		return this.matchesCache.get(factionUUID);
	}
	
	public FactionDuelMatch getMatch(Team faction) {
		return this.getMatch(faction.getUniqueId());
	}
	
	public FactionDuelMatch getMatch(Player player) {
		Team faction = Main.getInstance().getTeamHandler().getTeam(player);
		
		if (faction == null) {
			return null;
		}
		
		return this.getMatch(faction);
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
		// TODO: do this
		return true;
	}

	public FactionDuelArena getRandomArena() {
		FactionDuelArena arena = null;

		List<FactionDuelArena> availableArenas = this.getAllAvailableArenas();

		if (!availableArenas.isEmpty()) {
			arena = availableArenas.get(Main.RANDOM.nextInt(availableArenas.size()));
		}

		return arena;
	}

	public void sentInvite(Team team, Team target) {

		if (getAllAvailableArenas().isEmpty()){
			team.sendMessage(ChatColor.RED + "&cThere are no available arenas.");
			return;
		}

		this.invites.put(team.getUniqueId(), target.getUniqueId());

		team.sendMessage(CC.translate("&eYour faction have invited &6" + target.getName() + "&e to a faction duel."));

		target.sendMessage(CC.translate("&6" + team.getName() + " &ehas invited you to a faction duel."));
		Player leaderTarget = Bukkit.getPlayer(target.getOwner());

		Clickable clickable = new Clickable("&eType &6/factionduel accept " + team.getName() + " &eto accept the invite. &7Or click here to accept.",
				ChatColor.AQUA + "[Click to accept]",
				"/factionduel accept " + team.getName());

		clickable.sendToPlayer(leaderTarget);
	}

	public void accept(Team team, Team target) {

		if (target == null) {
			team.sendMessage(CC.translate("&cThe other team has left the faction."));
			return;
		}

		this.invites.remove(target.getUniqueId());

		FactionDuelMatchTeam team1 = new FactionDuelMatchTeam(team);
		FactionDuelMatchTeam team2 = new FactionDuelMatchTeam(target);

		FactionDuelMatch match = new FactionDuelMatch(getRandomArena(), team1, team2);

		this.matchesCache.put(team.getUniqueId(), match);
		this.matchesCache.put(target.getUniqueId(), match);
		this.activeMatches.add(match);

		match.startCountdown();
	}

	public boolean isInMatch(Team team) {
		return this.matchesCache.containsKey(team.getUniqueId());
	}

	public boolean hasInvite(Team target, Team other) {
		return this.invites.containsKey(other.getUniqueId()) && this.invites.get(other.getUniqueId()).equals(target.getUniqueId());
	}

	@EventHandler
	public void onFactionDuelMatchTerminate(FactionDuelMatchTerminateEvent event) {
		FactionDuelMatch match = event.getMatch();
		
		FactionDuelMatchTeam winner = event.getWinnerTeam();
		FactionDuelMatchTeam loser = winner == match.getTeam1() ? match.getTeam2() : match.getTeam1();
		
		this.activeMatches.remove(match);
		
		this.matchesCache.remove(winner.getFactionUUID());
		this.matchesCache.remove(loser.getFactionUUID());
	}
}
