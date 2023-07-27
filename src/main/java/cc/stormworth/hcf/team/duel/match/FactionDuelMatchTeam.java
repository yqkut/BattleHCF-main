package cc.stormworth.hcf.team.duel.match;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public final class FactionDuelMatchTeam {

	private final ObjectId factionUUID;
	private final String factionName;
	
	private final Set<Player> aliveMembers = Sets.newHashSet();
	private final Set<UUID> membersCache = Sets.newHashSet();
	
	private final Map<UUID, String> pvpClassesMap;
	
	public FactionDuelMatchTeam(Team faction, Collection<Player> participants, Map<UUID, String> pvpClassesMap) {
		Preconditions.checkNotNull(faction, "Faction can't be null.");
		
		this.factionUUID = faction.getUniqueId();
		this.factionName = faction.getName();
		
		participants.forEach(participant -> {
			this.aliveMembers.add(participant);
			this.membersCache.add(participant.getUniqueId());
		});
		
		this.pvpClassesMap = ImmutableMap.copyOf(pvpClassesMap);
	}

	public FactionDuelMatchTeam(Team team){
		this(team, team.getOnlineMembers(), team.getPvpClassesMap());
	}

	
	public Team getFaction() {
		return Main.getInstance().getTeamHandler().getTeam(this.factionUUID);
	}
	
	public void forEachAliveMember(Consumer<Player> action) {
		this.aliveMembers.forEach(action);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FactionDuelMatchTeam)) {
			return false;
		}
		
		return ((FactionDuelMatchTeam) obj).getFactionUUID().equals(this.factionUUID);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.factionUUID, this.factionName);
	}
}
