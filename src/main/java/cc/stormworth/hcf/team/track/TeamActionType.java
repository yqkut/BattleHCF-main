package cc.stormworth.hcf.team.track;

import com.google.common.base.CaseFormat;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum TeamActionType {

    // Chat Messages
    ALLY_CHAT_MESSAGE,
    TEAM_CHAT_MESSAGE,
    OFFICER_CHAT_MESSAGE,

    // Financial + Land
    PLAYER_WITHDRAW_MONEY,
    PLAYER_DEPOSIT_MONEY,
    PLAYER_CLAIM_LAND,
    PLAYER_UNCLAIM_LAND,
    PLAYER_RESIZE_LAND,

    // Create + Delete
    PLAYER_CREATE_TEAM,
    PLAYER_DISBAND_TEAM,

    // Mutes
    TEAM_MUTE_CREATED,
    TEAM_UMUTED,

    // Connections
    MEMBER_CONNECTED,
    MEMBER_DISCONNECTED,

    // Basic
    ANNOUNCEMENT_CHANGED,
    HEADQUARTERS_CHANGED,

    // Invites
    PLAYER_INVITE_SENT,
    PLAYER_INVITE_REVOKED,

    // Player Ranks
    PLAYER_JOINED,
    MEMBER_KICKED,
    MEMBER_REMOVED,
    LEADER_CHANGED,
    PROMOTED_TO_CAPTAIN,
    PROMOTED_TO_CO_LEADER,
    DEMOTED_FROM_CAPTAIN,
    DEMOTED_FROM_CO_LEADER,

    // PvP Deaths
    MEMBER_KILLED_ENEMY_IN_PVP,
    MEMBER_KILLED_BY_ENEMY_IN_PVP,

    // DTR
    MEMBER_DEATH,
    TEAM_NOW_RAIDABLE,
    TEAM_NO_LONGER_RAIDABLE,

    //POINTS
    TEAM_POINTS_ADDED,
    TEAM_POINTS_REMOVED;

    public String getInternalName() {
        // thanks guava!
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

    public String getName(){
        return StringUtils.capitalize(name().toLowerCase().replace("_", " "));
    }

    public static TeamActionType fromInternalName(String internalName) {
        return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, internalName));
    }

}