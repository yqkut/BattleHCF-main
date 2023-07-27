package cc.stormworth.hcf.giveaway.listeners;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.giveaway.GiveAway;
import cc.stormworth.hcf.giveaway.GiveAwayType;
import cc.stormworth.hcf.giveaway.commands.GiveAwayCommand;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class GiveAwayListener implements Listener {

  private final Main plugin;
  private static final String TWITTER_REGEX = "https://twitter\\.com/";

  @EventHandler(priority = EventPriority.LOW)
  public void onAsyncChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    if(GiveAwayCommand.RANDOM_PICK){


      if(GiveAwayCommand.getParticipants().containsKey(player.getUniqueId())){
        player.sendMessage(CC.translate("&cYou have already entered a giveaway!"));
        return;
      }

      String message = event.getMessage();

      String urlFormatted = message.replace("www.", "").replace("mobile.", "");

      if (Pattern.compile(TWITTER_REGEX).matcher(urlFormatted).find()) {
        event.setCancelled(true);
        GiveAwayCommand.getParticipants().put(player.getUniqueId(), urlFormatted);
        Bukkit.broadcastMessage(CC.translate("&6&l" + player.getName() + " &ehas joined the &6giveaway &esuccessfully by ussing &6&l" + message + "&e."));
        return;
      }

      return;
    }

    GiveAway giveAway = plugin.getGiveAwayHandler().getCurrentGiveAway();

    if (giveAway == null) {
      return;
    }

    String msg = event.getMessage();

    if (giveAway.getType() == GiveAwayType.RAFFLE) {

      if (giveAway.getParticipants().contains(player.getUniqueId())) {
        player.sendMessage(CC.translate("&cYou have already entered the giveaway!"));
        return;
      }

      if (msg.equalsIgnoreCase(giveAway.getWord())) {
        event.setCancelled(true);
        giveAway.addPlayer(player);
        Bukkit.broadcastMessage(CC.translate("&6&l" + player.getName() + " &ehas joined the &6giveaway &esuccessfully."));
      }
    } else if (giveAway.getType() == GiveAwayType.NUMBER) {

      int number;

      try {
        number = Integer.parseInt(msg);

        if (giveAway.getWinNumber() == number) {
          giveAway.winner(player);
        }

      } catch (NumberFormatException ignored) {
      }
    }else {
      if (msg.equalsIgnoreCase(giveAway.getWord())) {
        giveAway.getMostSpam().put(player.getUniqueId(), giveAway.getMostSpam().getOrDefault(player.getUniqueId(), 0) + 1);
      }
    }

  }

}