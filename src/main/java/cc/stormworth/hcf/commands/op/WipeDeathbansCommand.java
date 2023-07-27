package cc.stormworth.hcf.commands.op;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class WipeDeathbansCommand {

    @Command(names = {"lives cleardeathbans"}, permission = "op", hidden = true)
    public static void wipeDeathbans(final Player sender) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        final ConversationFactory factory = new ConversationFactory(Main.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(final ConversationContext context) {
                return "§aAre you sure you want to wipe all deathbans? This action CANNOT be reversed. Type §byes§a to confirm or §cno§a to quit.";
            }

            public Prompt acceptInput(final ConversationContext cc, final String s) {
                if (s.equalsIgnoreCase("yes")) {
                    //Main.getInstance().getDeathbanMap().wipeDeathbans();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Deathbans have been wiped.");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Deathban wipe aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }
                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        final Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }
}
