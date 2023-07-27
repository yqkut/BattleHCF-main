package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class TeamTeleportAllCommand {
    @Command(names = {"team tpall", "t tpall", "f tpall", "faction tpall", "fac tpall"}, permission = "op")
    public static void teamTP(final Player sender, @Param(name = "team") final Team team) {
        final ConversationFactory factory = new ConversationFactory(Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(final ConversationContext context) {
                return "§aAre you sure you want to teleport all players in " + team.getName() + " (" + team.getOnlineMembers().size() + ") to your location? Type §byes§a to confirm or §cno§a to quit.";
            }

            public Prompt acceptInput(final ConversationContext cc, final String s) {
                if (s.equalsIgnoreCase("yes")) {
                    for (final Player player : team.getOnlineMembers()) {
                        player.teleport(sender.getLocation());
                    }
                    sender.sendMessage(ChatColor.GREEN + "Teleported " + team.getOnlineMembers().size() + " to you.");
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
