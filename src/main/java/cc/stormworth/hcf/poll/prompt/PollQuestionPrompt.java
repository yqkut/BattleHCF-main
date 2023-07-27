package cc.stormworth.hcf.poll.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.poll.Poll;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class PollQuestionPrompt extends StringPrompt {

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate(
        "&ePlease write the question for the poll. &7&o(Or type cancel)");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the poll creation."));
      return Prompt.END_OF_CONVERSATION;
    }

    player.sendMessage(CC.translate("&aSuccessfully set the question."));
    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    return new PollTimePrompt(new Poll(input, player.getName()));
  }
}