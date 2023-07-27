package cc.stormworth.hcf.misc.war;

import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.pvpclasses.PvPClass;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.util.com.google.common.base.Preconditions;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class FactionWarParticipant {

    private final ObjectId factionUUID;
    private final String factionName;

    private final Set<Player> members;
    private final Set<UUID> membersCache;

    private final Map<UUID, PvPClass> pvpClassesMap = Maps.newHashMap();

    private final Set<UUID> disconnectedMembersMap = Sets.newHashSet();

    // This is only used while the participant is in a match
    private final Set<Player> aliveInMatch = Sets.newHashSet();

    public FactionWarParticipant(Team faction, Set<Entry<UUID, PvPClass>> membersClasses) {
        Preconditions.checkNotNull(faction, "Faction can't be null.");

        this.factionUUID = faction.getUniqueId();
        this.factionName = faction.getName();

        this.members = ImmutableSet.copyOf(faction.getOnlineMembers());
        this.membersCache = ImmutableSet.copyOf(this.members.stream().map(Player::getUniqueId).collect(Collectors.toSet()));

        for (Entry<UUID, PvPClass> membersEntry : membersClasses) {
            this.pvpClassesMap.put(membersEntry.getKey(), membersEntry.getValue());
        }
    }

    public Team getFaction() {
        return Main.getInstance().getTeamHandler().getTeam(this.factionUUID);
    }

    public boolean isAliveInMatch(Player player) {
        return this.aliveInMatch.contains(player);
    }

    public boolean hasDisconnectedMembers() {
        return this.disconnectedMembersMap.size() > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FactionWarParticipant)) {
            return false;
        }

        return ((FactionWarParticipant) obj).getFactionUUID().equals(this.factionUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.factionUUID, this.factionName, this.members);
    }
}
