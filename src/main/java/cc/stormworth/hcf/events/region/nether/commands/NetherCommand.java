package cc.stormworth.hcf.events.region.nether.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.events.region.nether.NetherArea;
import cc.stormworth.hcf.events.region.nether.NetherHandler;
import cc.stormworth.hcf.events.region.nether.NetherRespawnTask;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

public class NetherCommand {

    @Command(names = "nether scan", permission = "op")
    public static void scan(Player sender) {
        Team team = Main.getInstance().getTeamHandler().getTeam(NetherHandler.getTeamName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + NetherHandler.getTeamName() + ") and claim it!");
            return;
        }

        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + NetherHandler.getTeamName() + "' before scanning it!");
            return;
        }

        if (!Main.getInstance().getNetherHandler().hasArea()) {
            Main.getInstance().getNetherHandler().setArea(new NetherArea());
        }

        Main.getInstance().getNetherHandler().getArea().scan();
        Main.getInstance().getNetherHandler().save();

        sender.sendMessage(YELLOW + "[Nether] Scanned all saved to file!");
    }

    @Command(names = "nether reset", permission = "op")
    public static void reset(Player sender) {
        Team team = Main.getInstance().getTeamHandler().getTeam(NetherHandler.getTeamName());

        if (team == null || team.getClaims().isEmpty() || !Main.getInstance().getNetherHandler().hasArea()) {
            sender.sendMessage(RED + "Create the team '" + NetherHandler.getTeamName() + "', then make a claim for it, finally scan it! (/nether scan)");
            return;
        }

        NetherRespawnTask.makeReset();
    }

    @Command(names = {"nether set"}, permission = "op")
    public static void set(final Player sender) {
        final Team team = Main.getInstance().getTeamHandler().getTeam(NetherHandler.getTeamName());
        team.setHQ(sender.getLocation());
        sender.sendMessage(CC.translate("&aSuccessfully updated location!"));
    }
}