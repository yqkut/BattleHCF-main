package cc.stormworth.hcf.listener;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.onedoteight.ActionBarUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.deathmessage.event.PlayerKilledEvent;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StrafeAbilityListener implements Listener {

	private final int KILLS_TO_ENABLE = 30;
	private final int MINUTES_TO_MAKE_KILLS = 5;
	private final int REQUIRED_KILLS_TO_UNLOCK = 3;
	
	private final Map<UUID, BukkitTask> playersAwaitingUse = Maps.newHashMap();
	private final Map<UUID, Integer> playerKillsTrack = Maps.newHashMap();
	private final Set<UUID> playersWhoCanUse = Sets.newHashSet();
	
	public StrafeAbilityListener() {
		TaskUtil.runAsyncTimer(Main.getInstance(), () -> {
			for (UUID uuid : this.playersWhoCanUse) {
				Player player = Bukkit.getPlayer(uuid);
				
				if (player != null) {
					ActionBarUtils.sendActionBarMessage(player, "&a✦ &ePress &6&lSHIFT &eto use &6Strafe&e.");
				}
			}
		}, 0L, 30L * 20L);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKilled(PlayerKilledEvent event) {
		Player player = event.getKiller();
		
		if (!Main.getInstance().getMapHandler().isKitMap()) return;
		
		if (this.playersAwaitingUse.containsKey(player.getUniqueId())) {
			int newKills = this.playerKillsTrack.getOrDefault(player.getUniqueId(), 0) + 1;
			
			if (newKills == REQUIRED_KILLS_TO_UNLOCK) {
				this.playersAwaitingUse.remove(player.getUniqueId()).cancel();
				this.playerKillsTrack.remove(player.getUniqueId());
				this.playersWhoCanUse.add(player.getUniqueId());
				
				player.sendMessage("");
				player.sendMessage(CC.translate("&eYour &6&lStrafe &eability is ready!"));
				player.sendMessage(CC.translate(" &c&l► &7Press &fSHIFT &7when you are fighting a player to activate it."));
				player.sendMessage("");
			} else {
				this.playerKillsTrack.put(player.getUniqueId(), newKills);
			}
		} else {
			if (Main.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak() != KILLS_TO_ENABLE) return;
			
			this.playersAwaitingUse.put(player.getUniqueId(), new BukkitRunnable() {
				
				@Override
				public void run() {
					playersAwaitingUse.remove(player.getUniqueId());
					playerKillsTrack.remove(player.getUniqueId());
					
					player.sendMessage(CC.translate("&cYou lost the opportunity to activate the &lStrafe &cability! Good luck for the next time."));
				}
				
			}.runTaskLaterAsynchronously(Main.getInstance(), MINUTES_TO_MAKE_KILLS * 60L * 20L));
			
			player.playSound(player.getLocation(), Sound.NOTE_PLING, 6.0F, 1.5F);
			player.sendMessage("");
			player.sendMessage(CC.translate("&e&lYou have reached your &6&l" + KILLS_TO_ENABLE + "th &e&lconsecutive kill!"));
			player.sendMessage(CC.translate("&7&oYou have &f&o" + MINUTES_TO_MAKE_KILLS + " &7&ominutes to kill &f&o" + REQUIRED_KILLS_TO_UNLOCK + " &7&oplayers in order to activate the &6&l&oStrafe &7&oability."));
			player.sendMessage("");
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!Main.getInstance().getMapHandler().isKitMap()) return;
		
		Player player = event.getEntity();
		
		if (this.playersAwaitingUse.containsKey(player.getUniqueId())) {
			this.playersAwaitingUse.remove(player.getUniqueId()).cancel();
		}
		
		this.playerKillsTrack.remove(player.getUniqueId());
		this.playersWhoCanUse.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		if (!Main.getInstance().getMapHandler().isKitMap()) return;
		
		Player player = event.getPlayer();
		
		if (!this.playersWhoCanUse.contains(player.getUniqueId())) return;
		if (!event.isSneaking()) return;
		
		if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
			player.sendMessage(CC.translate("&cYou can't use the Strafe ability in safe zones!"));
			return;
		}
		
		if (HCFProfile.get(player).hasPvPTimer()) {
			player.sendMessage(CC.translate("&cYou can't use the Strafe ability whilst your pvp protection is active!"));
			return;
		}
		
		Player target = player.getNearbyEntities(2.5D, 2.5D, 2.5D)
				.stream()
				.filter(entity -> entity instanceof Player)
				.map(entity -> (Player) entity)
				.filter(entity -> DTRBitmask.SAFE_ZONE.appliesAt(entity.getLocation()))
				.filter(entity -> HCFProfile.get(entity).hasPvPTimer())
				.findFirst()
				.orElse(null);
		
		if (target == null) {
			player.sendMessage(CC.translate("&cYou must be fighting a player in order to activate the Strafe ability!"));
			return;
		}
		
		Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);
		
		if (playerTeam != null && playerTeam.contains(target)) {
			player.sendMessage(CC.translate("&cYou can't use the Strafe ability against your teammates!"));
			return;
		}
		
		Location targetLocation = target.getLocation();

		Vector inverseDirectionVec = targetLocation.getDirection().normalize().multiply(-3);
		Location locationBehind =  player.getLocation().add(inverseDirectionVec);
		locationBehind.setY(player.getLocation().getY());

		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 1));
		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5 * 20, 3));
		player.sendMessage(CC.translate("&aYou have used the &6&lStrafe &aability!"));
		player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 6.0F, 1.0F);
		player.teleport(locationBehind);

		this.playersWhoCanUse.remove(player.getUniqueId());
	}
}
