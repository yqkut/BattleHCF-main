package cc.stormworth.hcf.util.chat;

import cc.stormworth.hcf.Main;
import lombok.experimental.UtilityClass;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@UtilityClass
public class ChatUtils {

  public void beginPrompt(Player player, StringPrompt prompt) {
    player.closeInventory();
    player.beginConversation(
        new ConversationFactory(Main.getInstance())
            .withFirstPrompt(prompt)
            .withTimeout(60)
            .withModality(false)
            .withLocalEcho(false)
            .buildConversation(player));
  }
}