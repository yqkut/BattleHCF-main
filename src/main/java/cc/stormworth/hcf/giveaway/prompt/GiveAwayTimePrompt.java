package cc.stormworth.hcf.giveaway.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.giveaway.GiveAway;
import cc.stormworth.hcf.giveaway.GiveAwayType;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class GiveAwayTimePrompt extends StringPrompt {

  private final GiveAway giveAway;

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate("&eHow long should the giveaway?");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the giveaway."));
      return Prompt.END_OF_CONVERSATION;
    }

    long time = TimeUtil.parseTimeLong(input);

    if (time < 0) {
      player.sendMessage(CC.translate("&cInvalid time format."));
      return this;
    }

    giveAway.setTime(System.currentTimeMillis() + time);

    if (giveAway.getType() == GiveAwayType.RAFFLE) {
      Bukkit.broadcastMessage("");
      Bukkit.broadcastMessage(
          CC.translate("&7[&a&l✦&7] &4&l" + player.getName() + " &ehas started a &6&lGiveaway"));
      Bukkit.broadcastMessage(
          CC.translate("&7[&a&l✓&7] &eIn &6&l" + TimeUtil.millisToRoundedTime(time)
              + " &eone player will be randomly selected."));
      Bukkit.broadcastMessage("");
      Bukkit.broadcastMessage(
          CC.translate(
              "&7[&a&l✓&7] &ePlease type \"&6&l" + giveAway.getWord() + "&e\" to participate."));
      Bukkit.broadcastMessage("");
    }

    giveAway.start();
    return Prompt.END_OF_CONVERSATION;
  }
}