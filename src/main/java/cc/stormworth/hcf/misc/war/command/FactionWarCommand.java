package cc.stormworth.hcf.misc.war.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.FactionWar;
import cc.stormworth.hcf.misc.war.FactionWarManager;
import cc.stormworth.hcf.misc.war.arena.FactionWarArena;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import cc.stormworth.hcf.misc.war.menu.FactionWarJoinMenu;
import cc.stormworth.hcf.team.Team;
import org.bukkit.entity.Player;

public class FactionWarCommand {
    private static final FactionWarManager factionWarManager = Main.getInstance().getFactionWarManager();

    @Command(names = {"createarena", "war createarena"}, permission = "hcf.warcommand", async = true)
    public static void createArena(final Player sender, @Param(name = "arena") final String arena) {

        if (factionWarManager.getArena(arena) != null) {
            sender.sendMessage(CC.translate("&cThat arena already exists!"));
            return;
        }

        factionWarManager.getArenas().add(new FactionWarArena(arena));

        sender.sendMessage(CC.translate("&eArena &6" + arena + " &acreated&e."));

    }

    @Command(names = {"deletearena", "war deletearena"}, permission = "hcf.warcommand", async = true)
    public static void deleteArena(final Player sender, @Param(name = "arena") final String arena) {
        FactionWarArena arena1 = factionWarManager.getArena(arena);

        if (arena1 == null) {
            sender.sendMessage(CC.translate("&cArena \"" + arena + "\" not found!"));
            return;
        }

        if (arena1.isEnabled()) {
            sender.sendMessage(CC.translate("&cYou can't delete an arena while is enabled!"));
            return;
        }

        factionWarManager.getArenas().remove(arena1);

        sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &cdeleted&e."));

    }

    @Command(names = {"setspawnarena", "warspawn"}, permission = "hcf.warcommand", async = true)
    public static void setSpawnWar(final Player player, @Param(name = "arena") final String arena, @Param(name = "type") final String type) {

        FactionWarArena arena1 = factionWarManager.getArena(arena);

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

                player.sendMessage(CC.translate("&eFirst team spawn &aset &ffor arena &6" + arena1.getName() + "&e."));
                break;
            case "2":
                arena1.setTeam2Spawn(player.getLocation().clone());

                player.sendMessage(CC.translate("&eSecond team spawn &aset &ffor arena &6" + arena1.getName() + "&e."));
                break;
            default:
                player.sendMessage(CC.translate("&cType \"" + type + "\" not found!"));
        }
    }

    @Command(names = {"togglewararena"}, permission = "hcf.warcommand", async = true)
    public static void toggleWarArena(final Player sender, @Param(name = "arena") final String arena) {

        FactionWarArena arena1 = factionWarManager.getArena(arena);

        if (arena1 == null) {
            sender.sendMessage(CC.translate("&cArena \"" + arena + "\" not found!"));
            return;
        }

        if (!arena1.isEnabled()) {
            if (!arena1.canEnable()) {
                sender.sendMessage(CC.translate("&cYou must set both teams spawns in order to enable the arena!"));
                return;
            }

            arena1.setEnabled(true);

            sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &aenabled&e."));
        } else {
            arena1.setEnabled(false);

            sender.sendMessage(CC.translate("&eArena &6" + arena1.getName() + " &cdisabled&e."));
        }
    }

    @Command(names = {"warjoin", "war join", "joinwar"}, permission = "", async = true)
    public static void warJoin(final Player player) {

        if (factionWarManager.getActiveWar() == null) {
            player.sendMessage(CC.translate("&cThere isn't an active faction war!"));
            return;
        }

        FactionWar war = factionWarManager.getActiveWar();
        Team faction = Main.getInstance().getTeamHandler().getTeam(player);

        if (faction == null) {
            player.sendMessage(CC.translate("&cYou dont have faction"));
            return;
        }

        if (!player.getUniqueId().equals(faction.getOwner())) {
            player.sendMessage(CC.translate("you are not the faction leader"));
            return;
        }

        if (war.getState() != FactionWar.FactionWarState.STARTING) {
            player.sendMessage(CC.translate("&cThe faction war has already started!"));
            return;
        }

        if (war.isFull()) {
            player.sendMessage(CC.translate("&cThe faction war is full!"));
            return;
        }

        if (faction.getMembers().size() > faction.getOnlineMembers().size()) {
            player.sendMessage(CC.translate("&cAll members of your faction must be online in order to join the faction war!"));
            return;
        }

        new FactionWarJoinMenu(faction, war).openMenu(player);

    }

    @Command(names = {"warleave", "war leave", "leavewar"}, permission = "", async = true)
    public static void warLeave(final Player player) {
        if (factionWarManager.getActiveWar() == null) {
            player.sendMessage(CC.translate("&cThere isn't an active faction war!"));
            return;
        }

        FactionWar war = factionWarManager.getActiveWar();
        Team faction = Main.getInstance().getTeamHandler().getTeam(player);

        if (faction == null) {
            player.sendMessage(CC.translate("&cYou dont have faction"));
            return;
        }

        if (!player.getUniqueId().equals(faction.getOwner())) {
            player.sendMessage(CC.translate("you are not the faction leader"));
            return;
        }

        if (war.getState() == FactionWar.FactionWarState.PLAYING) {
            FactionWarMatch match = war.getMatch(faction.getUniqueId());

            if (match != null) {
                match.getParticipantsCache().get(player.getUniqueId()).getAliveInMatch().clear();
                match.tryFinish();
            }
        } else {
            war.removeParticipant(war.getParticipantByFaction(faction.getUniqueId()), true);
        }

        faction.sendMessage(CC.translate("&eYour faction has successfully &cleft &ethe faction war."));
    }

    @Command(names = {"warstart", "war start", "starwar"}, permission = "hcf.warcommand", async = true)
    public static void warstart(final Player sender) {
        if (factionWarManager.getActiveWar() != null) {
            sender.sendMessage(CC.translate("&cA faction war is already in progress!"));
            return;
        }

        factionWarManager.setActiveWar(new FactionWar());

        sender.sendMessage(CC.translate("&eFaction war &asuccessfully &estarted."));
    }
}
