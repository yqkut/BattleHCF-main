package cc.stormworth.hcf.misc.gkits.commands;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.JavaUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.misc.gkits.Kit;
import cc.stormworth.hcf.misc.gkits.KitType;
import cc.stormworth.hcf.misc.gkits.data.FlatFileKitManager;
import cc.stormworth.hcf.misc.gkits.event.KitCreateEvent;
import cc.stormworth.hcf.misc.gkits.event.KitRemoveEvent;
import cc.stormworth.hcf.misc.gkits.event.KitRenameEvent;
import cc.stormworth.hcf.misc.gkits.menu.SelectKitTypeMenu;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.misc.InventoryUtil;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class GKitCommand {

  @Command(names = {"managekit create"}, permission = "op")
  public static void create(final CommandSender sender, @Param(name = "kit") final String kitname) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Only players may create kits.");
      return;
    }
    if (!StringUtils.isAlphanumeric(kitname)) {
      sender.sendMessage(ChatColor.GRAY + "Kit names may only be alphanumeric.");
      return;
    }
    Kit kit = FlatFileKitManager.getKit(kitname);
    if (kit != null) {
      sender.sendMessage(ChatColor.RED + "There is already a kit named " + kitname + '.');
      return;
    }
    final Player player = (Player) sender;
    kit = new Kit(kitname, null, player.getInventory());
    final KitCreateEvent event = new KitCreateEvent(kit);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    Main.getInstance().getKitManager().createKit(kit);
    sender.sendMessage(ChatColor.GRAY + "Created kit '" + kit.getDisplayName() + "'.");
  }

  @Command(names = {"managekit delete"}, permission = "op")
  public static void delete(final CommandSender sender, @Param(name = "kit") final Kit kit) {
    final KitRemoveEvent event = new KitRemoveEvent(kit);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    sender.sendMessage(ChatColor.GRAY + "Removed kit '" + kit.getName() + "'.");
    Main.getInstance().getKitManager().removeKit(kit);
  }

  @Command(names = {"managekit resetcooldowns"}, permission = "op", async = true)
  public static void resetcooldowns(final CommandSender sender) {
    HCFProfile.resetAllGKits();
    sender.sendMessage(ChatColor.GRAY + "Reset cooldowns of every kit.");
  }

  @Command(names = {"managekit apply"}, permission = "op", async = true)
  public static void apply(final CommandSender sender, @Param(name = "kit") final Kit kit,
      @Param(name = "player") final Player target) {
    if (EOTWCommand.isFfaEnabled()) {
      sender.sendMessage(CC.RED + "You cannot apply kits during ffa.");
      return;
    }
    if (kit.applyTo(target, true, true)) {
      sender.sendMessage(
          ChatColor.GREEN + "Applied kit '" + kit.getDisplayName() + "' to '" + target.getName()
              + "'.");
      return;
    }
    sender.sendMessage(
        ChatColor.RED + "Failed to apply kit " + kit.getDisplayName() + " to " + target.getName()
            + '.');
  }

  @Command(names = {"_managekit applynpc"}, permission = "", async = true)
  public static void applynpc(final Player sender, @Param(name = "kit") final Kit kit) {

    if (EOTWCommand.isFfaEnabled()) {
      sender.sendMessage(CC.RED + "You cannot apply kits during ffa.");
      return;
    }

    if (DTRBitmask.CONQUEST.appliesAt(sender.getLocation()) || DTRBitmask.KOTH.appliesAt(
        sender.getLocation())) {
      sender.sendMessage(CC.RED + "You cannot use this inside events.");
      return;
    }

    if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
      sender.sendMessage(CC.translate("&cYou can only use this in the Safe Zone."));
      return;
    }

    Team teamat = LandBoard.getInstance().getTeam(sender.getLocation());
    if (!Main.getInstance().getMapHandler().isKitMap()
        && sender.getGameMode() != GameMode.CREATIVE
        && !sender.getWorld().getName().equalsIgnoreCase("world")
        && !sender.getWorld().getName().equalsIgnoreCase("void")) {
      if (teamat != null && teamat.getOwner() != null) {
        return;
      }
      sender.sendMessage(ChatColor.RED + "The kits are only available in overworld.");
      return;
    }

    if (Utils.isEventLocated(sender, true)) {
      sender.sendMessage(
          CC.RED + "You cannot use the enderchest command while your team is in the event.");
      return;
    }

    if (!kit.isEnabled()) {
      sender.sendMessage(CC.RED + "There is not a kit named " + kit.getName() + ".");
      return;
    }

    if (kit.canUse(sender)) {
      kit.applyFromNPC(sender);
    } else {
      sender.sendMessage(CC.translate("&cYou do not have permission to use this kit."));
    }
  }

  @Command(names = {"managekit disable"}, permission = "op")
  public static void disable(final Player sender, @Param(name = "kit") final Kit kit) {
    final boolean newEnabled = !kit.isEnabled();
    kit.setEnabled(newEnabled);
    sender.sendMessage(
        ChatColor.AQUA + "Kit " + kit.getDisplayName() + " has been " + (newEnabled ? (
            ChatColor.GREEN + "enabled") : (ChatColor.RED + "disabled")) + ChatColor.AQUA + '.');
  }

  @Command(names = {"managekit inmenu"}, permission = "op")
  public static void inmenu(final CommandSender sender, @Param(name = "kit") final Kit kit) {
    final boolean newEnabled = !kit.isInMenu();
    kit.setInMenu(newEnabled);
    sender.sendMessage(
        ChatColor.AQUA + "Set appear in menu of kit " + kit.getDisplayName() + " has been " + (
            newEnabled ? (ChatColor.GREEN + "enabled") : (ChatColor.RED + "disabled"))
            + ChatColor.AQUA + '.');
  }

  @Command(names = {"managekit rename"}, permission = "op")
  public static void rename(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "newname") final String newname) {
    final KitRenameEvent event = new KitRenameEvent(kit, kit.getName(), newname);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    if (event.getOldName().equals(event.getNewName())) {
      sender.sendMessage(ChatColor.RED + "This kit is already called " + event.getNewName() + '.');
      return;
    }
    kit.setName(event.getNewName());
    sender.sendMessage(
        ChatColor.AQUA + "Renamed kit " + event.getOldName() + " to " + event.getNewName() + '.');
  }

  @Command(names = {"managekit reset"}, permission = "op", async = true)
  public static void reset(final Player sender, @Param(name = "kitname|all") final String kitname,
      @Param(name = "player") final Player target) {
    HCFProfile profile = HCFProfile.getByUUID(target.getUniqueId());
    if (kitname.equalsIgnoreCase("all")) {
      for (final Kit kit : Main.getInstance().getKitManager().getKits()) {
        profile.ResetKitCooldown(kit);
      }
      sender.sendMessage("§eYou have reset the §6every kit §ecooldown to §6" + target.getName());
      return;
    }
    final Kit kit2 = FlatFileKitManager.getKit(kitname);
    if (kit2 == null) {
      sender.sendMessage(ChatColor.RED + "There is not a kit named " + kitname + '.');
      return;
    }
    if (target == null || (sender instanceof Player && !sender.canSee(target))) {
      sender.sendMessage(
          ChatColor.RED + "Player '" + ChatColor.GRAY + target + ChatColor.RED + "' not found.");
      return;
    }
    if (profile.getRemainingKitCooldown(kit2) == 0L) {
      sender.sendMessage(CC.translate("&cThis player doesn't have cooldown!"));
      return;
    }
    profile.ResetKitCooldown(kit2);
    sender.sendMessage(
        "§eYou have reset the kit §6" + kit2.getName() + " §ecooldown to §6" + target.getName());
  }

  @Command(names = {"managekit setdelay"}, permission = "op")
  public static void setdelay(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "duration") final String duration) {
    final long finalduration = JavaUtils.parse(duration);
    if (finalduration == -1L) {
      sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
      return;
    }
    kit.setDelayMillis(finalduration);
    sender.sendMessage(ChatColor.RED + "Set delay of kit " + kit.getName() + " to "
        + DurationFormatUtils.formatDurationWords(finalduration, true, true) + '.');
  }

  @Command(names = {"managekit setplaytime"}, permission = "op")
  public static void setplaytime(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "duration") final String duration) {
    final long finalduration = JavaUtils.parse(duration);
    if (finalduration == -1L) {
      sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
      return;
    }
    kit.setMinPlaytimeMillis(finalduration);
    sender.sendMessage(ChatColor.RED + "Set delay of kit " + kit.getName() + " to "
        + DurationFormatUtils.formatDurationWords(finalduration, true, true) + '.');
  }

  @Command(names = {"managekit setmaxuses"}, permission = "op")
  public static void axuses(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "amount") int amount) {
    if (amount < 0) {
      kit.setMaxUses(0);
    } else {
      kit.setMaxUses(amount);
    }
    sender.sendMessage(
        ChatColor.RED + "You have set the max uses for " + kit.getName() + " kit to " + amount
            + ".");
  }

  @Command(names = {"managekit setdescription"}, permission = "op")
  public static void setdescription(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "description") final String description) {
    if (description.equalsIgnoreCase("none") || description.equalsIgnoreCase("null")) {
      kit.setDescription(null);
      sender.sendMessage(
          ChatColor.RED + "Removed description of kit " + kit.getDisplayName() + '.');
      return;
    }
    kit.setDescription(description);
    sender.sendMessage(
        ChatColor.RED + "Set description of kit " + kit.getDisplayName() + " to " + description
            + '.');
  }

  @Command(names = {"managekit setimage"}, permission = "op")
  public static void setimage(final Player sender, @Param(name = "kit") final Kit kit) {
    final ItemStack stack = sender.getItemInHand();
    if (stack == null || stack.getType() == Material.AIR) {
      sender.sendMessage(ChatColor.RED + "You are not holding anything.");
      return;
    }
    kit.setImage(stack.clone());
    sender.sendMessage(
        ChatColor.AQUA + "Set image of kit " + ChatColor.RED + kit.getDisplayName() + ChatColor.AQUA
            + " to " + ChatColor.RED + InventoryUtil.getItemName(stack)
            + ChatColor.AQUA + '.');
  }

  @Command(names = {"managekit setslot"}, permission = "op")
  public static void setslot(final Player sender, @Param(name = "kit") final Kit kit,
      @Param(name = "1-54") final String slot,
      @Param(name = "type") String type) {
    final Integer finalslot = JavaUtils.tryParseInt(slot);
    if (finalslot == null || finalslot < 1 || finalslot > 54) {
      sender.sendMessage(CC.translate("&cInvalid Slot Number, must be 1-54."));
      return;
    }

    KitType kitType = KitType.getByType(type);

    if (kitType == null) {
      sender.sendMessage(CC.translate("&cInvalid Kit Type, must be Free or Premium."));
      return;
    }

    kit.setType(kitType);
    kit.setSlot(finalslot);
    sender.sendMessage(
        ChatColor.RED + "Set slot of kit " + kit.getName() + " to " + finalslot + '.');
  }

  @Command(names = {"managekit setitems"}, permission = "op")
  public static void setitems(final Player sender, @Param(name = "kit") final Kit kit) {
    if (kit == null) {
      sender.sendMessage(ChatColor.RED + "Kit '" + kit.getName() + "' not found.");
      return;
    }
    final PlayerInventory inventory = sender.getInventory();
    kit.setItems(inventory.getContents());
    kit.setArmour(inventory.getArmorContents());
    sender.sendMessage(ChatColor.AQUA + "Set the items of kit " + kit.getDisplayName()
        + " as your current inventory.");
  }

  @Command(names = {"managekit toggle"}, permission = "op")
  public static void toggle(final Player sender, @Param(name = "kit") final Kit kit) {
    if (kit == null) {
      sender.sendMessage(ChatColor.RED + "Kit '" + kit.getName() + "' not found.");
      return;
    }
    kit.setEnabled(!kit.isEnabled());
    sender.sendMessage(ChatColor.AQUA + "You have " + (kit.isEnabled() ? CC.GREEN + "Enabled"
        : CC.RED + "Disabled") + CC.GOLD + " the kit called " + kit.getDisplayName() + CC.GOLD
        + ".");
  }

  @Command(names = {"managekit list"}, permission = "op")
  public static void list(final Player sender) {
    final List<Kit> kits = Main.getInstance().getKitManager().getKits();
    if (kits.isEmpty()) {
      sender.sendMessage(ChatColor.RED + "No kits have been defined.");
      return;
    }
    final List<String> kitNames = new ArrayList<>();
    for (final Kit kit : kits) {
      final ChatColor color = ChatColor.GREEN;
      kitNames.add(color + kit.getDisplayName());
    }
    final String kitList = StringUtils.join(kitNames, ChatColor.GRAY + ", ");
    sender.sendMessage(CC.translate("&c&m--------------------------------"));
    sender.sendMessage(
        ChatColor.AQUA + ChatColor.BOLD.toString() + "Kit List " + ChatColor.GREEN + "["
            + kitNames.size() + '/' + kits.size() + "]");
    sender.sendMessage(CC.translate("&c&m--------------------------------"));
    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + kitList + ChatColor.GRAY + ']');
    sender.sendMessage(CC.translate("&c&m--------------------------------"));
  }

  @Command(names = {"gkit", "kit", "kits", "gkits", "gkitz"}, permission = "", requiresPlayer = true)
  public static void gui(Player sender, @Param(name = "kit", defaultValue = "menu") String kitname) {

    if (EOTWCommand.isFfaEnabled()) {
      sender.sendMessage(CC.RED + "You cannot apply kits during ffa.");
      return;
    }

    if (DTRBitmask.CONQUEST.appliesAt(sender.getLocation()) || DTRBitmask.KOTH.appliesAt(sender.getLocation())) {
      sender.sendMessage(CC.RED + "You cannot use this inside events.");
      return;
    }

    Team teamAt = LandBoard.getInstance().getTeam(sender.getLocation());

    if (!Main.getInstance().getMapHandler().isKitMap()
        && sender.getGameMode() != GameMode.CREATIVE
        && !sender.getWorld().getName().equalsIgnoreCase("world")
        && !sender.getWorld().getName().equalsIgnoreCase("void")) {

      if (teamAt != null && teamAt.getOwner() != null) {
        return;
      }

      sender.sendMessage(ChatColor.RED + "The kits are only available in overworld.");
      return;
    }

    if (Utils.isEventLocated(sender, true)) {
      sender.sendMessage(CC.RED + "You cannot use the enderchest command while your team is in the event.");
      return;
    }

    if (kitname.equalsIgnoreCase("menu")) {
      new SelectKitTypeMenu().openMenu(sender);
      return;
    }

    if(!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())){
        sender.sendMessage(CC.RED + "You cannot take these shortcuts if you are not in the safe zone.");
        return;
    }

    Kit kit = FlatFileKitManager.getKit(kitname);

    if (kitname.equalsIgnoreCase("deathban")) {

      HCFProfile profile = HCFProfile.get(sender);

      if (!profile.isDeathBanned()) {
        sender.sendMessage(CC.RED + "There is not a kit named " + kitname + ".");
        return;
      }
      kit.applyTo(sender, false, true);
      return;
    }
    if (kit == null || !kit.isEnabled()) {
      sender.sendMessage(CC.RED + "There is not a kit named " + kitname + ".");
      return;
    }
    if (kit.canUse(sender)) {
      kit.applyTo(sender, false, true);
    } else {
      sender.sendMessage(CC.translate("&cYou do not have permission to use this kit."));
    }
  }
}