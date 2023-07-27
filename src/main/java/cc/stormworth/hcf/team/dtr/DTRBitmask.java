package cc.stormworth.hcf.team.dtr;

import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import org.bukkit.Location;

public enum DTRBitmask {
    SAFE_ZONE(1, "Safe-Zone", "Determines if a region is considered completely safe"),
    GLOWSTONE(2, "Glowstone", "Determines if a region is considered a glowstone mountain"),
    FIFTEEN_MINUTE_DEATHBAN(4, "15m-Deathban", "Determines if a region has a 15m deathban"),
    FIVE_MINUTE_DEATHBAN(8, "5m-Deathban", "Determines if a region has a 5m deathban"),
    THIRTY_SECOND_ENDERPEARL_COOLDOWN(16, "30s-Enderpearl-Cooldown", "Determines if a region has a 30s enderpearl cooldown"),
    CITADEL(32, "Citadel", "Determines if a region is part of Citadel"),
    KOTH(64, "KOTH", "Determines if a region is a KOTH"),
    REDUCED_DTR_LOSS(128, "Reduced-DTR-Loss", "Determines if a region takes away reduced DTR upon death"),
    NO_ENDERPEARL(256, "No-Enderpearl", "Determines if a region cannot be pearled into"),
    QUARTER_DTR_LOSS(512, "1/4-DTR-Loss", "Determines if a region takes away 1/4th DTR loss."),
    ROAD(1024, "Road", "Determines if a region is a road."),
    CONQUEST(2048, "Conquest", "Determines if a region is part of Conquest."),
    RESTRICTED_ZONE(4096, "RestrictedZone", "Determines if a region is part of a RestrictedZone"),
    DTC(8192, "DTC", "Determines if a region is part of DTC"),
    END_PORTAL(32768, "End-Portal", "Determines if a region is a end portal."),
    ORE_MOUNTAIN(524288, "OreMountain", "Determines if a region is considered a ore mountain"),
    RESTRICTED_EVENT(1048576, "RestrictedEvent", "Determines if a region is considered a restricted event"),
    NETHER(16777216, "Nether", "Determines if a region is considered a nether"),
    NETHER_ZONE(33554432, "NetherZone", "Determines if a region is considered a netherzone");

    private final int bitmask;
    private final String name;
    private final String description;

    DTRBitmask(final int bitmask, final String name, final String description) {
        this.bitmask = bitmask;
        this.name = name;
        this.description = description;
    }

    public boolean appliesAt(final Location location) {
        final Team ownerTo = LandBoard.getInstance().getTeam(location);
        return ownerTo != null && ownerTo.getOwner() == null && ownerTo.hasDTRBitmask(this);
    }

    public int getBitmask() {
        return this.bitmask;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}