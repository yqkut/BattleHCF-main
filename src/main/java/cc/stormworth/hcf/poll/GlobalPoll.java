package cc.stormworth.hcf.poll;

import cc.stormworth.core.util.chat.CC;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
public class GlobalPoll {

  private int ok;
  private int bad;
  private int great;
  private final List<String> question;
  private final List<UUID> voters = Lists.newArrayList();

  public GlobalPoll(String... question) {
    this.question = Lists.newArrayList(question);
  }

  public GlobalPoll(Document document) {
    ok = document.getInteger("ok");
    bad = document.getInteger("bad");
    great = document.getInteger("great");
    question = document.getList("question", String.class);
    voters.addAll(document.getList("voters", String.class).stream().map(UUID::fromString)
        .collect(Collectors.toList()));
  }

  public Document serialize() {
    Document document = new Document();
    document.put("ok", ok);
    document.put("bad", bad);
    document.put("great", great);
    document.put("question", question);
    document.put("voters", voters.stream().map(UUID::toString).collect(Collectors.toList()));
    return document;
  }

  public void vote(Player player, int vote) {
    if (vote == 1) {
      bad++;
    } else if (vote == 2) {
      ok++;
    } else if (vote == 3) {
      great++;
    }

    voters.add(player.getUniqueId());
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
  }

  public boolean hasVoted(Player player) {
    return voters.contains(player.getUniqueId());
  }

  public void sendResults(Player player) {
    player.sendMessage(CC.translate("&6&lPoll Results:"));
    player.sendMessage("");
    player.sendMessage(CC.translate("&6&lQuestion: &e" + question));
    player.sendMessage("");
    player.sendMessage("");
    player.sendMessage(CC.translate("&eVotes:"));
    player.sendMessage(CC.translate("&7- &2Great: &b" + great));
    player.sendMessage(CC.translate("&7- &aGood: &b" + ok));
    player.sendMessage(CC.translate("&7- &cBad: &b" + bad));
    player.sendMessage("");
    player.sendMessage(CC.translate("&eTotal votes: &b" + (great + ok + bad)));
    player.sendMessage("");

    String results;

    if (great > bad && great > ok) {
      results = "&2&lGreat";
    } else if (ok > great && ok > bad) {
      results = "&a&lGood";
    } else if (bad > great && bad > ok) {
      results = "&c&lBad";
    } else {
      results = "&e&lTie";
    }

    player.sendMessage(CC.translate("&eResult: &b" + results));
  }

  public String getResult() {
    String results;

    if (great > bad && great > ok) {
      results = "&2&lGreat";
    } else if (ok > great && ok > bad) {
      results = "&a&lGood";
    } else if (bad > great && bad > ok) {
      results = "&c&lBad";
    } else {
      results = "&e&lTie";
    }

    return results;
  }

  public int getTotalVotes() {
    return great + ok + bad;
  }
}