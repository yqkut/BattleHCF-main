package cc.stormworth.hcf.giveaway.prompt;

import cc.stormworth.core.util.chat.CC;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class GiveAwayTypePrompt extends StringPrompt {

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate(
        "&eWhat type of giveaway would you like to create? &7&o(number or raffle). (Or type cancel to cancel)");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("number")) {
      return new GiveAwayNumberPrompt();
    } else if (input.equalsIgnoreCase("raffle")) {
      return new GiveAwayRafflePrompt();
    }else if (input.equalsIgnoreCase("spam")) {
      return new GiveAwayRafflePrompt();
    } else if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the giveaway."));
      return Prompt.END_OF_CONVERSATION;
    } else {
      player.sendMessage(CC.translate("&cInvalid input, please try again."));
      player.sendMessage(CC.translate("&7&o(number or raffle)"));
      return this;
    }
  }
}