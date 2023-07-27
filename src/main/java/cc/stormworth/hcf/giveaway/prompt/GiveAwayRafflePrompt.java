package cc.stormworth.hcf.giveaway.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.giveaway.GiveAway;
import cc.stormworth.hcf.giveaway.GiveAwayType;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class GiveAwayRafflePrompt extends StringPrompt {

  @Override
  public String getPromptText(ConversationContext context) {
    return CC.translate(
        "&eEnter a random word to be used for participate in the giveaway. &7&o(Or type cancel to cancel)");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the giveaway."));
      return Prompt.END_OF_CONVERSATION;
    }

    GiveAway giveAway = new GiveAway();
    giveAway.setType(GiveAwayType.RAFFLE);
    giveAway.setWord(input);

    player.sendMessage(CC.translate("&aYou have successfully set the raffle word to &7" + input));

    return new GiveAwayTimePrompt(giveAway);
  }
}