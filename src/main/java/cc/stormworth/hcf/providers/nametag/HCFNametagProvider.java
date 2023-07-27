package cc.stormworth.hcf.providers.nametag;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.nametag.NametagInfo;
import cc.stormworth.core.kt.nametag.NametagProvider;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.bounty.BountyPlayer;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.pvpclasses.pvpclasses.ArcherClass;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.commands.team.TeamTopCommand;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;
import java.util.List;

@Getter
public class HCFNametagProvider extends NametagProvider {

  public static boolean tagsdisabled = false;

  public HCFNametagProvider() {
    super("HCF Nametags", 5);
  }

  private static NametagInfo createNametag(final Player toRefresh, final Player refreshFor, final String prefix, final String suffix) {
    return createNametag(prefix, suffix);
  }

  public static NametagInfo getNameTag(Player toRefresh, Player refreshFor) {
    final Team viewerTeam = Main.getInstance().getTeamHandler().getTeam(refreshFor);
    NametagInfo nametagInfo = null;

    if (SpectatorListener.spectators.contains(toRefresh.getUniqueId())) {
      nametagInfo = createNametag(toRefresh, refreshFor, CC.translate("&7"), " §c✘");
    }
    if (CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(toRefresh)
        && Profile.getByUuidIfAvailable(toRefresh.getUniqueId()) != null) {
      return createNametag(toRefresh, refreshFor, CC.translate(
          (CorePlugin.getInstance().getStaffModeManager().getVanishedPlayers()
              .contains(toRefresh) ? "&7*" : "") + Profile.getByUuidIfAvailable(
              toRefresh.getUniqueId()).getRank().getColor()), "");
    }
    if (viewerTeam != null) {
      if (viewerTeam.isMember(toRefresh.getUniqueId())) {
        nametagInfo = createNametag(toRefresh, refreshFor,
            Main.getInstance().getMapHandler().getTeamRelationColor(), "");
      } else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
        nametagInfo = createNametag(toRefresh, refreshFor,
            Main.getInstance().getMapHandler().getAllyRelationColor(), "");
      }
    }
    if (refreshFor == toRefresh) {
      nametagInfo = createNametag(toRefresh, refreshFor,
          Main.getInstance().getMapHandler().getTeamRelationColor(), "");
    }
    if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused()
        .equals(toRefresh.getUniqueId())) {
      nametagInfo = createNametag(toRefresh, refreshFor, ChatColor.AQUA.toString(), "");
    }
    if (CustomTimerCreateCommand.getCustomTimers().containsKey("&a&lSOTW Timer")
        && CustomTimerCreateCommand.hasSOTWEnabled(toRefresh.getUniqueId())) {
      nametagInfo = createNametag(toRefresh, refreshFor, ChatColor.GOLD.toString(), "");
    }
    if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY) && refreshFor != toRefresh
        && !refreshFor.hasMetadata("invisible")) {

      Team otherTeam = Main.getInstance().getTeamHandler().getTeam(refreshFor);

      if (otherTeam != null && viewerTeam != null && otherTeam.getAllies()
          .contains(viewerTeam.getUniqueId())) {
        LunarClientAPI.getInstance().resetNametag(toRefresh, refreshFor);
        nametagInfo = createNametag(toRefresh, refreshFor,
            Main.getInstance().getMapHandler().getAllyRelationColor(), "");
      }

      if (viewerTeam == null) {
        LunarClientAPI.getInstance().resetNametag(toRefresh, refreshFor);
        nametagInfo = createNametag(toRefresh, refreshFor, "invis", "");
      }
      if (viewerTeam != null && !viewerTeam.isMember(toRefresh.getUniqueId())) {
        LunarClientAPI.getInstance().resetNametag(toRefresh, refreshFor);
        nametagInfo = createNametag(toRefresh, refreshFor, "invis", "");
      }


    }
    if (ArcherClass.isMarked(toRefresh) && (viewerTeam == null
        || viewerTeam != null && !viewerTeam.isMember(toRefresh.getUniqueId()))) {
      nametagInfo = createNametag(toRefresh, refreshFor,
          Main.getInstance().getMapHandler().getArcherTagColor(), "");
    }

    return (nametagInfo == null) ? createNametag(toRefresh, refreshFor,
        "" + Main.getInstance().getMapHandler().getDefaultRelationColor(), "") : nametagInfo;
  }

  public static void updateLC(Player toRefresh, Player refreshFor) {

    HCFProfile hcfProfile = HCFProfile.getByUUID(refreshFor.getUniqueId());

    if(hcfProfile == null) {
      return;
    }

    if (!hcfProfile.isTeamNameTags()) {
      LunarClientAPI.getInstance().resetNametag(toRefresh, refreshFor);
      return;
    }

    Team team = Main.getInstance().getTeamHandler().getTeam(toRefresh);
    boolean kitmap = Main.getInstance().getMapHandler().isKitMap();
    boolean pvptimer = !kitmap && HCFProfile.get(toRefresh).hasPvPTimer();
    String prefix = getNameTag(toRefresh, refreshFor).getPrefix();

    if (prefix.equals("invis")) {
      prefix = Main.getInstance().getMapHandler().getDefaultRelationColor();
    }

    List<String> nametag = Lists.newArrayList();

    if (BountyPlayer.get(toRefresh) != null) {
      BountyPlayer bountyPlayer = BountyPlayer.get(toRefresh);

      nametag.add(CC.translate(
          "&6Bounty: &2$&a" + bountyPlayer.getBalance() + " &7(&a+"
              + bountyPlayer.getRewards().size() + " Items&7)"));
      nametag.add("");
    }

    if (CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(toRefresh)
        && Profile.getByUuidIfAvailable(toRefresh.getUniqueId()) != null) {
      nametag.add(CC.translate("&7&o[Mod]"));
    }
    if (SpectatorListener.spectators.contains(toRefresh.getUniqueId())) {
      nametag.add(CC.translate("&7[Spectator]"));
    }
    if (team != null && !pvptimer) {
      String icon = getIconByPosition(team);

      nametag.add(CC.translate(icon + " &6[" + team.getName(refreshFor) + " &7┃ "
          + team.getDTRString() + "&6]" + (
          team.isDisqualified() ? CC.BD_RED + " ✘" : "")));
    }
    if (pvptimer) {
      nametag.add("§6 PvP Timer ");
    }

    nametag.add(prefix + toRefresh.getDisguisedName());

    LunarClientAPI.getInstance().overrideNametag(toRefresh, nametag, refreshFor);
  }

  @Override
  public NametagInfo fetchNametag(final Player toRefresh, final Player refreshFor) {
    updateLC(toRefresh, refreshFor);
    return getNameTag(toRefresh, refreshFor);
  }

  private static String getIconByPosition(Team team) {
    LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamTopCommand.getTopTeams();

    int position = 0;

    for (Team t : sortedTeamPlayerCount.keySet()) {

      if (t.getOwner() == null) {
        continue;
      }

      position++;
      if (position > 3) {
        break;
      }

      if (t.getUniqueId() == team.getUniqueId()) {
        break;
      }
    }

    if (position == 1) {
      return "&d➊";
    } else if (position == 2) {
      return "&5➋";
    } else if (position == 3) {
      return "&6➌";
    }

    return "";
  }
}