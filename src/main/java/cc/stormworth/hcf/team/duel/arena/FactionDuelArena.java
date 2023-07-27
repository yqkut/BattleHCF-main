package cc.stormworth.hcf.team.duel.arena;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Objects;

@Getter
@Setter
public class FactionDuelArena {
	private final String name;
	
	private Location team1Spawn;
	private Location team2Spawn;
	
	private boolean enabled = true;
	private boolean inUse = false;
	
	public FactionDuelArena(String name) {
		this.name = Preconditions.checkNotNull(name, "Name can't be null.");
	}
	
	public boolean canEnable() {
		return this.team1Spawn != null && this.team2Spawn != null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FactionDuelArena)) {
			return false;
		}
		
		return ((FactionDuelArena) obj).getName().equals(this.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}
}
