package cc.stormworth.hcf.misc.gkits;

import cc.stormworth.core.menu.Button;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.util.item.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.gkits.event.KitApplyEvent;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.PlayerKit;
import cc.stormworth.hcf.util.Utils;
import cc.stormworth.hcf.util.player.Players;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Kit implements ConfigurationSerializable {

  private static final ItemStack DEFAULT_IMAGE;

  static {
    DEFAULT_IMAGE = new ItemStack(Material.ITEM_FRAME, 1);
  }

  protected final UUID uniqueID;
  protected String name;
  protected String description;
  protected ItemStack[] items;
  protected ItemStack[] armour;
  protected ItemStack image;
  protected boolean enabled;
  protected long delayMillis;
  protected int slot;
  protected int maxUses;
  protected boolean inmenu;
  protected long minPlaytimeMillis;
  protected String delayWords;
  private final ItemStack BLANK;
  @Setter
  @Getter
  private KitType type = KitType.FREE;

  public Kit(final String name, final String description, final PlayerInventory inventory) {
    this(name, description, inventory, 0L, 1, true);
  }

  public Kit(final String name, final String description, final Inventory inventory,
      final long milliseconds, final int slot, final boolean inmenu) {
    this.BLANK = ItemBuilder.of(Material.STAINED_GLASS_PANE).data(DyeColor.ORANGE.getData())
        .name(" ").build();
    this.enabled = true;
    this.uniqueID = UUID.randomUUID();
    this.name = name;
    this.description = description;
    this.setItems(inventory.getContents());
    if (inventory instanceof PlayerInventory) {
      final PlayerInventory playerInventory = (PlayerInventory) inventory;
      this.setArmour(playerInventory.getArmorContents());
      this.setImage(playerInventory.getItemInHand());
    }
    this.delayMillis = milliseconds;
    this.minPlaytimeMillis = 0;
    this.slot = slot;
    this.maxUses = 0;
    this.inmenu = inmenu;
  }

  public Kit(final Map<String, Object> map) {
    this.BLANK = ItemBuilder.of(Material.STAINED_GLASS_PANE).data(DyeColor.ORANGE.getData())
        .name(" ").build();
    this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
    this.setName((String) map.get("name"));
    this.setDescription((String) map.get("description"));
    this.setEnabled((boolean) map.get("enabled"));
    final List<ItemStack> items = Utils.createList(map.get("items"), ItemStack.class);
    this.setItems(items.toArray(new ItemStack[items.size()]));
    final List<ItemStack> armour = Utils.createList(map.get("armour"), ItemStack.class);
    this.setArmour(armour.toArray(new ItemStack[armour.size()]));
    this.setImage((ItemStack) map.get("image"));
    this.setDelayMillis(Long.parseLong((String) map.get("delay")));
    this.setMinPlaytimeMillis(Long.parseLong((String) map.get("minplaytime")));
    this.setSlot((int) map.get("slot"));
    this.setMaxUses((int) map.get("maxUses"));
    this.setInMenu((boolean) map.get("inmenu"));
    setType(KitType.valueOf((String) map.get("type")));
  }

  public Map<String, Object> serialize() {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put("uniqueID", this.uniqueID.toString());
    map.put("name", this.name);
    map.put("description", this.description);
    map.put("enabled", this.enabled);
    map.put("items", this.items);
    map.put("armour", this.armour);
    map.put("image", this.image);
    map.put("delay", Long.toString(this.delayMillis));
    map.put("minplaytime", Long.toString(this.minPlaytimeMillis));
    map.put("slot", this.slot);
    map.put("maxUses", maxUses);
    map.put("inmenu", this.inmenu);
    map.put("type", this.type.name());
    return map;
  }

  public UUID getUniqueID() {
    return this.uniqueID;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public ItemStack[] getItems() {
    return Arrays.copyOf(this.items, this.items.length);
  }

  public void setItems(final ItemStack[] items) {
    final int length = items.length;
    this.items = new ItemStack[length];
    for (int i = 0; i < length; ++i) {
      final ItemStack next = items[i];
      this.items[i] = ((next == null) ? null : next.clone());
    }
  }

  public ItemStack[] getArmour() {
    return Arrays.copyOf(this.armour, this.armour.length);
  }

  public void setArmour(final ItemStack[] armour) {
    final int length = armour.length;
    this.armour = new ItemStack[length];
    for (int i = 0; i < length; ++i) {
      final ItemStack next = armour[i];
      this.armour[i] = ((next == null) ? null : next.clone());
    }
  }

  public ItemStack getImage() {
    if (this.image == null || this.image.getType() == Material.AIR) {
      this.image = Kit.DEFAULT_IMAGE;
    }
    return this.image;
  }

  public void setImage(final ItemStack image) {
    this.image = ((image == null || image.getType() == Material.AIR) ? null : image.clone());
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isInMenu() {
    return this.inmenu;
  }

  public void setInMenu(final boolean b) {
    this.inmenu = b;
  }

  public long getSlot() {
    return this.slot;
  }

  public void setSlot(final int slot) {
    if (this.slot != slot) {
      Preconditions.checkArgument(slot >= 1 && slot <= 54, "Slot cannot be negative");
      this.slot = slot;
    }
  }

  public long getMaxUses() {
    return this.maxUses;
  }

  public void setMaxUses(final int maxUses) {
    this.maxUses = maxUses;
  }

  public long getDelayMillis() {
    return this.delayMillis;
  }

  public void setDelayMillis(final long delayMillis) {
    if (this.delayMillis != delayMillis) {
      Preconditions.checkArgument(this.minPlaytimeMillis >= 0L,
          "Minimum delay millis cannot be negative");
      this.delayMillis = delayMillis;
      this.delayWords = DurationFormatUtils.formatDurationWords(delayMillis, true, true);
    }
  }

  public String getDelayWords() {
    return DurationFormatUtils.formatDurationWords(this.delayMillis, true, true);
  }

  public long getMinPlaytimeMillis() {
    return this.minPlaytimeMillis;
  }

  public void setMinPlaytimeMillis(final long minPlaytimeMillis) {
    if (this.minPlaytimeMillis != minPlaytimeMillis) {
      Preconditions.checkArgument(minPlaytimeMillis >= 0L,
          "Minimum playtime millis cannot be negative");
      this.minPlaytimeMillis = minPlaytimeMillis;
    }
  }

  public boolean canUse(Player player) {

    if (minPlaytimeMillis != 0) {
      int minPlaytime = (int) TimeUnit.MILLISECONDS.toSeconds(getMinPlaytimeMillis());

      HCFProfile profile = HCFProfile.getByUUID(player.getUniqueId());

      if(profile == null){
        return false;
      }

      long playtimeTime = TimeUnit.MILLISECONDS.toSeconds(profile.getTotalPlayTime());

      if (playtimeTime >= minPlaytime) {
        return true;
      }
    }

    if (getType() == KitType.FREE) {
      return true;
    }

    if (getName().equalsIgnoreCase("sotw")) {
      return true;
    }

    if (getName().equalsIgnoreCase("deathban")) {
      return true;
    }

    return player.hasPermission("crazyenchantments.gkitz." + getName().toLowerCase());
  }

  public boolean applyTo(final Player player, final boolean force, final boolean inform) {
    final KitApplyEvent event = new KitApplyEvent(this, player, force);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return false;
    }
    if (!event.getKit().isInMenu()) {
      player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
      player.getOpenInventory().getTopInventory().clear();
      player.getInventory().clear();
      player.getInventory().setHelmet(null);
      player.getInventory().setChestplate(null);
      player.getInventory().setLeggings(null);
      player.getInventory().setBoots(null);
    }
    ItemStack cursor = player.getItemOnCursor();
    Location location = player.getLocation();
    World world = player.getWorld();

    if (cursor != null && cursor.getType() != Material.AIR) {
      player.setItemOnCursor(new ItemStack(Material.AIR, 1));
      TaskUtil.run(Main.getInstance(), () -> world.dropItemNaturally(location, cursor));
    }

    PlayerInventory inventory = player.getInventory();

    HCFProfile profile = HCFProfile.get(player);

    if (this.armour != null) {
      for (int i = FastMath.min(3, this.armour.length); i >= 0; --i) {
        ItemStack stack = this.armour[i];

        if (stack != null && stack.getType() != Material.AIR) {
          int armourSlot = i + 36;
          ItemStack previous = inventory.getItem(armourSlot);
          stack = stack.clone();

          if (previous != null && previous.getType() != Material.AIR) {
            previous.setType(Material.AIR);
            ItemStack finalStack = stack;
            TaskUtil.run(Main.getInstance(), () -> world.dropItemNaturally(location, finalStack));
          } else {
            inventory.setItem(armourSlot, stack);
          }
        }
      }
    }

    if (profile.getKit(this) != null){

      PlayerKit playerKit = profile.getKit(this);

      for (ItemStack item2 : playerKit.getContents()) {
        if (item2 != null && item2.getType() != Material.AIR) {
          item2 = item2.clone();
          for (Map.Entry<Integer, ItemStack> excess : inventory.addItem(new ItemStack[]{item2.clone()}).entrySet()) {
            TaskUtil.run(Main.getInstance(), () -> world.dropItemNaturally(location, excess.getValue()));
          }
        }
      }
    }else{
      for (ItemStack item2 : this.items) {
        if (item2 != null && item2.getType() != Material.AIR) {

          item2 = item2.clone();

          for (Map.Entry<Integer, ItemStack> excess : inventory.addItem(new ItemStack[]{item2.clone()}).entrySet()) {
            TaskUtil.run(Main.getInstance(), () -> world.dropItemNaturally(location, excess.getValue()));
          }
        }
      }
    }

    if (inform) {
      player.sendMessage(CC.translate(""));
      player.sendMessage(CC.translate("&eSuccessfully equipped &6" + this.name + " &ekit!"));
      player.sendMessage(CC.translate(""));
    }
    return true;
  }

  public void applyFromNPC(Player player) {

    if (!player.hasMetadata("confirm") && !Players.isNaked(player)) {
      Button.playFail(player);
      player.sendMessage(
          CC.translate(
              "&cYou inventory isn't empty! &eThis is your last warn. &6&lClick again &eto confirm!"));
      player.setMetadata("confirm", new FixedMetadataValue(Main.getInstance(), true));
      return;
    }

    player.removeMetadata("confirm", Main.getInstance());

    final KitApplyEvent event = new KitApplyEvent(this, player, false);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }

    player.getActivePotionEffects().stream().map(PotionEffect::getType)
        .forEach(player::removePotionEffect);
    player.getOpenInventory().getTopInventory().clear();
    player.getInventory().clear();
    player.getInventory().setHelmet(null);
    player.getInventory().setChestplate(null);
    player.getInventory().setLeggings(null);
    player.getInventory().setBoots(null);

    HCFProfile profile = HCFProfile.get(player);
    PlayerInventory inventory = player.getInventory();

    if (this.armour != null) {
      player.getInventory().setArmorContents(this.armour);
    }

    if (profile.getKit(this) != null){

      PlayerKit playerKit = profile.getKit(this);

      for (ItemStack item2 : playerKit.getContents()) {
        if (item2 != null && item2.getType() != Material.AIR) {
          inventory.addItem(item2.clone());
        }
      }
    }else{
      for (ItemStack item2 : this.items) {
        if (item2 != null && item2.getType() != Material.AIR) {
          inventory.addItem(item2.clone());
        }
      }
    }

    player.updateInventory();
  }

}