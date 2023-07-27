package cc.stormworth.hcf.misc.daily.data;

import lombok.Data;

import java.util.UUID;

@Data
public class DailyPlayer {

    private UUID owner;

    private int daysJoined, claimedRewards, streak, nextRewardLevel;

    private long joined, nextReward;
}
