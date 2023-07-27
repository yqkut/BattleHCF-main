package cc.stormworth.hcf.commands.economy;

public class EcoCheckCommand {

 /* @Command(names = {"ecocheck"}, permission = "hcf.op")
  public static void ecoCheck(Player sender) {
    if (sender.getGameMode() != GameMode.CREATIVE) {
      sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
      return;
    }

    for (Team team : Main.getInstance().getTeamHandler().getTeams()) {
      if (isBad(team.getBalance())) {
        sender.sendMessage(ChatColor.YELLOW + "Team: " + ChatColor.WHITE + team.getName());
      }
    }

    try {
      Map<UUID, Double> balances = EconomyHandler.getBalances();

      for (Map.Entry<UUID, Double> balanceEntry : balances.entrySet()) {
        if (isBad(balanceEntry.getValue())) {
          sender.sendMessage(ChatColor.YELLOW + "Player: " + ChatColor.WHITE + UUIDUtils.name(balanceEntry.getKey()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static boolean isBad(double bal) {
    return (Double.isNaN(bal) || Double.isInfinite(bal));
  }*/

}