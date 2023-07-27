package cc.stormworth.hcf.bounty.prompt;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.bounty.menu.BountyBalanceMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class BountySelectBalancePrompt extends StringPrompt {

  private final BountyPlayer bountyPlayer;

  @Override
  public String getPromptText(ConversationContext conversationContext) {
    return CC.translate(
        "&7Please, type the amount of money that you are going to add as Reward for &6"
            + bountyPlayer.getTarget().getName() + "&7. Or type &6cancel&7 to go back.");
  }

  @Override
  public Prompt acceptInput(ConversationContext context, String input) {

    Player player = (Player) context.getForWhom();

    if (input.equalsIgnoreCase("cancel")) {
      new BountyBalanceMenu(bountyPlayer).openMenu(player);
      return END_OF_CONVERSATION;
    }

    try {
      int amount = Integer.parseInt(input);
      HCFProfile profile = HCFProfile.get(player);

      if (bountyPlayer.getBalance() > 0) {
        profile.getEconomyData().addBalance(bountyPlayer.getBalance());
        bountyPlayer.setBalance(0);
      }


      if (profile.getEconomyData().getBalance() < amount) {
        player.sendMessage(CC.translate("&cYou do not have enough money to do this."));
        return this;
      }

      profile.getEconomyData().subtractBalance(amount);
      bountyPlayer.setBalance(amount);

      player.sendMessage(CC.translate("&aYou have set the Reward to &6" + amount + "&a."));

      new BountyBalanceMenu(bountyPlayer).openMenu(player);
      return END_OF_CONVERSATION;
    } catch (NumberFormatException e) {
      player.sendMessage(CC.translate("&cInvalid number."));
      return this;
    }
  }
}