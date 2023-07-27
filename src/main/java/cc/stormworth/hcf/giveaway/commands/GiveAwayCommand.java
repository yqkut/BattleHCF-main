package cc.stormworth.hcf.giveaway.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.giveaway.prompt.GiveAwayTypePrompt;
import cc.stormworth.hcf.util.chat.ChatUtils;
import cc.stormworth.hcf.util.countdown.Countdown;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GiveAwayCommand {

  @Command(names = {"giveaway start", "ga start"}, permission = "DEVELOPER")
  public static void start(Player player) {
    ChatUtils.beginPrompt(player, new GiveAwayTypePrompt());
  }

  @Command(names = {"giveaway stop", "ga stop"}, permission = "DEVELOPER")
  public static void stop(Player player) {
    Main.getInstance().getGiveAwayHandler().setCurrentGiveAway(null);
    Bukkit.broadcastMessage(CC.translate("&cThe Giveaway has been cancelled."));
  }

  @Getter private static final Map<UUID, String> participants = Maps.newHashMap();
  public static boolean RANDOM_PICK = false;
  @Command(names = {"randompick"}, permission = "DEVELOPER")
  public static void randompick(Player player, @Param(name = "amount") int amount, @Param(name = "time") String time) {

    if (amount < 1) {
      player.sendMessage(CC.translate("&cYou must specify a positive amount."));
      return;
    }

    int seconds = TimeUtils.parseTime(time);

    if(seconds == -1 || seconds == 0) {
      player.sendMessage(CC.translate("&cYou must specify a valid time."));
      return;
    }

    Bukkit.broadcastMessage(CC.translate("&6&lRandom Picker &ehas &6started&e. Ends in &6&n" + TimeUtils.formatIntoDetailedString(seconds) + "&e."));
    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(CC.translate("&eTo participate you must provide a &b&lTwitter Link &eof a Screenshare of you playing on Battle. &cTag us! &f@BattleRIPNet"));

    RANDOM_PICK = true;

    Countdown.of(seconds, TimeUnit.SECONDS)
            .withMessage(CC.translate("&eGiveaway ends in &6&l{time}&e!"))
            .onFinish(() -> {

              Bukkit.broadcastMessage(CC.translate("&6&lGiveaway &ehas &6&lended!"));

              if (participants.isEmpty()) {
                return;
              }

              Bukkit.broadcastMessage("");
              Bukkit.broadcastMessage(CC.translate("&eTotal entries&f: " + participants.size()));
              Bukkit.broadcastMessage("");

              List<UUID> keys = Lists.newArrayList(participants.keySet());

              for (int i = 0; i < amount; i++) {

                  if(keys.isEmpty()) {
                      break;
                  }

                  UUID uuid = keys.remove((int) (Math.random() * participants.size()));

                  Player other = Bukkit.getPlayer(uuid);

                  if (other != null) {
                      Bukkit.broadcastMessage(CC.translate("&6&l" + other.getName() + " &ehas won the giveaway!"));

                      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                          if(onlinePlayer.hasPermission("hcf.staff")) {
                              onlinePlayer.sendMessage(CC.translate("&6&l" + other.getName() + " &ehas won the giveaway with the link &6&l" + participants.get(uuid) + "&e!"));
                          }
                    }
                  }
              }
              RANDOM_PICK = false;
            }).start();
  }
}