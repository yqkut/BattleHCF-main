package cc.stormworth.hcf.commands.playtime;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.providers.scoreboard.ScoreFunction;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand {

  @Command(names = {"playtime", "pt"}, permission = "")
  public static void playTime(CommandSender sender, @Param(name = "player", defaultValue = "self") UUID target) {

    HCFProfile profile = HCFProfile.getByUUID(target);

    if (profile == null) {

      CompletableFuture<HCFProfile> future = HCFProfile.load(target);

      future.thenAccept(p -> {

        if (p == null) {
            sender.sendMessage(CC.RED + "Profile not found.");
            return;
        }

        sender.sendMessage(CC.translate(p.getName() + " &ehas play for&6&l "+  ScoreFunction.TIME_FANCY.apply(p.getPlayTime() / 1000F) + "&e."));
      });

      return;
    }

    long playtimeTime = profile.getTotalPlayTime();

    sender.sendMessage(CC.translate(UUIDUtils.name(target) + " &ehas play for&6&l " + ScoreFunction.TIME_FANCY.apply(playtimeTime / 1000F) + "&e."));
  }

}