package cc.stormworth.hcf.commands.vote;

import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.voteparty.VotePartyHandler;
import cc.stormworth.hcf.voteparty.VotePartyMenu;
import org.bukkit.entity.Player;

public class VoteCommand {
  @Command(names= {"vote", "partyvote", "namemc"}, permission = "DEFAULT")
  public static void vote(Player player){
    VotePartyHandler votePartyHandler = Main.getInstance().getVotePartyHandler();

    new VotePartyMenu(votePartyHandler).openMenu(player);
  }
}