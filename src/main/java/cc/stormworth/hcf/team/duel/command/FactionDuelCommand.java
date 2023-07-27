package cc.stormworth.hcf.team.duel.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.duel.FactionDuelManager;
import cc.stormworth.hcf.team.duel.arena.FactionDuelArena;
import cc.stormworth.hcf.team.duel.menu.FactionDuelMenu;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FactionDuelCommand {

  private static final FactionDuelManager factionDuelManager = Main.getInstance().getFactionDuelManager();

  @Command(names = "togglefactionduel", permission = "op")
  public static void togglefactionduel(Player player){
    factionDuelManager.setFactionDuelEnabled(!factionDuelManager.isFactionDuelEnabled());
    player.sendMessage(CC.translate("&eFaction Duel has been " + (factionDuelManager.isFactionDuelEnabled() ? "&aenabled" : "&cdisabled") + "&e."));
  }

  @Command(names = {"factionduel", "faction duel", "f duel", "fac duel", "team duel", "t duel"}, permission = "", async = true)
  public static void factionDuel(Player sender, @Param(name = "team") Team otherTeam) {


    if (!factionDuelManager.isFactionDuelEnabled()){
        sender.sendMessage(CC.RED + "Faction duel is currently disabled.");
        return;
    }

    if (factionDuelManager.getAllAvailableArenas().isEmpty()){
        sender.sendMessage(CC.RED + "There are no available arenas.");
        return;
    }

    if(!Main.getInstance().getMapHandler().isKitMap()){
      sender.sendMessage(CC.RED + "This is not a HCF command.");
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(CC.RED + "You are not in a faction.");
      return;
    }

    if(!team.getOwner().equals(sender.getUniqueId())) {
      sender.sendMessage(CC.RED + "You are not the owner of your faction.");
      return;
    }

    if (team == otherTeam) {
      sender.sendMessage(CC.RED + "You cannot duel yourself.");
      return;
    }

    if(team.getOnlineMemberAmount() < otherTeam.getOnlineMemberAmount()){
        sender.sendMessage(CC.RED + "You cannot duel a team with less players than you.");
        return;
    }

    Player owner = Bukkit.getPlayer(otherTeam.getOwner());

    if(owner == null){
      sender.sendMessage(CC.RED + "The owner of " + otherTeam.getName() + " team is not online.");
      return;
    }

    if (factionDuelManager.isInMatch(team)) {
      sender.sendMessage(CC.RED + "You are already in a faction duel.");
      return;
    }

    if (CooldownAPI.hasCooldown(sender, "FDUEL")){
        sender.sendMessage(CC.RED + "You cannot duel for another " + TimeUtil.millisToRoundedTime(CooldownAPI.getCooldown(sender, "FDUEL")));
        return;
    }

    if (factionDuelManager.isInMatch(otherTeam)) {
      sender.sendMessage(CC.RED + "The owner of " + otherTeam.getName() + " team is already in a faction duel.");
      return;
    }

    if (factionDuelManager.hasInvite(team, otherTeam)) {
      sender.sendMessage(CC.translate("&eYou've already dueled this player. Wait until their acceptation."));
      return;
    }

    new FactionDuelMenu(team, otherTeam, true).open(sender);
  }

  @Command(names = {"factionduel accept", "faction duel accept", "f duel accept", "fac duel accept", "team duel accept", "t duel accept"}, permission = "", async = true)
    public static void factionDuelAccept(final Player sender, @Param(name = "team") Team otherTeam) {

    if (factionDuelManager.getAllAvailableArenas().isEmpty()){
      sender.sendMessage(CC.RED + "There are no available arenas.");
      return;
    }

    if (!factionDuelManager.isFactionDuelEnabled()){
      sender.sendMessage(CC.RED + "Faction duel is currently disabled.");
      return;
    }

    if(!Main.getInstance().getMapHandler().isKitMap()){
      sender.sendMessage(CC.RED + "This is not a HCF command.");
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(sender);

    if (team == null) {
      sender.sendMessage(CC.RED + "You are not in a faction.");
      return;
    }

    if (!team.getOwner().equals(sender.getUniqueId())) {
      sender.sendMessage(CC.RED + "You are not the owner of your faction.");
      return;
    }

    if (!factionDuelManager.hasInvite(team, otherTeam)) {
      sender.sendMessage(CC.RED + "You have no pending faction duel requests.");
      return;
    }

    new FactionDuelMenu(team, otherTeam, false).open(sender);
  }

  @Command(names = {"factionduel createarena", "duel createarena"}, permission = "hcf.duelcommand", async = true)
  public static void createArenaDuel(final Player sender, @Param(name = "arena") String arena) {

    if (factionDuelManager.getArena(arena) != null) {
      sender.sendMessage(CC.translate("&cThat arena already exists!"));
      return;
    }

    factionDuelManager.getArenas().add(new FactionDuelArena(arena));

    sender.sendMessage(CC.translate("&eArena &6" + arena + " &acreated&e."));
  }

  @Command(names = {"factionduel deletearena", "duel deletearena"}, permission = "hcf.duelcommand", async = true)
  public static void deleteArenaDuel(final Player sender, @Param(name = "arena") String arena) {

    FactionDuelArena arena1 = factionDuelManager.getArena(arena);

    if (arena1 == null) {
      sender.sendMessage(CC.translate("&cArena \"" + arena + "\" not found!"));
      return;
    }

    if (arena1.isEnabled()) {
      sender.sendMessage(CC.translate("&cYou can't delete an arena while is enabled!"));
      return;
    }

    factionDuelManager.getArenas().remove(arena1);

    sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &cdeleted&e."));
  }

  @Command(names = {"factionduel togglearena", "duel togglearena"}, permission = "hcf.duelcommand", async = true)
  public static void toggleArenaDuel(final Player sender, @Param(name = "arena") String arena) {

    FactionDuelArena arena1 = factionDuelManager.getArena(arena);

    if (arena1 == null) {
      sender.sendMessage(CC.translate("&cArena \"" + arena + "\" not found!"));
      return;
    }

    if (!arena1.isEnabled()) {
      if (!arena1.canEnable()) {
        sender.sendMessage(
            CC.translate("&cYou must set both teams spawns in order to enable the arena!"));
        return;
      }

      arena1.setEnabled(true);

      sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &aenabled&e."));
    } else {
      arena1.setEnabled(false);

      sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &cdisabled&e."));
    }
  }

  @Command(names = {"setspawnduelarena", "duelspawn"}, permission = "hcf.duelcommand", async = true)
  public static void setSpawnWar(Player player, @Param(name = "arena") String arena, @Param(name = "type") String type) {

    FactionDuelArena arena1 = factionDuelManager.getArena(arena);

    if (arena1 == null) {
      player.sendMessage(CC.translate("&cArena \"" + arena + "\" not found!"));
      return;
    }

    if (arena1.isEnabled()) {
      player.sendMessage(CC.translate("&cYou can't modify an arena while is enabled!"));
      return;
    }

    switch (type) {
      case "1":
        arena1.setTeam1Spawn(player.getLocation().clone());

        player.sendMessage(
            CC.translate("&eFirst team spawn &aset &ffor arena &6" + arena1.getName() + "&e."));
        break;
      case "2":
        arena1.setTeam2Spawn(player.getLocation().clone());

        player.sendMessage(
            CC.translate("&eSecond team spawn &aset &ffor arena &6" + arena1.getName() + "&e."));
        break;
      default:
        player.sendMessage(CC.translate("&cType \"" + type + "\" not found!"));
    }
  }

}

