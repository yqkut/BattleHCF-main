package cc.stormworth.hcf.giveaway;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.giveaway.runnable.GiveAwayRunnable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class GiveAway {

  private int maxNumber;
  private final int minNumber = 1;
  private long time;

  private int winNumber;
  private String word;
  private GiveAwayType type;
  private boolean muteChatOnFinish;

  private final List<UUID> participants = Lists.newArrayList();

  private final Map<UUID, Integer> mostSpam = Maps.newHashMap();

  public void start() {
    if (type == GiveAwayType.RAFFLE || type == GiveAwayType.SPAM_WORD) {
      new GiveAwayRunnable(this).runTaskTimer(Main.getInstance(), 0, 20L);
    }
    Main.getInstance().getGiveAwayHandler().setCurrentGiveAway(this);

    if(CorePlugin.getInstance().getRedisManager().isChatSilenced()){
      this.muteChatOnFinish = true;
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mutechat");
    }
  }

  public void generateRandomNumber() {
    this.winNumber = (int) (Math.random() * (this.maxNumber - this.minNumber + 1)) + this.minNumber;

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("giveaway.see")) {
        player.sendMessage("");
        player.sendMessage(
            CC.translate("&7[&a&l✓&7] &eThe winner number is &6&l" + winNumber + "&e."));
        player.sendMessage("");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
      }
    }
  }

  public void winner(Player player) {
    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(
        CC.translate("&7[&a&l✦&7] &4&l" + player.getName() + " &ehas &6won &ethe giveaway."));
    Bukkit.broadcastMessage("");

    for (Player other : Bukkit.getOnlinePlayers()) {
      other.playSound(other.getLocation(), Sound.LEVEL_UP, 1, 1);
    }

    end();
  }

  public void addPlayer(Player player) {
    this.participants.add(player.getUniqueId());
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
  }

  public void end() {
    Main.getInstance().getGiveAwayHandler().setCurrentGiveAway(null);

    if(this.muteChatOnFinish) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mutechat");
    }

    if (this.type == GiveAwayType.SPAM_WORD){
        UUID winner = mostSpam.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        Player player = Bukkit.getPlayer(winner);

        if(player != null) {
            winner(player);
        }
    }
  }

  public void selectRandomWinner() {
    if (this.participants.isEmpty()) {
      end();
      return;
    }

    int random = (int) (Math.random() * this.participants.size());
    UUID uuid = this.participants.get(random);
    Player player = Bukkit.getPlayer(uuid);

    if (player != null) {
      winner(player);
    }
  }
}