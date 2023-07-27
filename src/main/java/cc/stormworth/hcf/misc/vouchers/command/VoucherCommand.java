package cc.stormworth.hcf.misc.vouchers.command;

import cc.stormworth.core.cmds.rank.RankCommand;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.rank.Rank;
import cc.stormworth.core.rank.grant.Grant;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.onedoteight.TitleBuilder;
import cc.stormworth.core.util.time.Duration;
import cc.stormworth.hcf.misc.vouchers.Voucher;
import cc.stormworth.hcf.profile.HCFProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VoucherCommand {

    @Command(names = {"voucher list", "vouchers list"}, permission = "op", requiresPlayer = true)
    public static void list(Player sender) {
        sender.sendMessage(CC.translate("&7&m----------------------------------"));
        for (Voucher voucher : Voucher.getVouchers().values()) {
            Clickable clickable = new Clickable(CC.LIGHT_PURPLE + voucher.getName(), "&aClick to receive &lx1 " + voucher.getName(), "/voucher give " + voucher.getName() + " " + sender.getName() + " 1");
            clickable.sendToPlayer(sender);
        }
        sender.sendMessage(CC.translate("&7&m----------------------------------"));
    }

    @Command(names = {"voucher give", "vouchers give"}, permission = "op")
    public static void give(CommandSender sender, @Param(name = "voucher") String vouchername, @Param(name = "target") Player target, @Param(name = "amount") int amount) {
        Voucher voucher = Voucher.getByName(vouchername);
        if (voucher == null) {
            sender.sendMessage(ChatColor.RED + "There is not a voucher named " + vouchername.toLowerCase() + ".");
            return;
        }
        if (sender instanceof Player)
            sender.sendMessage(CC.YELLOW + "You have given " + target.getName() + " x" + amount + " " + voucher.getName() + CC.YELLOW + " voucher.");
        target.sendMessage(CC.YELLOW + "You have received x" + amount + " " + voucher.getName() + CC.YELLOW + " voucher.");
        target.getInventory().addItem(Voucher.getVoucher(voucher, amount));
    }

    @Command(names = {"voucher giveall", "vouchers giveall"}, permission = "op")
    public static void give(CommandSender sender, @Param(name = "voucher") String vouchername, @Param(name = "amount") int amount) {
        Voucher voucher = Voucher.getByName(vouchername);
        if (voucher == null) {
            sender.sendMessage(ChatColor.RED + "There is not a voucher named " + vouchername.toLowerCase() + ".");
            return;
        }
        for (Player target : Bukkit.getOnlinePlayers()){
            target.sendMessage(CC.YELLOW + "You have received x" + amount + " " + voucher.getName() + CC.YELLOW + " voucher.");
            target.getInventory().addItem(Voucher.getVoucher(voucher, amount));

            TitleBuilder titleBuilder = new TitleBuilder("&b&lVoucher All &areceived.", "&eAmount: &f" + amount, 10, 20, 10);
            titleBuilder.send(target);
        }

        sender.sendMessage(CC.YELLOW + "You have given all players x" + amount + " " + voucher.getName() + CC.YELLOW + " voucher.");
    }

    @Command(names = {"voucher giverank", "vouchers giverank"}, permission = "op", async = true)
    public static void execute(final CommandSender sender, @Param(name = "target") UUID uuid, @Param(name = "rank") Rank rank, @Param(name = "reason") String reason, @Param(name = "duration") String time) {
        Duration duration = Duration.fromString(time);
        boolean bypassRank = false;
        Rank actualRank = Profile.getByUuidIfAvailable(uuid).getRank();

        if (rank.isAbove(actualRank)) bypassRank = true;

        if (duration.getValue() == -1) {
            sender.sendMessage(CC.RED + "That duration is not valid.");
            sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
            return;
        }

        UUID addedBy = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        Grant grant = new Grant(uuid, rank, addedBy, System.currentTimeMillis(), reason, duration.getValue());
        RankCommand.setRank(sender, grant);

        if (bypassRank) HCFProfile.getByUUID(uuid).setReclaimed(false);
    }
}