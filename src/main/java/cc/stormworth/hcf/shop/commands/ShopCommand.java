package cc.stormworth.hcf.shop.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.shop.ShopKitmapMenu;
import cc.stormworth.hcf.shop.ShopMenu;
import org.bukkit.entity.Player;

public class ShopCommand {

  @Command(names = {"shop", "shops", "blockshop"}, permission = "")
  public static void shop(Player player) {

    if(SpawnTagHandler.isTagged(player)){
        player.sendMessage(CC.translate("&cYou can't use this command while tagged."));
        return;
    }

    if (Main.getInstance().getMapHandler().isKitMap()) {
      new ShopKitmapMenu().openMenu(player);
    } else {
      new ShopMenu().openMenu(player);
    }
  }

}