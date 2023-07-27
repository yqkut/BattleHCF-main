package cc.stormworth.hcf.misc.map.stats.command;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.team.Team;
import java.util.UUID;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class StatsCommand {

  @Command(names = {"stats"}, permission = "", async = true)
  public static void stats(CommandSender sender,
      @Param(name = "player", defaultValue = "self") UUID uuid) {
    StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler().getStats(uuid);
    if (stats == null) {
      sender.sendMessage(ChatColor.RED + "Player not found.");
      return;
    }
    sender.sendMessage(
        ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(uuid));
    sender.sendMessage(
        ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
    sender.sendMessage(ChatColor.YELLOW + "Kills: " + ChatColor.RED + stats.getKills());
    sender.sendMessage(ChatColor.YELLOW + "Deaths: " + ChatColor.RED + stats.getDeaths());
    sender.sendMessage(
        ChatColor.YELLOW + "Killstreak: " + ChatColor.RED + stats.getKillstreak());
    sender.sendMessage(ChatColor.YELLOW + "Highest Killstreak: " + ChatColor.RED
        + stats.getHighestKillstreak());
    sender.sendMessage(
        ChatColor.YELLOW + "KD: " + ChatColor.RED + (stats.getDeaths() == 0 ? "0.00"
            : Team.DTR_FORMAT.format((double) stats.getKills() / (double) stats.getDeaths())));
    sender.sendMessage(
        ChatColor.RED.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 53));
  }

  @Command(names = {"resetstats"}, permission = "op", async = true)
  public static void resetstats(CommandSender sender,
      @Param(name = "player", defaultValue = "self") UUID uuid) {
    StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler().getStats(uuid);
    if (stats == null) {
      sender.sendMessage(ChatColor.RED + "Player not found.");
      return;
    }

    stats.setKills(0);
    stats.setDeaths(0);
    stats.setKillstreak(0);

    sender.sendMessage(ChatColor.RED + "Stats reset.");
  }

  @Command(names = {"clearallstats"}, permission = "op", async = true)
  public static void clearallstats(final Player sender) {
    final ConversationFactory factory = new ConversationFactory(
        Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix())
        .withFirstPrompt(new StringPrompt() {
          public String getPromptText(final ConversationContext context) {
            return "§aAre you sure you want to clear all stats? Type §byes§a to confirm or §cno§a to quit.";
          }

          public Prompt acceptInput(final ConversationContext cc, final String s) {
            if (s.equalsIgnoreCase("yes")) {
              Main.getInstance().getMapHandler().getStatsHandler().clearAll();
              cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
              return Prompt.END_OF_CONVERSATION;
            }
            if (s.equalsIgnoreCase("no")) {
              cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
              return Prompt.END_OF_CONVERSATION;
            }
            cc.getForWhom().sendRawMessage(ChatColor.GREEN
                + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
            return Prompt.END_OF_CONVERSATION;
          }
        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10)
        .thatExcludesNonPlayersWithMessage("Go away evil console!");
    final Conversation con = factory.buildConversation(sender);
    sender.beginConversation(con);
  }
}