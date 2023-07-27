package cc.stormworth.hcf.misc.lunarclient.waypoint;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum PlayerWaypointType {

    SPAWN,
    NETHER_SPAWN,
    END_SPAWN,
    END_RETURN,

    GLOWSTONE,
    KOTH,
    DTC,
    CONQUEST,
    CONQUEST_RED,
    CONQUEST_BLUE,
    CONQUEST_GREEN,
    CONQUEST_YELLOW,
    SUPPLY_DROP,

    FACTION_RALLY,
    FACTION_HQ,
    FOCUSED_FACTION_HOME;


    public static PlayerWaypointType getByName(String name) {
        for (PlayerWaypointType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}