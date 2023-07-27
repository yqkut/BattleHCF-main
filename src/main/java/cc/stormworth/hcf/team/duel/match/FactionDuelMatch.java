package cc.stormworth.hcf.team.duel.match;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.gkits.data.FlatFileKitManager;
import cc.stormworth.hcf.misc.war.util.PlayerCache;
import cc.stormworth.hcf.misc.war.util.Task;
import cc.stormworth.hcf.team.duel.FactionDuelManager;
import cc.stormworth.hcf.team.duel.arena.FactionDuelArena;
import cc.stormworth.hcf.team.duel.event.FactionDuelMatchEndEvent;
import cc.stormworth.hcf.team.duel.event.FactionDuelMatchStartCountdownEvent;
import cc.stormworth.hcf.team.duel.event.FactionDuelMatchStartEvent;
import cc.stormworth.hcf.team.duel.event.FactionDuelMatchTerminateEvent;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.common.base.Preconditions;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public final class FactionDuelMatch {

	private final FactionDuelArena arena;
	
	private FactionDuelMatchState state = FactionDuelMatchState.NONE;
	
	private long startedAt = 0L;
	private long endedAt = 0L;
	
	private final FactionDuelMatchTeam team1;
	private final FactionDuelMatchTeam team2;
	
	private final Set<Player> alivePlayers = Sets.newHashSet();
	private final Set<Player> spectators = Sets.newHashSet();
	
	private final Map<UUID, FactionDuelMatchTeam> participantsCache = Maps.newHashMap();
	
	private final Map<UUID, PlayerCache> playersStuffCache = Maps.newHashMap();
	private final Set<Item> droppedItemsCache = Sets.newConcurrentHashSet();
	
	public FactionDuelMatch(FactionDuelArena arena, FactionDuelMatchTeam team1, FactionDuelMatchTeam team2) {
		this.arena = Preconditions.checkNotNull(arena, "Arena can't be null.");
		
		Preconditions.checkNotNull(team1, "Team 1 can't be null.");
		Preconditions.checkNotNull(team2, "Team 2 can't be null.");

		(this.team1 = team1).getAliveMembers().forEach(player -> {
			this.alivePlayers.add(player);
			this.participantsCache.put(player.getUniqueId(), this.team1);
			this.playersStuffCache.put(player.getUniqueId(), new PlayerCache(player));
		});
		
		(this.team2 = team2).getAliveMembers().forEach(player -> {
			this.alivePlayers.add(player);
			this.participantsCache.put(player.getUniqueId(), this.team2);
			this.playersStuffCache.put(player.getUniqueId(), new PlayerCache(player));
		});
		
		this.arena.setInUse(true);
		
		this.startCountdown();
	}
	
	public void startCountdown() {
		if (this.state == FactionDuelMatchState.STARTING) {
			return;
		}
		
		this.state = FactionDuelMatchState.STARTING;
		
		Location team1Spawn = this.arena.getTeam1Spawn().clone().add(0.0D, 0.25D, 0.0D);
		Location team2Spawn = this.arena.getTeam2Spawn().clone().add(0.0D, 0.25D, 0.0D);
		
		if (!team1Spawn.getChunk().isLoaded()) {
			team1Spawn.getChunk().load();
		}
		
		if (!team2Spawn.getChunk().isLoaded()) {
			team2Spawn.getChunk().load();
		}

		this.team1.forEachAliveMember(player -> {
			Players.reset(player, GameMode.SURVIVAL, true);
			
			if (!this.team1.getPvpClassesMap().containsKey(player.getUniqueId())) {
				FlatFileKitManager.getKit("Diamond").applyTo(player, true, true);
			} else {
				String className = this.team1.getPvpClassesMap().get(player.getUniqueId());

				if(!className.equalsIgnoreCase("Diamond")){
					className = "Free" + className;
				}

				FlatFileKitManager.getKit(className).applyTo(player, true, true);
			}
			
			player.teleport(team1Spawn);
		});
		
		this.team2.forEachAliveMember(player -> {
			Players.reset(player, GameMode.SURVIVAL, true);
			
			if (!this.team2.getPvpClassesMap().containsKey(player.getUniqueId())) {
				FlatFileKitManager.getKit("Diamond").applyTo(player, true, true);
			} else {
				String className = this.team2.getPvpClassesMap().get(player.getUniqueId());

				if(!className.equalsIgnoreCase("Diamond")){
					className = "Free" + className;
				}

				FlatFileKitManager.getKit(className).applyTo(player, true, true);
			}
			
			player.teleport(team2Spawn);
		});
		
		Bukkit.getPluginManager().callEvent(new FactionDuelMatchStartCountdownEvent(this));
		
		new BukkitRunnable() {
			int i = 5;
			
			@Override
			public void run() {
				if (i == 0) {
					this.cancel();
					
					start();
					return;
				}
				
				forEachAll(player -> {
					player.sendMessage(CC.translate("&c» &eMatch &astarts &ein &6" + i + "&e..."));
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 6.0F, 1.0F);
				});
				
				i--;
			}
			
		}.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);
	}
	
	void start() {
		if (this.state == FactionDuelMatchState.PLAYING) {
			return;
		}
		
		this.state = FactionDuelMatchState.PLAYING;
		this.startedAt = System.currentTimeMillis();
		
		this.forEachAll(player -> {
			player.sendMessage(CC.translate("&a&lMatch started! Good luck."));
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 6.0F, 2.4F);
		});
		
		Bukkit.getPluginManager().callEvent(new FactionDuelMatchStartEvent(this));
	}
	
	public void tryFinish() {
		int alivePlayersTeam1 = this.team1.getAliveMembers().size();
		int alivePlayersTeam2 = this.team2.getAliveMembers().size();
		
		if (alivePlayersTeam1 == 0 && alivePlayersTeam2 > 0) {
			this.finish(this.team1);
			return;
		}
		
		if (alivePlayersTeam2 == 0 && alivePlayersTeam1 > 0) {
			this.finish(this.team2);
		}
	}
	
	public void cancel() {
		this.finish(null);
	}
	
	void finish(FactionDuelMatchTeam winnerTeam) {		
		if (this.state == FactionDuelMatchState.ENDING) {
			return;
		}
		
		// Match forced to end
		if (winnerTeam == null) {
			this.terminate(null);
			return;
		}
		
		this.state = FactionDuelMatchState.ENDING;
		this.endedAt = System.currentTimeMillis();

		Bukkit.getPluginManager().callEvent(new FactionDuelMatchEndEvent(this, winnerTeam));
		
		FactionDuelMatchTeam loserTeam = winnerTeam.equals(this.team1) ? this.team2 : this.team1;
		
		String winnerTeamMembers = winnerTeam.getMembersCache()
				.stream()
				.map(memberUUID -> {
					return Bukkit.getOfflinePlayer(memberUUID).getName();
				})
				.collect(Collectors.joining("&7, &a"));
		String loserTeamMembers = loserTeam.getMembersCache()
				.stream()
				.map(memberUUID -> {
					return Bukkit.getOfflinePlayer(memberUUID).getName();
				})
				.collect(Collectors.joining("&7, &c"));
		
		this.forEachAll(player -> {
			player.sendMessage("");
			player.sendMessage(CC.translate("&6&lMatch Ended &7(&e" + TimeUtil.formatTime(this.getGameTimeMillis(), TimeUtil.FormatType.MILLIS_TO_MINUTES) + "&7)"));
			player.sendMessage(CC.translate(" &7&l• &fWinners&7: " + winnerTeamMembers));
			player.sendMessage(CC.translate(" &7&l• &fLosers&7: " + loserTeamMembers));
			player.sendMessage("");
		});
		
		Task.runLater(() -> terminate(winnerTeam), 100L);
	}
	
	void terminate(FactionDuelMatchTeam winnerTeam) {
		if (this.state == FactionDuelMatchState.TERMINATING) {
			return;
		}
		
		this.state = FactionDuelMatchState.TERMINATING;
		
		Location spawn = Main.getInstance().getServerHandler().getSpawnLocation();
		
		this.forEachAll(player -> {
			Players.reset(player, player.getGameMode(), true);
			
			this.playersStuffCache.get(player.getUniqueId()).restore(player);
			
			if (spawn != null) {
				player.teleport(spawn);
			}
			
			FactionDuelManager.updateVisibility(player);
		});
		
		for (Item item : this.droppedItemsCache) {
			item.remove();
			
			this.droppedItemsCache.remove(item);
		}
		
		this.team1.getAliveMembers().clear();
		this.team2.getAliveMembers().clear();
		
		this.spectators.clear();
		this.participantsCache.clear();
		
		this.playersStuffCache.clear();
		droppedItemsCache.clear();
		
		this.arena.setInUse(false);
		
		// terminate() method can be called when the server is shutting down
		// to cancel all active matches, so this prevents an exception
		if (JavaPlugin.getPlugin(Main.class).isEnabled()) {
			Bukkit.getPluginManager().callEvent(new FactionDuelMatchTerminateEvent(this, winnerTeam));
		}
	}

	public void setDead(Player player) {
		if (!this.isAlive(player)) {
			return;
		}
		
		FactionDuelMatchTeam playerTeam = this.participantsCache.get(player.getUniqueId());
		
		playerTeam.getAliveMembers().remove(player);
		
		this.alivePlayers.remove(player);
		this.spectators.add(player);
		
		Players.reset(player, GameMode.CREATIVE, false);
		
		FactionDuelManager.updateVisibility(player);
	}
	
	public void forEachAll(Consumer<Player> action) {
		this.getAll().forEach(action);
	}
	
	public void forEachAlive(Consumer<Player> action) {
		this.alivePlayers.forEach(action::accept);
	}
	
	public Set<Player> getAll() {
		Set<Player> toReturn = Sets.newHashSet(this.alivePlayers);

		toReturn.addAll(this.spectators);
		
		return toReturn;
	}
	
	public long getGameTimeMillis() {
		return this.endedAt - this.startedAt;
	}
	
	public boolean isAlive(Player player) {
		return this.alivePlayers.contains(player);
	}
	
	public boolean isSpectating(Player player) {
		return this.spectators.contains(player);
	}
	
	public static enum FactionDuelMatchState {

		NONE,
		STARTING,
		PLAYING,
		ENDING,
		TERMINATING;
	}

}
