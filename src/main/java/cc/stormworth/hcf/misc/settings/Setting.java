package cc.stormworth.hcf.misc.settings;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.hcf.misc.settings.menu.button.SettingButton;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.Lang;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;

@AllArgsConstructor
public enum Setting {

  PUBLIC_CHAT(
      ChatColor.GOLD + "Public Chat",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "public chat messages?"
      ),
      Material.SIGN,
      ChatColor.YELLOW + "Show public chat",
      ChatColor.YELLOW + "Hide public chat",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setGlobalChat(!hcfProfile.isGlobalChat());
      player.sendMessage(
          ChatColor.YELLOW + "You are now " + (hcfProfile.isGlobalChat() ? ChatColor.GREEN + "able"
              : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see global chat messages.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isGlobalChat();
    }
  },
  TEAM_NAMETAGS(
      ChatColor.GOLD + "Team Tags",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "team tags?"
      ),
      Material.NAME_TAG,
      ChatColor.YELLOW + "Show tags",
      ChatColor.YELLOW + "Hide tags",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setTeamNameTags(!hcfProfile.isTeamNameTags());
      CorePlugin.getInstance().getNametagEngine().reloadOthersFor(player);
      player.sendMessage(
          ChatColor.YELLOW + "You are now " + (hcfProfile.isTeamNameTags() ? ChatColor.GREEN
              + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see tags.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isTeamNameTags();
    }
  },
  DEATH_MESSAGES(
      ChatColor.GOLD + "Death Messages",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "death messages?"
      ),
      Material.SKULL_ITEM,
      ChatColor.YELLOW + "Show messages",
      ChatColor.YELLOW + "Hide messages",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setDeathMessages(!hcfProfile.isDeathMessages());
      player.sendMessage(
          ChatColor.YELLOW + "You are now " + (hcfProfile.isDeathMessages() ? ChatColor.GREEN
              + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW + " to see death messages.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isDeathMessages();
    }
  },
  ENDERPEARL_COOLDOWN_CHAT(
      ChatColor.GOLD + "EnderPearl Cooldown",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "Ender pearl cooldown in chat?"
      ),
      Material.ENDER_PEARL,
      ChatColor.YELLOW + "Show ender pearl cooldown",
      ChatColor.YELLOW + "Hide ender pearl cooldown",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setEnderpearlCooldown(!hcfProfile.isEnderpearlCooldown());
      player.sendMessage(ChatColor.YELLOW + "You are now " + (hcfProfile.isEnderpearlCooldown() ?
          ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW
          + " to see ender pearl cooldown in chat.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isEnderpearlCooldown();
    }
  },
  PARTICLES_BATTLE(
      ChatColor.GOLD + "Particles of Battle rank",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "Particles of Battle rank?"
      ),
      Material.NETHER_STAR,
      ChatColor.YELLOW + "Show particles Battle rank",
      ChatColor.YELLOW + "Hide particles Battle rank",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setParticlesBattle(!hcfProfile.isParticlesBattle());
      player.sendMessage(ChatColor.YELLOW + "You are now " + (hcfProfile.isParticlesBattle() ?
          ChatColor.GREEN + "able" : ChatColor.RED + "unable") + ChatColor.YELLOW
          + " to see particles of Battle rank in chat.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isParticlesBattle();
    }
  },
  LANG(
      ChatColor.GOLD + "Language",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to see",
          ChatColor.YELLOW + "Tips in your language?"
      ),
      Material.ANVIL,
      ChatColor.YELLOW + "English",
      ChatColor.YELLOW + "Spanish",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      hcfProfile.setLang(hcfProfile.getLang() == Lang.ENGLISH ? Lang.SPANISH : Lang.ENGLISH);
      player.sendMessage(ChatColor.YELLOW + "You are now " + (hcfProfile.getLang() == Lang.ENGLISH ?
          ChatColor.GOLD + "English" : ChatColor.GOLD + "Spanish") + ChatColor.YELLOW
          + " language.");
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.getLang() == Lang.ENGLISH || hcfProfile.getLang() == Lang.UNDEFINED;
    }
  },
  PAY(
      ChatColor.GOLD + "Payments",
      ImmutableList.of(
          ChatColor.YELLOW + "Do you want to receive",
          ChatColor.YELLOW + "payments?"
      ),
      Material.PAPER,
      ChatColor.GREEN + "Yes",
      ChatColor.RED + "No",
      true
  ) {
    @Override
    public void toggle(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      final boolean val = !hcfProfile.isPaymentsToggled();
      player.sendMessage(ChatColor.YELLOW + "You are now " + (val ? (ChatColor.GREEN + "able")
          : (ChatColor.RED + "unable")) + ChatColor.YELLOW + " to receive payments!");
      hcfProfile.setPaymentsToggled(val);
    }

    @Override
    public boolean isEnabled(Player player) {
      HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());
      return hcfProfile.isPaymentsToggled();
    }
  };

  @Getter
  private String name;
  @Getter
  private Collection<String> description;
  @Getter
  private Material icon;
  @Getter
  private String enabledText;
  @Getter
  private String disabledText;
  private boolean defaultValue;

  // Using @Getter means the method would be 'isDefaultValue',
  // which doesn't correctly represent this variable.
  public boolean getDefaultValue() {
    return (defaultValue);
  }

  public SettingButton toButton() {
    return new SettingButton(this);
  }

  public abstract void toggle(Player player);

  public abstract boolean isEnabled(Player player);
}