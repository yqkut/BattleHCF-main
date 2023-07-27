package cc.stormworth.hcf.team.notes;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TeamNote {

	private final Date issuedOn;
	private final String staff;
	private final String reason;
}
