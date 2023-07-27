package cc.stormworth.hcf.misc.payout;

import java.util.Date;
import java.util.UUID;

import cc.stormworth.core.util.time.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public final class Payout {

	private final String id;
	
	private final String type, faction, factionLeader, createdOn;
	@Setter
	private String payedOn = null;
	
	public Payout(String type, String faction, String factionLeader) {
		this.id = UUID.randomUUID().toString();
		
		this.type = type;
		this.faction = faction;
		this.factionLeader = factionLeader;
		this.createdOn = TimeUtil.dateToString(new Date());
	}
	
	public boolean isPayed() {
		return this.payedOn != null;
	}
}
