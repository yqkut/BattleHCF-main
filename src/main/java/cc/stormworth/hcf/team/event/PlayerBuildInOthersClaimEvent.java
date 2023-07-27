package cc.stormworth.hcf.team.event;

import cc.stormworth.hcf.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBuildInOthersClaimEvent extends PlayerEvent {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final Block block;
    @Getter
    private final Team team;
    @Getter
    @Setter
    private boolean willIgnore;

    public PlayerBuildInOthersClaimEvent(Player who, Block block, Team team) {
        super(who);
        this.block = block;
        this.team = team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}