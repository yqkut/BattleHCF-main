package cc.stormworth.hcf.misc.map.stats.command;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class ClearleaderboardsCommand {

    @Command(names = {"clearleaderboards"}, permission = "op", hidden = true, async = true)
    public static void clearallstats(final Player sender) {
        final ConversationFactory factory = new ConversationFactory(Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(final ConversationContext context) {
                return "§aAre you sure you want to clear leaderboards? Type §byes§a to confirm or §cno§a to quit.";
            }

            public Prompt acceptInput(final ConversationContext cc, final String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Main.getInstance().getMapHandler().getStatsHandler().clearLeaderboards();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Leaderboards cleared");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        final Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }
}