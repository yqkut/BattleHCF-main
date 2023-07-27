package cc.stormworth.hcf.poll.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.poll.GlobalPoll;
import cc.stormworth.hcf.poll.Poll;
import cc.stormworth.hcf.poll.menu.PollMenu;
import cc.stormworth.hcf.poll.menu.PollQuickMenu;
import cc.stormworth.hcf.poll.prompt.PollQuestionPrompt;
import cc.stormworth.hcf.util.chat.ChatUtils;
import org.bukkit.entity.Player;

public class PollCommand {

  @Command(names = {"poll", "polls"}, permission = "")
  public static void poll(Player player) {
    if (Main.getInstance().getPollHandler().getCurrentPoll() == null ||
        Main.getInstance().getPollHandler().getCurrentPoll().isFinished()) {
      new PollMenu().openMenu(player);
    } else {
      new PollQuickMenu(Main.getInstance().getPollHandler().getCurrentPoll()).openMenu(player);
    }
  }

  @Command(names = {"poll quick", "polls quick"}, permission = "")
  public static void quick(Player player) {

    if (!player.hasPermission("hcf.poll.quick")) {
      if (Main.getInstance().getPollHandler().getCurrentPoll() == null ||
          Main.getInstance().getPollHandler().getCurrentPoll().isFinished()) {
        player.sendMessage(CC.translate("&cThere is no poll currently running."));
        return;
      }

      new PollQuickMenu(Main.getInstance().getPollHandler().getCurrentPoll()).openMenu(player);

    } else {
      if (Main.getInstance().getPollHandler().getCurrentPoll() != null &&
          !Main.getInstance().getPollHandler().getCurrentPoll().isFinished()) {
        new PollQuickMenu(Main.getInstance().getPollHandler().getCurrentPoll()).openMenu(player);
        return;
      }

      ChatUtils.beginPrompt(player, new PollQuestionPrompt());
    }
  }

  @Command(names = {"poll cancel", "polls cancel"}, permission = "DEVELOPER")
  public static void cancel(Player player) {
    if (Main.getInstance().getPollHandler().getCurrentPoll() == null ||
        Main.getInstance().getPollHandler().getCurrentPoll().isFinished()) {
      player.sendMessage(CC.translate("&cThere is no poll currently running."));
      return;
    }

    Main.getInstance().getPollHandler().getCurrentPoll().cancel();
    player.sendMessage(CC.translate("&aPoll cancelled."));
  }

  @Command(names = {"poll results", "polls results"}, permission = "DEVELOPER")
  public static void results(Player player) {
    player.sendMessage(CC.translate("&6&lPoll Results"));
    player.sendMessage("");
    GlobalPoll knockbackPoll = Main.getInstance().getPollHandler().getPoll("Knockback");
    GlobalPoll crossPearlPoll = Main.getInstance().getPollHandler().getPoll("Cross Pearl");

    new Clickable("&7-&f Knockback: " + knockbackPoll.getResult(), "&7Click to see more info.",
        "/poll result Knockback").sendToPlayer(player);

    new Clickable("&7-&f Cross Pearl: " + crossPearlPoll.getResult(), "&7Click to see more info.",
        "/poll result Cross Pearl").sendToPlayer(player);

    player.sendMessage("");
    player.sendMessage(CC.translate("&eAbilities Polls:"));
    for (Ability ability : Ability.getAbilities()) {
      GlobalPoll poll = Main.getInstance().getPollHandler().getPoll(ability.getName());
      new Clickable("&7-&f " + ability.getName() + ": " + poll.getResult(),
          "&7Click to see more info.",
          "/poll result " + ability.getName()).sendToPlayer(player);
    }
    player.sendMessage("");
  }

  @Command(names = {"poll result", "polls result"}, permission = "DEVELOPER")
  public static void results(Player player,
      @Param(name = "poll", defaultValue = "last", wildcard = true) String pollName) {

    if (pollName.equalsIgnoreCase("last")) {
      Poll poll = Main.getInstance().getPollHandler().getCurrentPoll();
      if (poll == null) {
        player.sendMessage(CC.translate("&cThere is no poll currently running."));
        return;
      }

      poll.sendResults(player);
      return;
    }
    GlobalPoll poll = Main.getInstance().getPollHandler().getPoll(pollName);

    if (poll == null) {
      player.sendMessage(CC.translate("&cThere is no poll by that name."));
      return;
    }

    poll.sendResults(player);
  }

  @Command(names = {"poll help", "polls help"}, permission = "")
  public static void help(Player player) {
    if (!player.hasPermission("battle.poll.help")) {
      player.sendMessage(CC.translate("&cUsage: /poll"));
      return;
    }

    player.sendMessage(CC.translate("&cUsage: /poll"));
    player.sendMessage(CC.translate("&cUsage: /poll quick"));
    player.sendMessage(CC.translate("&cUsage: /poll results"));
  }
}