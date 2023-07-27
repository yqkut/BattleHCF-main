package cc.stormworth.hcf.voteparty;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.menu.Menu;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.NameMCApi;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class VotePartyMenu extends Menu {

  private final VotePartyHandler votePartyHandler;

  public VotePartyMenu(VotePartyHandler votePartyHandler) {
    setAutoUpdate(true);
    this.votePartyHandler = votePartyHandler;
  }

  @Override
  public String getTitle(Player player) {
    return CC.translate("&6&lVote Party");
  }


  @Override
  public Map<Integer, Button> getButtons(Player player) {
    Map<Integer, Button> buttons = Maps.newHashMap();

    buttons.put(4, new Button() {

      @Override
      public String getName(Player player) {
        return CC.translate("&7(&6&lParty Vote&7)");
      }

      @Override
      public List<String> getDescription(Player player) {
        return CC.translate(
            Lists.newArrayList(
                "",
                "&7Here you will find a Votes Counter. Every &6&l50 &evotes",
                "&7will be sort automatically a &6&lKeyAll",
                "",
                "&eVotes:&f " + votePartyHandler.getCurrentVotes() + "&7/&f"
                    + votePartyHandler.getMAX_VOTES()
            )
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.DOUBLE_PLANT;
      }
    });

    buttons.put(getSlot(4, 2), new Button() {

      @Override
      public String getName(Player player) {
        return CC.translate("&6&lNameMC");
      }

      @Override
      public List<String> getDescription(Player player) {
        return CC.translate(
            Lists.newArrayList(
                "",
                "&7Vote to adquire &a&lFree &7rewards.",
                "",
                "&6&lRewards:",
                "&e&l. &a&lVerified &7Rank",
                "&e&l. &6&l500 &7Coins",
                "&e&l. &6&lBattle &7Tag",
                "",
                "&eClick to vote!",
                "",
                "&c&lWarning: You must verify your vote with: /checknamemc"
            )
        );
      }

      @Override
      public Material getMaterial(Player player) {
        return Material.DIAMOND;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {

        if (votePartyHandler.isOnCooldown()) {
          player.sendMessage(CC.translate("&cYou can't use this command right now."));
          return;
        }

        if (votePartyHandler.hasVote(player)) {
          player.sendMessage(CC.translate("&cYou already vote."));
          return;
        }

        player.closeInventory();
        player.sendMessage(CC.translate("&aChecking vote..."));

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() ->
            NameMCApi.isLiked(player)
        );

        future.thenAccept(isLiked -> {

          if (!isLiked) {
            player.sendMessage(CC.translate("&cYou must like the server to vote."));
            player.sendMessage(CC.translate(
                "&eYou can vote on:&f https://es.namemc.com/server/battle.rip?q=battle.rip"));
            return;
          }

          TaskUtil.run(Main.getInstance(), () -> votePartyHandler.vote(player));
        });
      }
    });

    return buttons;
  }
}