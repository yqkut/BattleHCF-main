package cc.stormworth.hcf.team.commands.team;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamLivesCommand {
    @Command(names = {"team lives add", "t lives add", "f lives add", "fac lives add", "faction lives add", "t lives deposit", "t lives d", "f lives deposit", "f lives d", "lives deposit"}, permission = "", async = true)
    public static void livesAdd(final Player sender, @Param(name = "lives") final int lives) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }
        if (lives <= 0) {
            sender.sendMessage(ChatColor.RED + "You really think we'd fall for that?");
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(sender.getUniqueId());
        final int currLives = hcfProfile.getLives();
        if (currLives < lives) {
            sender.sendMessage(ChatColor.RED + "You only have " + ChatColor.YELLOW + currLives + ChatColor.RED + " lives, you cannot deposit " + ChatColor.YELLOW + lives);
            return;
        }
        hcfProfile.removeLives(lives);
        team.addLives(lives);
        sender.sendMessage(ChatColor.GREEN + "You have deposited " + ChatColor.RED + lives + ChatColor.GREEN + " lives to " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ". You now have " + ChatColor.RED + (currLives - lives) + ChatColor.GREEN + " lives and your team now has " + ChatColor.RED + team.getLives() + ChatColor.GREEN + " lives.");
    }

    @Command(names = {"team revive", "t revive", "f revive", "fac revive", "faction revive"}, permission = "")
    public static void livesRevive(final Player sender, @Param(name = "player") final UUID toReviveUUID) {
        Team team = Main.getInstance().getTeamHandler().getTeam(sender);

        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }
        if (!team.isCoLeader(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only co-leaders and owners can use this command!");
            return;
        }
        if (team.getLives() <= 0) {
            sender.sendMessage(ChatColor.RED + "Your team has no lives to use.");
            return;
        }
        if (!team.isMember(toReviveUUID)) {
            sender.sendMessage(ChatColor.RED + "This player is not a member of your team.");
            return;
        }
        HCFProfile hcfProfile = HCFProfile.getByUUID(toReviveUUID);

        if (hcfProfile == null){

            CompletableFuture<HCFProfile> future = HCFProfile.load(toReviveUUID);

            future.thenAccept(profile -> {

                if (profile == null){
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                    return;
                }

                if (!profile.isDeathBanned()){
                    sender.sendMessage(ChatColor.RED + "This player is not death banned currently.");
                    return;
                }

                profile.setDeathban(null);

                profile.asyncSave();

                sender.sendMessage(ChatColor.GREEN + "You have revived " + ChatColor.RED + UUIDUtils.name(toReviveUUID) + ChatColor.GREEN + ".");
            });

            return;
        }

        if (!hcfProfile.isDeathBanned()) {
            sender.sendMessage(ChatColor.RED + "This player is not death banned currently.");
            return;
        }
        team.removeLives(1);
        hcfProfile.getDeathban().revive(toReviveUUID);
        sender.sendMessage(ChatColor.GREEN + "You have revived " + ChatColor.RED + UUIDUtils.name(toReviveUUID) + ChatColor.GREEN + ".");
    }

    @Command(names = {"team lives", "t lives", "f lives", "fac lives", "faction lives"}, permission = "")
    public static void getLives(final Player sender) {
        if (Main.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(CC.translate("&cThis is a HCF only command."));
            return;
        }
        final Team team = Main.getInstance().getTeamHandler().getTeam(sender);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You need a team to use this command.");
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "Your team has " + ChatColor.RED + team.getLives() + ChatColor.YELLOW + " lives.");
        sender.sendMessage(ChatColor.YELLOW + "To deposit lives, use /t lives add <amount>");
        sender.sendMessage(ChatColor.YELLOW + "Life deposits are FINAL!");
        sender.sendMessage(ChatColor.YELLOW + "Leaders can revive members using " + ChatColor.WHITE + "/t revive <name>");
    }
}
