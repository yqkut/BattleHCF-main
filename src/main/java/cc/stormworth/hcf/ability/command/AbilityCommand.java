package cc.stormworth.hcf.ability.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.ability.Ability;
import cc.stormworth.hcf.ability.prompt.AbilityPrompt;
import cc.stormworth.hcf.misc.request.Request;
import cc.stormworth.hcf.util.chat.ChatUtils;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AbilityCommand {

  @Command(names = {"ability list", "abilitys"}, permission = "DEVELOPER", async = true)
  public static void list(final Player sender) {
    sender.sendMessage(CC.translate("&7&m----------------------------------"));
    for (Ability ability : Ability.getAbilities()) {
      Clickable clickable = new Clickable(CC.translate("&7- " + ability.getDisplayName()),
          CC.translate("&aClick to receive " + ability.getDisplayName()),
          "/ability get " + ability.getName());
      clickable.sendToPlayer(sender);
    }
    sender.sendMessage(CC.translate("&7&m----------------------------------"));
  }

  @Command(names = {"ability getall"}, permission = "DEVELOPER", async = true)
  public static void getall(final Player sender) {
    for (Ability ability : Ability.getAbilities()) {
      ItemStack item = ability.getItem().clone();
      if (item != null) {
        sender.getInventory().addItem(item);
      }
    }

    sender.sendMessage(CC.translate("&aYou have been given all abilities."));
  }

  @Command(names = {"ability get"}, permission = "DEVELOPER", async = true)
  public static void get(final Player player, @Param(name = "ability") final String abilityName) {
    Ability ability = Ability.getByName(abilityName);

    if (ability == null) {
      player.sendMessage(CC.translate("&cInvalid ability name."));
      return;
    }

    ChatUtils.beginPrompt(player, new AbilityPrompt(ability));
  }

  @Command(names = {"ability toggle"}, permission = "DEVELOPER", async = true)
  public static void toggle(final Player player,
      @Param(name = "ability") final String abilityName) {
    Ability ability = Ability.getByName(abilityName);

    if (ability == null) {
      player.sendMessage(CC.translate("&cInvalid ability name."));
      return;
    }

    if (ability.isEnabled()) {
      ability.setEnabled(false);
      player.sendMessage(CC.translate("&cDisabled &l" + ability.getDisplayName()));
    } else {
      ability.setEnabled(true);
      player.sendMessage(CC.translate("&aEnabled &l" + ability.getDisplayName()));
    }
  }


  @Command(names = {"ability reset"}, permission = "DEVELOPER", async = true)
  public static void reset(final Player sender, @Param(name = "target") final Player target) {
    for (Ability ability : Ability.getAbilities()) {

      if (CooldownAPI.hasCooldown(target, ability.getName())) {
        CooldownAPI.removeCooldown(target, ability.getName());
        CooldownAPI.removeCooldown(target, "Global");
        target.sendMessage(CC.translate("&aReset &l" + ability.getDisplayName() + " &acooldown"));
      }
    }
  }

  @Command(names = {"ability give"}, permission = "DEVELOPER", async = true)
  public static void give(final CommandSender sender, @Param(name = "player") final Player target,
      @Param(name = "ability") final String name, @Param(name = "amount") final int amount) {
    Ability ability = Ability.getByName(name);

    if (ability == null) {
      sender.sendMessage(CC.translate("&cInvalid ability name."));
      return;
    }

    ItemStack item = ability.getItem().clone();

    item.setAmount(amount);

    target.getInventory().addItem(item);

    target.sendMessage(CC.translate("&aYou received &e" + ability.getName() + " &aitem."));
    sender.sendMessage(CC.translate("&aYou gave &e" + target.getName() + " &aitem."));
  }


  @Command(names = "acceptsacrifice", permission = "DEFAULT")
  public static void acceptsacrifice(Player player){
    if (Request.hasRequest(player)) {
        Request request = Request.getRequest(player);

        request.execute();
        player.sendMessage(CC.translate("&aYou have accepted the sacrifice."));
        Player other = request.getRequesterPlayer();
        other.sendMessage(CC.translate("&c" + player.getName() + " &ahas accepted the sacrifice."));
    }
  }
}