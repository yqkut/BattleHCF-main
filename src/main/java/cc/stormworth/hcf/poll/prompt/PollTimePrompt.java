package cc.stormworth.hcf.poll.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.poll.Poll;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PollTimePrompt extends StringPrompt {

  private final Poll poll;

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate("&eEnter the duration of the poll.");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled the poll creation."));
      return Prompt.END_OF_CONVERSATION;
    }

    long time = TimeUtil.parseTimeLong(input);

    if (time < 0) {
      player.sendMessage(CC.translate("&cInvalid time format."));
      return this;
    }

    poll.setDuration(time);

    player.sendMessage(
        CC.translate(
            "&aThe duration of the poll has been set to &e" + TimeUtil.millisToRoundedTime(time)));
    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

    poll.start(player);

    Main.getInstance().getPollHandler().setCurrentPoll(poll);
    return null;
  }
}