package cc.stormworth.hcf.bounty;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.battle.crates.crate.Crate;

import java.util.List;

public class BountyRunnable implements Runnable{

    private final List<ItemStack> randomRewards = Lists.newArrayList();
    private long lastBountyTime;

    public BountyRunnable(){
        lastBountyTime = System.currentTimeMillis() + TimeUtil.parseTimeLong("5m");

        TaskUtil.runLater(Main.getInstance(), () -> {
            randomRewards.add(Crate.getByName("Giftbox").getKey());
            randomRewards.add(Crate.getByName("Ability").getKey());
        }, 20 * 60);

    }

    @Override
    public void run() {
        if(lastBountyTime > System.currentTimeMillis()){
            return;
        }

        Player target = getRandomPlayer();

        if (target == null) {
            return;
        }

        BountyPlayer bountyPlayer = new BountyPlayer(target.getUniqueId(), "Battle", null);

        bountyPlayer.setBalance(10000);

        int randomMax = Main.RANDOM.nextInt(2) + 1;

        for (int i = 0; i < randomMax; i++) {
            bountyPlayer.getRewards().add(randomRewards.get(Main.RANDOM.nextInt(randomRewards.size())));
        }

        BountyPlayer.getBounties().put(target.getUniqueId(), bountyPlayer);

        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&6[Bounty] &6&l" + target.getName() + "&e has been selected for the bounty."));
        Bukkit.broadcastMessage(CC.translate(""));

        lastBountyTime = System.currentTimeMillis() + TimeUtil.parseTimeLong("5m");

        CorePlugin.getInstance().getNametagEngine().reloadPlayer(bountyPlayer.getTarget());
        CorePlugin.getInstance().getNametagEngine().reloadOthersFor(bountyPlayer.getTarget());
    }

    private Player getRandomPlayer(){
        List<Player> players = Lists.newArrayList(Bukkit.getOnlinePlayers());

        players.removeIf(player -> player.getGameMode() != GameMode.SURVIVAL);
        players.removeIf(player -> player.hasMetadata("invisible"));
        players.removeIf(player -> CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player));
        players.removeIf(player -> DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()));

        if (players.isEmpty()) {
            return null;
        }

        return players.get(Main.RANDOM.nextInt(players.size()));
    }
}
