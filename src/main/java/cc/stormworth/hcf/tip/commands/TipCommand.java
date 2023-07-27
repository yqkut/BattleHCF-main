package cc.stormworth.hcf.tip.commands;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Lang;
import cc.stormworth.hcf.tip.TipsEnglishMenu;
import cc.stormworth.hcf.tip.TipsMenu;
import cc.stormworth.hcf.tip.TipsSpanishMenu;
import org.bukkit.entity.Player;

public class TipCommand {

  @Command(names = {"tips", "tip"}, permission = "")
  public static void tip(Player player) {
    HCFProfile profile = HCFProfile.get(player);

    if (profile.getLang() == Lang.UNDEFINED) {
      new TipsMenu().openMenu(player);
    } else if (profile.getLang() == Lang.ENGLISH) {
      new TipsEnglishMenu().openMenu(player);
    } else {
      new TipsSpanishMenu().openMenu(player);
    }
  }

}