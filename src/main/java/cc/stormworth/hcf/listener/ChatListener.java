package cc.stormworth.hcf.listener;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.cmds.staff.MutePartnersCommand;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Constants;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.track.TeamActionType;
import cc.stormworth.hcf.team.track.TeamTrackerManager;
import cc.stormworth.hcf.util.chat.ChatMode;
import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());
    Rank rank = profile == null ? Rank.DEFAULT : profile.getRank();
    Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player);
    HCFProfile hcfProfile = HCFProfile.get(player);

    if (profile == null) return;

    String rankPrefix = (profile.getActiveTag() != null ? profile.getColoredActiveTag() + " " : "") + profile.getRank().getPrefix();

    ChatMode finalChatMode = hcfProfile.getChatMode();

    if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
      player.sendMessage(
          ChatColor.RED + "You cannot speak in non-public chat if you're not in a team!");

      hcfProfile.setChatMode(ChatMode.PUBLIC);
      return;
    }

    if (finalChatMode != ChatMode.PUBLIC) {

      if (finalChatMode == ChatMode.OFFICER &&
              !playerTeam.isCaptain(player.getUniqueId()) &&
              !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
        hcfProfile.setChatMode(ChatMode.TEAM);
        player.sendMessage(ChatColor.RED + "You cannot speak in officer chat if you're not an officer!");
        return;
      }
    }

    switch (finalChatMode) {
      case PUBLIC: {

        if(event.isCancelled()){
          return;
        }

        String publicChatFormat = Constants.publicChatFormat(player, playerTeam, rankPrefix);
        String message = event.getMessage();

        if (CorePlugin.getInstance().getRedisManager().isChatSilenced()
                && (!player.isOp() && (rank == Rank.PARTNER && MutePartnersCommand.partnersMuted))) {
          return;
        }

        String finalMessage = String.format(publicChatFormat, message);

        if(ChatColor.stripColor(finalMessage).toLowerCase().contains("soy nuevo")){
          player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
          player.sendMessage(CC.translate("&eSi eres nuevo recuerda utilizar &6/gkit sotw &epara tener un inicio más agradable &c&l❤"));
        }

        for (final Player recipient : event.getRecipients()) {

          HCFProfile recipientProfile = HCFProfile.getByUUIDIfAvailable(recipient.getUniqueId());

          if (recipientProfile == null) {
            continue;
          }

          if (playerTeam != null) {
            publicChatFormat = Constants.publicChatFormat(player, recipient, playerTeam, rankPrefix);
            finalMessage = String.format(publicChatFormat, message);
          }

          if (!player.isOp() && !recipientProfile.isGlobalChat()) {
            continue;
          }

          if(player.isOp() && ChatColor.stripColor(finalMessage).toLowerCase().contains("@everyone")){
            recipient.playSound(recipient.getLocation(), Sound.NOTE_PLING, 5, 5);
          }

          recipient.sendMessage(finalMessage);
        }
        break;
      }
      case ALLIANCE: {
        String allyChatFormat = Constants.allyChatFormat(player, event.getMessage());
        String allyChatSpyFormat = Constants.allyChatSpyFormat(playerTeam, player, event.getMessage());

        for (final Player player2 : Main.getInstance().getServer().getOnlinePlayers()) {

          if (playerTeam.isMember(player2.getUniqueId()) || playerTeam.isAlly(player2.getUniqueId())) {
            player2.sendMessage(allyChatFormat);
          } else {
            HCFProfile hcfProfileOther = HCFProfile.getByUUIDIfAvailable(player2.getUniqueId());

            if (!hcfProfileOther.getSpyTeam().contains(playerTeam.getUniqueId())) {
              continue;
            }

            player2.sendMessage(allyChatSpyFormat);
          }
        }

        // Log to ally's allychat log.
        for (ObjectId allyId : playerTeam.getAllies()) {
          Team ally = Main.getInstance().getTeamHandler().getTeam(allyId);

          if (ally != null) {
            TeamTrackerManager.logAsync(ally, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.<String, Object>builder()
                    .put("allyTeamId", playerTeam.getUniqueId().toString())
                    .put("allyTeamName", playerTeam.getName())
                    .put("playerId", event.getPlayer().getUniqueId().toString())
                    .put("message", event.getMessage())
                    .put("date", System.currentTimeMillis())
                    .build()
            );
          }
        }

        TeamTrackerManager.logAsync(playerTeam, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.of(
                "playerId", event.getPlayer().getUniqueId().toString(),
                "message", event.getMessage(),
                "date", System.currentTimeMillis()
        ));

        break;
      }
      case TEAM: {
        String teamChatFormat = Constants.teamChatFormat(player, event.getMessage());
        String teamChatSpyFormat = Constants.teamChatSpyFormat(playerTeam, player, event.getMessage());

        for (Player player3 : Main.getInstance().getServer().getOnlinePlayers()) {
          if (playerTeam.isMember(player3.getUniqueId())) {
            player3.sendMessage(teamChatFormat);
          } else {
            HCFProfile hcfProfileOther = HCFProfile.getByUUIDIfAvailable(player3.getUniqueId());

            if (!hcfProfileOther.getSpyTeam().contains(playerTeam.getUniqueId())) {
              continue;
            }
            player3.sendMessage(teamChatSpyFormat);
          }
        }

        // Log to our teamchat log.
        TeamTrackerManager.logAsync(playerTeam, TeamActionType.TEAM_CHAT_MESSAGE, ImmutableMap.of(
                "playerId", event.getPlayer().getUniqueId().toString(),
                "message", event.getMessage(),
                "date", System.currentTimeMillis()
        ));

        break;
      }
      case OFFICER: {
        String officerChatFormat = Constants.officerChatFormat(player, event.getMessage());

        for (final Player player4 : Main.getInstance().getServer().getOnlinePlayers()) {
          if (playerTeam.isCaptain(player4.getUniqueId()) || playerTeam.isCoLeader(
              player4.getUniqueId()) || playerTeam.isOwner(player4.getUniqueId())) {
            player4.sendMessage(officerChatFormat);
          }
        }

        // Log to our teamchat log.
        TeamTrackerManager.logAsync(playerTeam, TeamActionType.OFFICER_CHAT_MESSAGE, ImmutableMap.of(
                "playerId", event.getPlayer().getUniqueId().toString(),
                "message", event.getMessage(),
                "date", System.currentTimeMillis()
        ));

        break;
      }
    }

    event.setCancelled(true);
  }
}