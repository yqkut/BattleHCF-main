package cc.stormworth.hcf.util.tasks;

import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.chat.CentredMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class OnlineDonorsTask {

  private final BukkitTask announcementTask;

  public OnlineDonorsTask() {
    announcementTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), this::sendRankAnnouncementMessage, 0L, 600 * 20L);
  }

  public void disable() {
    if (this.announcementTask != null) {
      this.announcementTask.cancel();
    }
  }

  private void handlePlayerAnnouncementMessage(Player player, List<String> playerNames) {
    Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());
    Rank rank = profile == null ? Rank.DEFAULT : profile.getRank();

    if (rank != Rank.BATTLE) {
      return;
    }
    playerNames.add(Rank.BATTLE.getColor() + player.getName());
  }

  private void sendRankAnnouncementMessage() {
    List<String> playerNames = new ArrayList<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      this.handlePlayerAnnouncementMessage(player, playerNames);
    }

    StringJoiner joiner = new StringJoiner(CC.translate("&7, "));
    playerNames.forEach(joiner::add);

    if (joiner.length() != 0) {
      Bukkit.getOnlinePlayers().forEach(player -> {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 3, 1);

        player.sendMessage(
            CC.translate("&7&m---------------------------------------------"));
        player.sendMessage(CentredMessage.generate("&8[&e&k:&6&lBattle&6&k:&8]"));
        player.sendMessage(
            CentredMessage.generate("&6&l» &ePurchase this rank at &fstore.battle.rip &6&l«"));
        player.sendMessage("");
        player.sendMessage(CentredMessage.generate(joiner.toString()));
        player.sendMessage(
            CC.translate("&7&m---------------------------------------------"));
      });
    }
  }
}