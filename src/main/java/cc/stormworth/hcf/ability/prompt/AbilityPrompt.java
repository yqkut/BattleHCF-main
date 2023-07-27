package cc.stormworth.hcf.ability.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.ability.Ability;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class AbilityPrompt extends StringPrompt {

  private final Ability ability;

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate("&eEnter  amount of items you want to get. &7&oOr type cancel ");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      player.sendMessage(CC.translate("&cYou have cancelled ability give."));
      return Prompt.END_OF_CONVERSATION;
    }

    int amount;

    try {
      amount = Integer.parseInt(input);

      if (amount < 1) {
        player.sendMessage(CC.translate("&cYou must enter a number greater than 0."));
        return this;
      }

      ItemStack item = ability.getItem().clone();

      item.setAmount(amount);

      player.getInventory().addItem(item);

      player.sendMessage(CC.translate("&aYou received &e" + ability.getName() + " &aitem."));
    } catch (NumberFormatException e) {
      player.sendMessage(CC.translate("&cYou must enter a valid number."));
      return this;
    }

    return null;
  }
}