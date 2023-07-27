package cc.stormworth.hcf.supplydrop;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.holograms.Hologram;
import cc.stormworth.core.util.holograms.Holograms;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.lunarclient.waypoint.PlayerWaypointType;
import cc.stormworth.hcf.misc.lunarclient.waypoint.WaypointManager;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import cc.stormworth.hcf.team.claims.LandBoard;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import rip.battle.crates.crate.Crate;
import rip.battle.crates.reward.Reward;
import rip.battle.crates.utils.RandomUtils;

import java.util.*;

@Getter
public class SupplyDropManager implements Listener {

    private Location lastLocation;
    private Hologram lastHologram;

    private long lastDrop;
    private long openIn = 0;
    private boolean open = false;

    public SupplyDropManager(){
        lastDrop = TimeUtil.parseTimeLong("15m") + System.currentTimeMillis();

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {

            if(lastDrop > System.currentTimeMillis()){
                return;
            }

            if (!spawnSupplyDrop()){
                return;
            }

            lastDrop = TimeUtil.parseTimeLong("30m") + System.currentTimeMillis();
        }, 20, 20);
    }

    public boolean spawnSupplyDrop(){
        Location randomLocation = getRandomLocation();

        if(randomLocation == null){
            return false;
        }

        Block block = randomLocation.getBlock();

        if(block.getType() != Material.AIR){
            return false;
        }

        block.setType(Material.CHEST);

        Bukkit.broadcastMessage(CC.translate("&6&lSupply Drop &espawned at: &f" +
                randomLocation.getBlockX() + "&7, &f" + randomLocation.getBlockY() + "&7,&f " + randomLocation.getBlockZ()));

        if (lastLocation != null){
            lastLocation.getBlock().setType(Material.AIR);
        }

        lastLocation = randomLocation;

       if(lastHologram != null){
           lastHologram.destroy();
        }

        Hologram hologram = Holograms.newHologram()
                .at(randomLocation.clone().add(0.5, 0.8, 0.5))
                .addLines("&e&ki&r &6&lSupply Drop &e&ki")
                .updates()
                .onUpdate(hologram1 -> {
                    if(open && (openIn - System.currentTimeMillis()) < 0){
                        hologram1.destroy();
                    }else if(openIn > 0){
                        hologram1.setLine(1, "&eOpen in:&6 " + ScoreFunction.TIME_FANCY.apply((float) (openIn - System.currentTimeMillis()) / 1000));
                    }else{
                        hologram1.setLine(1, "&eRight click to open.");
                    }
                })
                .build();

        hologram.send();

        lastHologram = hologram;

        WaypointManager.updateGlobalWaypoints(PlayerWaypointType.SUPPLY_DROP, true);
        return true;
    }


    public Location getRandomLocation(){
        int borderSize = 1500;

        World world = Bukkit.getWorlds().get(0);
        Random random = Main.RANDOM;

        int x = random.nextInt(borderSize * 2) - borderSize;
        int z = random.nextInt(borderSize * 2) - borderSize;

        Location location = new Location(world, x, world.getHighestBlockYAt(x, z) + 0.5, z);

        if (LandBoard.getInstance().getTeam(location) != null) {
            return null;
        }

        return location;
    }

    public List<ItemStack> getRandomContents(Player player){
        int max = Main.RANDOM.nextInt(10) + 1;

        List<ItemStack> contents = Lists.newArrayList();

        List<Reward> rewardList = new ArrayList<>(Crate.getByName("SupplyDrop").getRewards());

        for (int i = 0; i < max; i++) {
            Collections.shuffle(rewardList);

            Reward randomReward = RandomUtils.getRandomReward(rewardList);

            contents.add(randomReward.getItem());

            if (!randomReward.getBroadcast().isEmpty()) {
                randomReward.getBroadcast().forEach(line -> Bukkit.broadcastMessage(line.replace("{player}", player.getName())));
            }
        }

        return contents;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getClickedBlock() == null || lastLocation == null){
            return;
        }

        if(event.getClickedBlock().getType() != Material.CHEST){
            return;
        }

        if(event.getClickedBlock().getX() == lastLocation.getBlockX() &&
                event.getClickedBlock().getZ() == lastLocation.getBlockZ() &&
                event.getClickedBlock().getY() == lastLocation.getBlockY()){

            Player player = event.getPlayer();

            if(openIn == 0){
                openIn = TimeUtil.parseTimeLong("1m") + System.currentTimeMillis();
                event.setCancelled(true);
                open = true;
            }else {
                if(openIn > System.currentTimeMillis()){
                    player.sendMessage(CC.translate("&cYou must wait " + ScoreFunction.TIME_FANCY.apply((float) (openIn - System.currentTimeMillis()) / 1000) + " before opening this chest."));
                    event.setCancelled(true);
                    return;
                }

                lastLocation = null;
                open = false;
                openIn = 0;

                Chest chest = (Chest) event.getClickedBlock().getState();

                getRandomContents(player).forEach(chest.getInventory()::addItem);

                LCWaypoint waypoint = WaypointManager.getGlobalWaypoints().remove(PlayerWaypointType.SUPPLY_DROP);

                for (UUID uuid : Main.getInstance().getLunarClientManager().getPlayers()) {
                    Player lunarPlayer = Bukkit.getPlayer(uuid);

                    if (lunarPlayer != null) {
                        WaypointManager.removeWaypoint(lunarPlayer, waypoint);
                    }
                }
            }
        }
    }

    @Command(names = { "nextsupplydrop", "nextsupply" }, permission = "")
    public static void nextSupplyDrop(Player player) {
    	player.sendMessage(CC.translate("&eNext supply drop: &6" + ScoreFunction.TIME_FANCY.apply((float) (Main.getInstance().getSupplyDropManager().getLastDrop() - System.currentTimeMillis()) / 1000)));
    }
}
