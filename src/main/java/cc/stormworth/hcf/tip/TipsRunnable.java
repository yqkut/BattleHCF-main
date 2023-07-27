package cc.stormworth.hcf.tip;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Lang;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class TipsRunnable implements Runnable {

  private final List<Tip> tips;
  private int count = 0;

  @Override
  public void run() {
    if (this.count == this.tips.size()) {
      this.count = 0;
    }

    Tip tip = this.tips.get(count);

    Bukkit.broadcastMessage("");
    for (Player player : Bukkit.getOnlinePlayers()) {
      HCFProfile profile = HCFProfile.get(player);
      if (profile.getLang() == Lang.ENGLISH || profile.getLang() == Lang.UNDEFINED) {
        tip.getMessages_english().forEach(message -> player.sendMessage(CC.translate(message)));
      } else {
        tip.getMessages_spanish().forEach(message -> player.sendMessage(CC.translate(message)));
      }
    }
    Bukkit.broadcastMessage("");
    this.count++;

  }
}