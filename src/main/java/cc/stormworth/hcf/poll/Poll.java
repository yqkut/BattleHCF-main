package cc.stormworth.hcf.poll;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Poll {

  private int yes;
  private int no;
  private final String question;
  private long duration;
  private long startAt;
  public boolean finished;
  private final List<UUID> voters = Lists.newArrayList();
  private PollRunnable pollRunnable;
  private String createByName;

  public Poll(String question, String createByName) {
    this.question = question;
    this.createByName = createByName;
  }

  public void voteYes(Player player) {
    yes++;
    voters.add(player.getUniqueId());
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
  }

  public void voteNo(Player player) {
    no++;
    voters.add(player.getUniqueId());
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
  }

  public boolean hasVoted(Player p) {
    return voters.contains(p.getUniqueId());
  }

  public void sendResults(Player player) {
    player.sendMessage(CC.translate("&6&lPoll Results:"));
    player.sendMessage("");
    player.sendMessage(CC.translate("&6&lQuestion: &e" + question));
    player.sendMessage("");
    player.sendMessage(CC.translate("&eCreated by &4&l" + createByName));
    player.sendMessage("");
    player.sendMessage(CC.translate("&eVotes:"));
    player.sendMessage(CC.translate("&7- &aYes: &b" + yes));
    player.sendMessage(CC.translate("&7- &cNo: &b" + no));
    player.sendMessage("");
    player.sendMessage(CC.translate("&eTotal votes: &b" + (yes + no)));
    player.sendMessage("");
    player.sendMessage(CC.translate("&eResult: &b" + (yes > no ? "&aYes" : "&cNo")));
  }

  public boolean isFinished() {

    if (duration == -1) {
      return false;
    }

    return (startAt + duration) < System.currentTimeMillis();
  }

  public void start(Player player) {
    this.startAt = System.currentTimeMillis();

    Clickable clickable = new Clickable(
        "&7[&6&l✪&7] &6&lPoll &ehas been created by &4&l" + createByName
            + " &7&o(Click to vote)",
        "&bClick to vote!",
        "/poll yes");

    for (Player other : Bukkit.getOnlinePlayers()) {
      clickable.sendToPlayer(other);
      other.playSound(other.getLocation(), Sound.ORB_PICKUP, 1, 1);
    }

    new TitleBuilder(
            "&6&lPoll Created!",
            "&7&oUse /poll to choose &a&oYES&7 or &c&oNO",
            5,
            50,
            5
    ).send(player);

    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

    pollRunnable = new PollRunnable(this);
    pollRunnable.runTaskTimer(Main.getInstance(), 20, 20);
  }

  public void cancel() {
    this.finished = true;
    pollRunnable.cancel();
  }
}