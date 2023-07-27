package cc.stormworth.hcf.misc.kits.command;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.chat.Clickable;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.kits.Kit;
import cc.stormworth.hcf.misc.kits.KitManager;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class KkitsCommand {

    @Command(names = {"adminkits list"}, permission = "op", hidden = true, requiresPlayer = true)
    public static void list(final Player player) {
        player.sendMessage(CC.translate("&7&m----------------------------------"));
        for (Kit kit : Main.getInstance().getMapHandler().getKitManager().getKits()) {
            Clickable clickable = new Clickable("&a&l" + kit.getName(), "&aClick to receive &lx1 " + kit.getName(), "/adminkits get " + kit.getName());
            clickable.sendToPlayer(player);
        }
        player.sendMessage(CC.translate("&7&m----------------------------------"));
    }

    @Command(names = {"adminkits get"}, permission = "")
    public static void load(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        if (!sender.isOp() && sender.getGameMode() != GameMode.CREATIVE && !DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            return;
        }

        if (!sender.isOp() && sender.getGameMode() != GameMode.CREATIVE && SpawnTagHandler.isTagged(sender)) return;

        if (sender.isOp() && sender.getGameMode() == GameMode.CREATIVE && !sender.hasMetadata("invisible")) {
            kit.apply(sender);
            return;
        }
        KitManager.attemptApplyKit(sender, kit);
    }

    @Command(names = {"adminkits create"}, permission = "op")
    public static void create(Player sender, @Param(name = "name", wildcard = true) String name) {
        if (Main.getInstance().getMapHandler().getKitManager().get(name) != null) {
            sender.sendMessage(ChatColor.RED + "That kit already exists.");
            return;
        }

        Kit kit = Main.getInstance().getMapHandler().getKitManager().getOrCreate(name);
        kit.setIcon(sender.getItemInHand());
        kit.update(sender.getInventory());
        Main.getInstance().getMapHandler().getKitManager().save();

        sender.sendMessage(ChatColor.YELLOW + "The " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + " kit has been created from your inventory.");
    }

    @Command(names = {"adminkits delete"}, permission = "op")
    public static void delete(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        Main.getInstance().getMapHandler().getKitManager().delete(kit);

        sender.sendMessage(
                ChatColor.YELLOW + "Kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + " has been deleted.");
    }

    @Command(names = {"adminkits edit"}, permission = "op")
    public static void edit(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        kit.update(sender.getInventory());
        kit.setIcon(sender.getItemInHand());
        Main.getInstance().getMapHandler().getKitManager().save();

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + " has been edited and saved.");
    }

    @Command(names = {"adminkits seticon"}, permission = "op")
    public static void seticon(Player sender, @Param(name = "kit", wildcard = true) Kit kit) {
        kit.setIcon(sender.getItemInHand());
        Main.getInstance().getMapHandler().getKitManager().save();

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GOLD + kit.getName() + ChatColor.YELLOW + " icon has been edited and saved.");
    }
}