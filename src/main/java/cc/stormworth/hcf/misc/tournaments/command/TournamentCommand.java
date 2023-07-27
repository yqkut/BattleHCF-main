package cc.stormworth.hcf.misc.tournaments.command;

import cc.stormworth.core.fancy.FormatingMessage;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.LocationUtil;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.tournaments.Tournament;
import cc.stormworth.hcf.misc.tournaments.TournamentState;
import cc.stormworth.hcf.misc.tournaments.TournamentType;
import cc.stormworth.hcf.misc.tournaments.menu.TournamentMenu;
import cc.stormworth.hcf.misc.tournaments.tasks.TournamentRunnable;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TournamentCommand {

    @Command(names = {"tournament leave", "leave"}, permission = "")
    public static void leave(Player player) {
        Tournament tournament = Main.getInstance().getTournamentHandler().getTournament();
        if (tournament != null) {
            if (!Main.getInstance().getTournamentHandler().isInTournament(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not participating in any event!");
            } else if (tournament.getHoster().getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(ChatColor.RED + "You cannot leave your own event!");
            } else {
                Main.getInstance().getTournamentHandler().leaveTournament(player);
            }
        } else {
            player.sendMessage(ChatColor.RED + "This event does not exist!");
        }
    }

    @Command(names = {"tournament join", "join"}, permission = "")
    public static void join(Player player) {
        Tournament tournament = Main.getInstance().getTournamentHandler().getTournament();
        ItemStack[] contents;
        for (int length = (contents = player.getInventory().getContents()).length, i = 0; i < length; ++i) {
            ItemStack item = contents[i];
            if (item != null) {
                player.sendMessage(ChatColor.RED + "To enter the event you must have an empty inventory!");
                return;
            }
        }
        if (tournament == null) {
            return;
        }

        int countdown = Main.getInstance().getTournamentHandler().getTournament().getCooldown();
        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You must be in spawn to join tournaments");
            return;
        }

        if (player.hasMetadata("ModMode")) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (HCFProfile.get(player).hasPvPTimer()) {
            player.sendMessage(ChatColor.RED + "You cannot do this while your PVPTimer is active!");
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "To remove your PvPTimer type '" + ChatColor.WHITE + "/pvp enable" + ChatColor.GRAY + ChatColor.ITALIC + "'.");
            return;
        }

        if (Main.getInstance().getTournamentHandler().isInTournament(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in an event!");
            return;
        }

        if (tournament.getPlayers().size() == tournament.getSize()) {
            player.sendMessage(ChatColor.RED + "The event is currently full.");
            return;
        }

        if (countdown == 0) {
            player.sendMessage(ChatColor.RED + "The event has already started!");
            return;
        }

        if (tournament.getTournamentState() == TournamentState.FIGHTING) {
            player.sendMessage(ChatColor.RED + "The event has already started!");
            return;
        }

        Main.getInstance().getTournamentHandler().joinTournament(player);
        tournament.saveInventory(player);
        if (player.getGameMode() != GameMode.SURVIVAL) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        if (player.isFlying()) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        tournament.teleport(player, "Spawn");
        tournament.giveItemWait(player);
    }


    @Command(names = {"tournament set"}, permission = "hcf.tournament.set")
    public static void set(Player player, @Param(name = "type") String type, @Param(name = "<spawn|first|second>") String tournament) {
        if (type.equalsIgnoreCase("spawn")) {
            Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Spawn", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
            Main.getInstance().getTournamentHandler().getTournament().getFile().save();
            player.sendMessage(ChatColor.GREEN + "Tournament Spawn location saved.");
        } else if (type.equalsIgnoreCase("sumo")) {
            if (tournament.equalsIgnoreCase("spawn")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Sumo.Spawn", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Sumo Spawn location saved.");
            } else if (tournament.equalsIgnoreCase("first")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Sumo.First", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Sumo First location saved.");
            } else if (tournament.equalsIgnoreCase("second")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Sumo.Second", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Sumo Second location saved.");
            } else if (tournament.equalsIgnoreCase("spectate")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Sumo.Spectate", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Sumo Spectate zone location saved.");
            } else {
                player.sendMessage(ChatColor.RED + "Tournament sub-command '" + tournament + "' not found.");
            }
        } else if (type.equalsIgnoreCase("ffa")) {
            if (tournament.equalsIgnoreCase("spawn")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.FFA.Spawn", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament FFA Spawn location saved.");
            } else {
                player.sendMessage(ChatColor.RED + "Tournament sub-command '" + tournament + "' not found.");
            }
        } else if (type.equalsIgnoreCase("axe")) {
            if (tournament.equalsIgnoreCase("spawn")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Axe.Spawn", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Axe Spawn location saved.");
            } else {
                player.sendMessage(ChatColor.RED + "Tournament sub-command '" + tournament + "' not found.");
            }
        } else if (type.equalsIgnoreCase("archer")) {
            if (tournament.equalsIgnoreCase("spawn")) {
                Main.getInstance().getTournamentHandler().getTournament().getFile().getConfig().set("Locations.Archer.Spawn", LocationUtil.convertLocation(String.valueOf(player.getLocation())));
                Main.getInstance().getTournamentHandler().getTournament().getFile().save();
                player.sendMessage(ChatColor.GREEN + "Tournament Archer Spawn location saved.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Tournament sub-command '" + tournament + "' not found.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Tournament sub-command '" + type + "' not found.");
        }
    }

    @Command(names = {"tournament create"}, permission = "hcf.tournament.host")
    public static void create(Player player, @Param(name = "maxplayers") int maxplayers, @Param(name = "sumo|diamond|axe|archer|rogue") String tournamentType) {
        ItemStack[] contents;
        for (int length = (contents = player.getInventory().getContents()).length, i = 0; i < length; ++i) {
            ItemStack item = contents[i];
            if (item != null) {
                player.sendMessage(ChatColor.RED + "To create an event you must have an empty inventory!");
                return;
            }
        }

        if (player.hasMetadata("ModMode")) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (maxplayers < 1) {
            player.sendMessage(ChatColor.RED + "Invalid size.");
            return;
        }
        if (maxplayers > 50) {
            player.sendMessage(ChatColor.RED + "Maximum size is 50.");
            return;
        }

        try {
            TournamentType type = TournamentType.valueOf(tournamentType.toUpperCase());
            Main.getInstance().getTournamentHandler().createTournament(player, maxplayers, type, player);
            player.performCommand("tournament join");
            for (Player online : Bukkit.getOnlinePlayers()) {
                String name = player.getDisplayName();
                Tournament tournament = Main.getInstance().getTournamentHandler().getTournament();
                FormatingMessage message = new FormatingMessage("");
                message.then(CC.translate("&4&l" + type.getName() + " &7hosted by &r" + name + " &f(" + "&a" + tournament.getPlayers().size() + "&f/&a" + tournament.getSize() + "&f)"))
                        .tooltip(CC.translate("&aClick to enter")).command("/tournament join").send(online);
            }
            new TournamentRunnable(Main.getInstance().getTournamentHandler().getTournament()).runAnnounce();
        } catch (Exception e) {
            player.sendMessage(CC.translate("&eTournamentType not found."));
        }
    }

    @Command(names = {"tournament cancel", "tournament stop", "tournament end"}, permission = "hcf.command.host")
    public static void stop(Player player) {
        if (!Main.getInstance().getTournamentHandler().isCreated()) {
            player.sendMessage(ChatColor.RED + "There is currently no active event.");
            return;
        }

        if (Main.getInstance().getTournamentHandler().getTournament().getHoster() != player) {
            player.sendMessage(ChatColor.RED + "You need to be the host to cancel the event!");
            return;
        }

        Tournament tournament = Main.getInstance().getTournamentHandler().getTournament();
        if (tournament != null) {
            Main.getInstance().getTournamentHandler().setCreated(false);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (Main.getInstance().getTournamentHandler().isInTournament(online.getUniqueId())) {
                    tournament.rollbackInventory(online);
                    Main.getInstance().getTournamentHandler().kickPlayer(online.getUniqueId());
                    online.sendMessage(ChatColor.RED + "You have been kicked from the event because it " + ChatColor.BOLD + "CANCELED" + ChatColor.RED + "!");
                    online.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }
            }
        }
    }


    @Command(names = {"tournament status", "status"}, permission = "")
    public static void status(Player player) {
        Tournament tournament = Main.getInstance().getTournamentHandler().getTournament();
        if (tournament == null) {
            player.sendMessage(ChatColor.RED + "There isn't an active tournament");
            return;
        }
        if (tournament.getTournamentState() == TournamentState.FIGHTING) {
            player.sendMessage(CC.translate("&7&m----------------------------------------"));
            player.sendMessage(CC.translate("&eType&7: &f" + WordUtils.capitalizeFully(tournament.getType().name())));
            player.sendMessage(CC.translate("&eRound&7: &f" + tournament.getCurrentRound()));
            if (tournament.getType() == TournamentType.SUMO) {
                player.sendMessage(CC.translate("&4Current Fight:"));
                player.sendMessage(CC.translate("   &4" + tournament.getFirstPlayer().getDisplayName() + " &7vs &2" + tournament.getSecondPlayer().getDisplayName()));
            }
            player.sendMessage(CC.translate("&eNext Round&7: &f" + (tournament.getCurrentRound() + 1)));
            player.sendMessage(CC.translate("&ePlayers&7: &f" + tournament.getPlayers().size() + "&7/&f" + tournament.getSize()));
            player.sendMessage(CC.translate("&eHoster&7: &f" + tournament.getHoster().getName()));
            player.sendMessage(CC.translate("&7&m----------------------------------------"));
        } else if (tournament.getTournamentState() == TournamentState.STARTING) {
            player.sendMessage(CC.translate("&7&m----------------------------------------"));
            player.sendMessage(CC.translate("&4The event is Starting..."));
            player.sendMessage("");
            player.sendMessage(CC.translate("&eEvent&7: &f(&a" + tournament.getType().toString() + "&f)"));
            player.sendMessage(CC.translate("&cPlayers&7: &f" + tournament.getPlayers().size() + "/" + tournament.getSize()));
            player.sendMessage(CC.translate("&7&m----------------------------------------"));
        }
    }

    @Command(names = {"tournament host", "host"}, permission = "hcf.command.host")
    public static void host(Player player) {
        new TournamentMenu().openMenu(player);
    }
}