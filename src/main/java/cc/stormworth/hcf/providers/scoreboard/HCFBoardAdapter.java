package cc.stormworth.hcf.providers.scoreboard;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.cmds.staff.FreezeCommand;
import cc.stormworth.core.profile.Profile;
import cc.stormworth.core.server.utils.FreezeInfo;
import cc.stormworth.core.staffmode.StaffModeManager;
import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.scoreboard.ScoreboardAdapter;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.commands.op.EndEventCommand;
import cc.stormworth.hcf.commands.staff.CustomTimerCreateCommand;
import cc.stormworth.hcf.commands.staff.EOTWCommand;
import cc.stormworth.hcf.events.Event;
import cc.stormworth.hcf.events.EventType;
import cc.stormworth.hcf.events.conquest.game.ConquestGame;
import cc.stormworth.hcf.events.dtc.DTC;
import cc.stormworth.hcf.events.eclipse.EclipseEvent;
import cc.stormworth.hcf.events.koth.KOTH;
import cc.stormworth.hcf.events.ktk.KillTheKing;
import cc.stormworth.hcf.events.ktk.commands.KTKCommand;
import cc.stormworth.hcf.events.mad.MadGame;
import cc.stormworth.hcf.listener.GoldenAppleListener;
import cc.stormworth.hcf.listener.SpectatorListener;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import cc.stormworth.hcf.profile.HCFProfile;
import cc.stormworth.hcf.profile.deathban.DeathBan;
import cc.stormworth.hcf.pvpclasses.pvpclasses.ArcherClass;
import cc.stormworth.hcf.pvpclasses.pvpclasses.BardClass;
import cc.stormworth.hcf.server.SpawnTagHandler;
import cc.stormworth.hcf.supplydrop.SupplyDropManager;
import cc.stormworth.hcf.team.Team;
import cc.stormworth.hcf.team.TeamUtils;
import cc.stormworth.hcf.team.claims.LandBoard;
import cc.stormworth.hcf.team.commands.team.TeamStuckCommand;
import cc.stormworth.hcf.team.dtr.DTRBitmask;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import cc.stormworth.hcf.util.cooldowntimer.TimerManager;
import cc.stormworth.hcf.util.player.Logout;
import cc.stormworth.hcf.util.player.Spawn;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HCFBoardAdapter implements ScoreboardAdapter {

    private static final String STRAIGHT_SCOREBOARD_LINE = StringUtils.repeat("-", 28);

    @Override
    public String getTitle(Player player) {
        String name = CorePlugin.getInstance().getServerId();

        if (name.toLowerCase().contains("hcf")) {
            name = "HCF";
        }

        if (name.toLowerCase().contains("kits")) {
            name = "KitMap";
        }

        return CC.translate("&6&l" + name);
    }

    @Override
    public List<String> getLines(Player player) {
        LinkedList<String> lines = new LinkedList<>();

        String spawnTagScore = this.getSpawnTagScore(player);
        String enderpearlScore = this.getEnderpearlScore(player);
        String abilityCooldown = this.getGlobalCooldown(player);
        String pvpTimerScore = this.getPvPTimerScore(player);
        String archerMarkScore = this.getArcherMarkScore(player);
        String bardEffectScore = this.getBardEffectScore(player);
        String bardEnergyScore = this.getBardEnergyScore(player);
        String fstuckScore = this.getFStuckScore(player);
        String logoutScore = this.getLogoutScore(player);
        String spawnScore = this.getSpawnScore(player);
        String homeScore = this.getHomeScore(player);
        String appleScore = this.getAppleScore(player);

        Profile profile = Profile.getByUuidIfAvailable(player.getUniqueId());
        HCFProfile hcfProfile = HCFProfile.getByUUID(player.getUniqueId());

        if (FreezeCommand.getFreezes().containsKey(player.getUniqueId())) {
            lines.add("&f\u2588\u2588\u2588\u2588&c\u2588&f\u2588\u2588\u2588\u2588");
            lines.add("&f\u2588\u2588\u2588&c\u2588&6\u2588&c\u2588&f\u2588\u2588\u2588");
            lines.add("&f\u2588\u2588&c\u2588&6\u2588&0\u2588&6\u2588&c\u2588&f\u2588\u2588");
            lines.add("&f\u2588&c\u2588&6\u2588\u2588&0\u2588&6\u2588\u2588&c\u2588&f\u2588");
            lines.add("&f\u2588&c\u2588&6\u2588\u2588\u2588\u2588\u2588&c\u2588&f\u2588");
            lines.add("&c\u2588&6\u2588\u2588\u2588&0\u2588&6\u2588\u2588\u2588&c\u2588");
            lines.add("&f\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588");
            lines.add("");
            lines.add("&4&lYou are now freezed!");
            lines.add("&cJoin our Teamspeak!");

        } else if (SpectatorListener.spectators.contains(player.getUniqueId())) {

            lines.add("&c&lSpectator Mode");
            lines.add("&eSpectators&7: &f" + SpectatorListener.spectatorsSize());
            lines.add("&eAlive&7: &f" + (Bukkit.getOnlinePlayers().size() - SpectatorListener.spectatorsSize()));

            for (final Event event : Main.getInstance().getEventHandler().getEvents()) {
                if (event.isActive()) {

                    if (event.isHidden()) {
                        continue;
                    }

                    if(event.getName().equalsIgnoreCase(MadGame.EVENT_NAME)){
                       lines.add("&6&lFaction Captures&7: ");

                        int displayed = 0;

                        List<Team> sortedTeams = MadGame.getTeamPoints().keySet().stream()
                                .filter(id -> Main.getInstance().getTeamHandler().getTeam(id) != null)
                                .map(id -> Main.getInstance().getTeamHandler().getTeam(id))
                                .sorted(Comparator.comparing(Team::getPoints).reversed()).sorted((o1, o2) -> o2.getPoints() - o1.getPoints())
                                .collect(Collectors.toList());

                        for (Team team : sortedTeams) {
                            if (team == null) {
                                continue;
                            }

                            String teamName = team.getName(player);

                            lines.add("&e-" + teamName + "&e: &c" + MadGame.getTeamPoints().get(team.getUniqueId()));
                            ++displayed;

                            if (displayed == 4) {
                                break;
                            }
                        }

                        if (displayed == 0) {
                            lines.add(" &cNo scores yet");
                        }

                        lines.add("&4&lMad Event:" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));

                    }else{
                        String displayName = TeamUtils.getEventName(event.getName());

                        if (event.getType() == EventType.DTC) {
                            lines.add(displayName + "&7: &c" + ((DTC) event).getCurrentPoints());
                        } else {
                            lines.add(displayName + "&7: &c" +
                                    ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));
                        }
                    }

                }
            }
            if (EOTWCommand.isFfaEnabled()) {
                long ffaEnabledAt = EOTWCommand.getFfaActiveAt();

                if (System.currentTimeMillis() < ffaEnabledAt) {
                    long difference = ffaEnabledAt - System.currentTimeMillis();
                    lines.add("&4&lFFA&7: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000.0f));
                }
            }

        } else {
            Team ownerTeam = LandBoard.getInstance().getTeam(player.getLocation());

            String location;
            if (ownerTeam != null) {
                location = ownerTeam.getName(player.getPlayer());
            } else if (!Main.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                location = ChatColor.GRAY + "Wilderness";
            } else if (LandBoard.getInstance().getTeam(player.getLocation()) != null
                    && LandBoard.getInstance().getTeam(player.getLocation()).getName().equalsIgnoreCase("citadel")) {
                location = ChatColor.DARK_PURPLE + "Citadel";
            } else {
                location = ChatColor.RED + "Warzone";
            }

            if (profile != null && CorePlugin.getInstance().getStaffModeManager()
                    .hasStaffToggled(player)) {

                lines.add(CC.PRIMARY + "&lStaff Mode&7:");
                lines.add("&7»&f Vanish&7: " +
                        (StaffModeManager.getVanishedPlayers().contains(player.getUniqueId()) ?
                                "&aEnabled"
                                : "&cDisabled"));

                lines.add("&7»&f Chat&7: " + (profile.isAdminchat() ? "&cAdmin"
                        : profile.isStaffchat() ? "&9Staff" : "&aPublic"));

                lines.add("&7»&f Online&7: &e" +
                        CorePlugin.getInstance().getServer().getOnlinePlayers().size() +
                        "/"
                        + CorePlugin.getInstance().getServer().getMaxPlayers());

                FreezeInfo freezeInfo = FreezeCommand.getFreezeByStaff(player.getUniqueId());

                if (freezeInfo != null){
                    long freezeTime = System.currentTimeMillis() - freezeInfo.getTime();

                    String time = ScoreFunction.TIME_SIMPLE.apply((float) (freezeTime / 1000));

                    lines.add("&7»&f Freeze&7:");
                    lines.add("  &fTarget&7: &c" + UUIDUtils.name(freezeInfo.getTarget()));
                    lines.add("  &fTime&7: &c" + time);
                }
            }

            if (!player.getWorld().getName().equalsIgnoreCase("void")) {
                lines.add("&6&lClaim&7: " + location);
            }

            if (!Main.getInstance().getMapHandler().isKitMap()) {

                if (hcfProfile.isDeathBanned()) {
                    String deathbanScore = this.getDeathbanScore(hcfProfile.getDeathban());
                    lines.add("&c&lDeathBan: &c" + deathbanScore);
                    lines.add("&6&lLives&7: &c" + hcfProfile.getLives());
                }

                if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                    lines.add("&6&lBalance&7: &a" + hcfProfile.getEconomyData().getFormattedBalance());
                }
            }

            if (!hcfProfile.isDeathBanned()) {

                Iterator<Map.Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();

                while (iterator.hasNext()) {

                    Map.Entry<String, Long> timer = iterator.next();

                    if (timer.getValue() < System.currentTimeMillis()) {
                        iterator.remove();
                    } else if (timer.getKey().equals("&a&lSOTW Timer")) {
                        if (CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
                            lines.add(ChatColor.translateAlternateColorCodes('&',
                                    "&c&l&mSOTW Timer&7: &c" + this.getTimerScore(timer)));
                        } else {
                            lines.add(ChatColor.translateAlternateColorCodes('&',
                                    "&a&lSOTW Timer&7: &c" + this.getTimerScore(timer)));
                        }
                    } else if (timer.getKey().equals("&6&lGlowstone")) {
                        lines.add(ChatColor.translateAlternateColorCodes('&',
                                "&6&lGlowstone&7: &c" + this.getTimerScore(timer)));
                    } else if (timer.getKey().equals("&6&lDouble Ores")) {
                        lines.add(ChatColor.translateAlternateColorCodes('&',
                                "&6&lDouble Ores&7: &c" + this.getTimerScore(timer)));
                    }else if (timer.getKey().equals("&3&lFlash Sale")) {
                        lines.add(CC.translate("&3&lFlash Sale&7: &b/store"));
                        lines.add(CC.translate("&7&l•&f " + this.getTimerScore(timer)));
                    } else {
                        lines.add(ChatColor.translateAlternateColorCodes('&', timer.getKey()) + "&7: &c"
                                + this.getTimerScore(timer));
                    }
                }
                if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && Main.getInstance().getMapHandler().isKitMap()) {

                    if(!CorePlugin.getInstance().getStaffModeManager().hasStaffToggled(player)){
                        StatsEntry stats = Main.getInstance().getMapHandler().getStatsHandler()
                                .getStats(player.getUniqueId());
                        lines.add("&e» &fKills: &e" + stats.getKills());
                        lines.add("&e» &fDeaths: &e" + stats.getDeaths());
                    }
                }

                if (spawnTagScore != null) {
                    if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && player.getGameMode() == GameMode.CREATIVE) {
                        SpawnTagHandler.removeTag(player);
                    }

                    lines.add("&c&lSpawn Tag&7: &c" + spawnTagScore);
                }

                if (homeScore != null) {
                    lines.add("&9&lHome&7: &c" + homeScore);
                }

                if (appleScore != null) {
                    lines.add("&e&lApple&7: &c" + appleScore);
                }

                if (enderpearlScore != null) {
                    lines.add("&e&lEnderpearl&7: &c" + enderpearlScore);
                }

                if (abilityCooldown != null) {
                    lines.add("&6&lAbility&7: &c" + abilityCooldown);
                }

                if (!Main.getInstance().getMapHandler().isKitMap() && pvpTimerScore != null) {

                    if(hcfProfile.getPvpTimer().isFirstTime()){
                        lines.add("&a&lSOTW Timer&7: &c" + pvpTimerScore);
                    }else{
                        lines.add("&a&lPvP Timer&7: &c" + pvpTimerScore);
                    }
                }

                for (final Event event : Main.getInstance().getEventHandler().getEvents()) {

                    if (event.isActive()) {

                        if (event.isHidden()) {
                            continue;
                        }

                        if(event.getName().equalsIgnoreCase(MadGame.EVENT_NAME)){
                            lines.add("");
                            lines.add("&6&lFaction Captures&7: ");

                            int displayed = 0;
                            for (final Map.Entry<ObjectId, Integer> entry : MadGame.getTeamPoints().entrySet()) {
                                Team resolved = Main.getInstance().getTeamHandler().getTeam(entry.getKey());

                                if (resolved == null) {
                                    MadGame.getTeamPoints().remove(entry.getKey());
                                    continue;
                                }

                                String teamName = resolved.getName(player);

                                lines.add("&7- " + teamName + "&7: &f" + entry.getValue());
                                ++displayed;

                                if (displayed == 4) {
                                    break;
                                }

                            }

                            if (displayed == 0) {
                                lines.add("&cNo scores yet");
                            }

                            lines.add("");
                            lines.add("&4&lMad Event&7: &f" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));

                        }else {
                            String displayName = TeamUtils.getEventName(event.getName());

                            if (event.getType() == EventType.DTC) {
                                lines.add(displayName + ": &c" + ((DTC) event).getCurrentPoints());
                            } else {
                                lines.add(displayName + ": &c" + ScoreFunction.TIME_SIMPLE.apply(
                                        (float) ((KOTH) event).getRemainingCapTime()));
                            }
                        }
                    }
                }

                if (EOTWCommand.isFfaEnabled()) {
                    long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
                    if (System.currentTimeMillis() < ffaEnabledAt) {
                        long difference = ffaEnabledAt - System.currentTimeMillis();
                        lines.add("&4&lFFA&7: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000.0f));
                    }
                }
                if (archerMarkScore != null) {
                    lines.add("&e&lArcher Mark&7: &c" + archerMarkScore);
                }

                if (bardEffectScore != null) {
                    lines.add("&e&lBard Effect&7: &c" + bardEffectScore);
                }

                if (bardEnergyScore != null) {
                    lines.add("&e&lBard Energy&7: &c" + bardEnergyScore);
                }

                if (fstuckScore != null) {
                    lines.add("&4&lStuck&7: &c" + fstuckScore);
                }

                if (spawnScore != null) {
                    lines.add("&9&lSpawn&7: &c" + spawnScore);
                }

                if (TimerManager.getInstance().getCooldownTimer().isActive(player, "camp")) {
                    lines.add("&6&lCamp&7: &c" + TimerManager.getInstance().getCooldownTimer()
                            .getTimeLeft(player, "camp"));
                }

                if (logoutScore != null) {
                    lines.add("&4&lLogout&7: &c" + logoutScore);
                }

                ConquestGame conquest = Main.getInstance().getConquestHandler().getGame();

                if (conquest != null) {
                    if (lines.size() != 0) {
                        lines.add("&0&7&m--------------------");
                    }
                    lines.add("&6&lConquest:");
                    int displayed = 0;
                    for (final Map.Entry<ObjectId, Integer> entry : ConquestGame.getTeamPoints().entrySet()) {
                        Team resolved = Main.getInstance().getTeamHandler().getTeam(entry.getKey());
                        if (resolved == null) {
                            ConquestGame.getTeamPoints().remove(entry.getKey());
                            continue;
                        }

                        String teamName = resolved.getName(player);

                        lines.add(" " + teamName + "&7: &f" + entry.getValue());
                        ++displayed;

                        if (displayed == 3) {
                            break;
                        }

                    }

                    if (displayed == 0) {
                        lines.add(" &cNo scores yet");
                    }
                }

                if (EndEventCommand.started) {

                    if (lines.size() != 0) {
                        lines.add("&0&7&m--------------------");
                    }

                    lines.add("&6&lEnd Event:");

                    int displayed = 0;
                    for (final Map.Entry<UUID, Integer> entry : EndEventCommand.getHits().entrySet()) {

                        Player resolved = Bukkit.getPlayer(entry.getKey());

                        if (resolved == null) {
                            EndEventCommand.getHits().remove(entry.getKey());
                            continue;
                        }

                        final String teamName = resolved.getName();

                        lines.add(" " + (resolved != player ? "&c" : "&2") + teamName + "&7: &f"
                                + entry.getValue());
                        ++displayed;

                        if (displayed == 3) {
                            break;
                        }
                    }

                    if (displayed == 0) {
                        lines.add(" &cNo scores yet");
                    }
                }


                if (CorePlugin.getInstance().getShutdownTask() != null &&
                        CorePlugin.getInstance().getShutdownTask().isEnabled()) {

                    lines.add(
                            "&4&lReboot&7: &f" + TimeUtil.formatTime(CorePlugin.getInstance().getShutdownTask().getSecondsUntilShutdown(), TimeUtil.FormatType.SECONDS_TO_MINUTES));
                }
                if (Main.getInstance().getKillTheKing() != null) {

                    KillTheKing killTheKing = Main.getInstance().getKillTheKing();
                    Player king = Bukkit.getServer().getPlayer(killTheKing.getUuid());

                    if (king == null) {
                        Main.getInstance().setKillTheKing(null);
                        if (KTKCommand.killTheKingListener != null) {
                            KTKCommand.killTheKingListener.unload();
                            KTKCommand.killTheKingListener = null;
                        }
                    }

                    if (king != null) {
                        lines.add("&6&lKill The King&7:");
                        lines.add("&7» &6King&7: &f" + king.getName());
                        lines.add(
                                "&7» &6Location&7: &f" + king.getLocation().getBlockX() + ", " + king.getLocation()
                                        .getBlockZ());
                        lines.add("&7» &6Time elapsed&7: &f" + TimeUtils.formatLongIntoHHMMSS(
                                TimeUnit.MILLISECONDS.toSeconds(
                                        System.currentTimeMillis() - killTheKing.getStarted())));
                    }
                }
                Team playerTeam = Main.getInstance().getTeamHandler().getTeam(player.getUniqueId());

                if (playerTeam != null && playerTeam.getFocus() != null) {

                    Team focusTeam = Main.getInstance().getTeamHandler().getTeam(playerTeam.getFocus());

                    if (focusTeam == null) {
                        Main.getInstance().getTeamHandler().getTeam(player.getUniqueId()).setFocus(null);
                    } else {

                        if (!lines.isEmpty()) {
                            lines.add(" ");
                        }

                        lines.add("&6&lTeam&7: &e" + focusTeam.getName());
                        lines.add(
                                "&6&lHQ&7: &e" + ((focusTeam.getHQ() != null) ? (focusTeam.getHQ().getBlockX()
                                        + ", " + focusTeam.getHQ().getBlockZ()) : "None"));
                        if (focusTeam.getOwner() != null) {
                            lines.add("&6&lDTR&7: &e" + focusTeam.getDTRString());
                            lines.add("&6&lOnline&7: &e" + focusTeam.getOnlineMemberAmount());
                        }
                    }
                }
            }

            if(Main.getInstance().getMapHandler().isKitMap()){
                SupplyDropManager supplyDropManager = Main.getInstance().getSupplyDropManager();

                if(supplyDropManager.getLastLocation() != null){
                    lines.add("&0&7&m" + STRAIGHT_SCOREBOARD_LINE);
                    lines.add("&e&ki&r &6&lSupply Drop &e&ki");
                    lines.add("");
                    lines.add("&eCoords&7: " + supplyDropManager.getLastLocation().getBlockX() + ", " + supplyDropManager.getLastLocation().getBlockY() + ", " + supplyDropManager.getLastLocation().getBlockZ());
                    lines.add("&eStatus&7: " + ((supplyDropManager.getOpenIn() - System.currentTimeMillis()) <= 0 ? "&cNot Lotted" : (supplyDropManager.isOpen() ? "&cOpening: " + ScoreFunction.TIME_FANCY.apply((float) (supplyDropManager.getOpenIn() - System.currentTimeMillis()) / 1000) : "&cNot opened.")));
                }
            }
        }

        EclipseEvent eclipseEvent = Main.getInstance().getEventHandler().getEclipseEvent();

        if (eclipseEvent.isActive()) {
            lines.addAll(eclipseEvent.getScoreboardScore(player));
        }

        if (!lines.isEmpty()) {
            lines.addFirst("&2&7&m" + STRAIGHT_SCOREBOARD_LINE);

            boolean utc = CorePlugin.getInstance().getConfigFile().getConfig().getString("proxy").equals("eu");
            date.setTime(System.currentTimeMillis());
            format.setTimeZone(TimeZone.getTimeZone(utc ? "UTC" : "EST"));

            lines.add(" ");
            lines.add(CC.GRAY + format.format(date) + (utc ? " UTC" : " EST"));
            lines.add("&6&7&m" + STRAIGHT_SCOREBOARD_LINE);
            lines.add("&6battle.rip");
        }

        return lines;
    }

    public String getSpawnScore(final Player player) {
        Spawn spawn = Main.getInstance().getServerHandler().getSpawntasks().get(player.getName());

        if (spawn != null) {

            float diff = spawn.getSpawnTime() - System.currentTimeMillis();
            if (diff >= 0.0f) {
                return ScoreFunction.TIME_FANCY.apply(diff / 1000.0f);
            }
        }
        return null;
    }

    public String getDeathbanScore(DeathBan deathBan) {

        long unbannedOn = deathBan.getExpireAt();
        long left = unbannedOn - System.currentTimeMillis();

        if (left >= 0L) {
            return ScoreFunction.TIME_FANCY.apply(left / 1000.0f);
        }
        return null;
    }

    public String getAppleScore(Player player) {
        if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId())
                && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId())
                >= System.currentTimeMillis()) {
            float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId())
                    - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getHomeScore(Player player) {
        if (Main.getInstance().getServerHandler().getHomeTimer().containsKey(player.getName())
                && Main.getInstance().getServerHandler().getHomeTimer().get(player.getName())
                >= System.currentTimeMillis()) {
            float diff = Main.getInstance().getServerHandler().getHomeTimer().get(player.getName())
                    - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }
        return (null);
    }

    public String getFStuckScore(Player player) {
        if (TeamStuckCommand.getWarping().containsKey(player.getName())) {
            float diff = TeamStuckCommand.getWarping().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getLogoutScore(Player player) {
        Logout logout = Main.getInstance().getServerHandler().getLogouttasks().get(player.getName());

        if (logout != null) {
            float diff = logout.getLogoutTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }
        return null;
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }
        return (null);
    }

    public String getEnderpearlScore(Player player) {
        if (CooldownAPI.hasCooldown(player, "enderpearl")) {

            float diff = CooldownAPI.getCooldown(player, "enderpearl");

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }
        return (null);
    }

    public String getGlobalCooldown(Player player) {
        if (CooldownAPI.hasCooldown(player, "Global")) {

            float diff = CooldownAPI.getCooldown(player, "Global");

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }
        return (null);
    }

    public String getPvPTimerScore(Player player) {

        HCFProfile profile = HCFProfile.get(player);

        if (profile.hasPvPTimer()) {
            int secondsRemaining = (int) (profile.getPvpTimer().getRemaining() / 1000);

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_FANCY.apply((float) secondsRemaining));
            }
        }
        return (null);
    }

    public String getTimerScore(Map.Entry<String, Long> timer) {
        long diff = timer.getValue() - System.currentTimeMillis();

        if (diff > 0) {
            return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
        } else {
            return (null);
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName())
                && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff =
                    BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }
        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }
        return (null);
    }
    private final Date date = new Date();
    private final SimpleDateFormat format = new SimpleDateFormat("MMMM d, HH:mm");

}
