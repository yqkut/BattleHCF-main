package cc.stormworth.hcf.giveaway.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.giveaway.GiveAway;
import cc.stormworth.hcf.giveaway.GiveAwayType;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class GiveAwayNumberPrompt extends StringPrompt {

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate("&eEnter the max number for start giveaway: ");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the giveaway."));
      return Prompt.END_OF_CONVERSATION;
    }

    int max;

    try {
      max = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      player.sendMessage(CC.translate("&cYou must enter a number."));
      return this;
    }

    if (max < 1) {
      player.sendMessage(CC.translate("&cYou must enter a number greater than 0."));
      return this;
    }

    GiveAway giveAway = new GiveAway();

    giveAway.setType(GiveAwayType.NUMBER);
    giveAway.setMaxNumber(max);
    giveAway.generateRandomNumber();

    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(
        CC.translate("&7[&a&l✦&7] &4&l" + player.getName() + " &ehas started a &6&lGiveaway"));
    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(
        CC.translate("&7[&a&l✦&7] &eTry to guess which is the winner number. "));
    Bukkit.broadcastMessage("");
    Bukkit.broadcastMessage(
        CC.translate("&7[&a&l✦&7] &eGuess between &6&l1-" + giveAway.getMaxNumber()));

    giveAway.start();
    return Prompt.END_OF_CONVERSATION;
  }
}