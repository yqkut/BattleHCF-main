package cc.stormworth.hcf.misc.kills.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.misc.kills.KillInfo;
import cc.stormworth.hcf.misc.kills.KillsManager;
import cc.stormworth.hcf.misc.kills.KillsMenu;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public class KillsCommand {

  @Command(names = {"kills"}, permission = "op")
  public static void kills(Player player, @Param(name = "target") UUID target) {

    player.sendMessage(CC.translate("&eLoading kills of &6" + UUIDUtils.name(target) + "&e..."));

    CompletableFuture<List<KillInfo>> future = CompletableFuture.supplyAsync(() ->
        KillsManager.getAllKills(target)
    );

    future.thenAccept(kills -> new KillsMenu(kills, UUIDUtils.name(target)).openMenu(player));
  }

}