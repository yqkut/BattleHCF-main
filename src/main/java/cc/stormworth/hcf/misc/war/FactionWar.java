package cc.stormworth.hcf.misc.war;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.time.TimeUtil;
import cc.stormworth.core.util.time.TimeUtils;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.war.arena.FactionWarArena;
import cc.stormworth.hcf.misc.war.event.FactionWarEndEvent;
import cc.stormworth.hcf.misc.war.match.FactionWarMatch;
import cc.stormworth.hcf.team.Team;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public final class FactionWar {

    private int round = 0;
    private FactionWarStage stage = FactionWarStage.STARTING;
    private FactionWarState state = FactionWarState.STARTING;

    private long startedAt = 0L;
    private long endedAt = 0L;

    private final List<FactionWarParticipant> participants = Lists.newArrayList();
    private int totalParticipants = 0;

    private final AtomicInteger counting = new AtomicInteger(121);

    private final Set<FactionWarMatch> activeMatches = Sets.newHashSet();
    private final Map<ObjectId, FactionWarMatch> factionsMatchesCache = Maps.newHashMap();

    public FactionWar() {
        new BukkitRunnable() {

            @Override
            public void run() {
                int startsIn = counting.decrementAndGet();

                if (startsIn == 120 || startsIn == 60 || startsIn == 30 || startsIn == 15) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(CC.translate("&eA &6&lFaction War &eis starting..."));
                    Bukkit.broadcastMessage(CC.translate(" &7&l• &fParticipants&7: &e" + size() + "&7/&e" + FactionWarManager.DEFAULT_WARS_SIZE));
                    Bukkit.broadcastMessage(CC.translate(" &7&l• &fStarting in&7: &e" + TimeUtil.formatTime(startsIn * 1000L, TimeUtil.FormatType.MILLIS_TO_MINUTES)));
                    Bukkit.broadcastMessage("");
                } else if (startsIn == 0) {
                    if (size() < FactionWarManager.DEFAULT_WARS_SIZE) {
                        counting.set(60);

                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(CC.translate("&cThere weren't enough participants in order to start the faction war! Trying again in 60 seconds..."));
                        Bukkit.broadcastMessage("");

                        return;
                    }

                    this.cancel();

                    startRoundCountdown();
                }
            }

        }.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);
    }

    public void addParticipant(FactionWarParticipant participant) {
        if (this.contains(participant)) {
            return;
        }

        this.participants.add(participant);

        ++this.totalParticipants;

        Bukkit.broadcastMessage(CC.translate("&c» &6" + UUIDUtils.name(participant.getFaction().getOwner()) + "&e's faction has &ajoined &ethe faction war. &7(&6" + this.size() + "&7/&6" + FactionWarManager.DEFAULT_WARS_SIZE + "&7)"));
    }

    public void removeParticipant(FactionWarParticipant participant, boolean disqualified) {
        if (!this.contains(participant)) {
            return;
        }

        this.participants.remove(participant);

        if (!disqualified) {
            --this.totalParticipants;

            Bukkit.broadcastMessage(CC.translate("&c» &6" + UUIDUtils.name(participant.getFaction().getOwner()) + "&e's faction has &cleft &ethe faction war. &7(&6" + this.size() + "&7/&6" + FactionWarManager.DEFAULT_WARS_SIZE + "&7)"));
        }
    }

    void startRoundCountdown() {
        if (this.state == FactionWarState.STARTING_ROUND) {
            return;
        }

        this.state = FactionWarState.STARTING_ROUND;
        this.stage = FactionWarStage.getByRound(++this.round);
        this.counting.set(31);

        new BukkitRunnable() {

            @Override
            public void run() {
                int intValue = counting.decrementAndGet();

                if (intValue == 30 || intValue == 15 || (intValue <= 5 && intValue > 0)) {
                    Bukkit.broadcastMessage(CC.translate("&c» &eFaction War's &6" + stage.getDisplayName() + " &ewill begin in &a" + intValue + " &esecond" + (intValue == 1 ? "" : "s") + "."));
                } else if (intValue == 0) {
                    this.cancel();

                    startRound();
                }
            }

        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    void startRound() {
        if (this.state == FactionWarState.PLAYING) {
            return;
        }

        this.state = FactionWarState.PLAYING;

        if (this.round == 1) {
            this.startedAt = System.currentTimeMillis();
        }

        List<FactionWarParticipant> participantsCopy = Lists.newArrayList(this.participants);

        while (participantsCopy.size() >= 2) {
            Optional<FactionWarArena> arena = Main.getInstance().getFactionWarManager().findAvailableArena();

            if (!arena.isPresent()) {
                throw new IllegalStateException("No arena was found to start a faction war.");
            }

            FactionWarParticipant faction1 = participantsCopy.remove(0);
            FactionWarParticipant faction2 = participantsCopy.remove(0);

            if (faction1.hasDisconnectedMembers() || faction2.hasDisconnectedMembers()) {
                int faction1Disconnections = faction1.getDisconnectedMembersMap().size();
                int faction2Disconnections = faction2.getDisconnectedMembersMap().size();

                if (faction1Disconnections > 0 && faction2Disconnections > 0) {
                    this.removeParticipant(faction1, true);
                    this.removeParticipant(faction2, true);

                    faction1.getFaction().sendMessage(CC.translate("&cYour faction has been disqualified from the faction war because one or more members went offline!"));
                    faction2.getFaction().sendMessage(CC.translate("&cYour faction has been disqualified from the faction war because one or more members went offline!"));

                    if (this.tryFinishRound()) {
                        return;
                    }
                } else {
                    if (faction1Disconnections == 0) {
                        this.removeParticipant(faction2, true);

                        faction2.getFaction().sendMessage(CC.translate("&cYour faction has been disqualified from the faction war because one or more members went offline!"));
                        faction1.getFaction().sendMessage(CC.translate("&cYour faction has automatically advanced to the next round due to the opponent faction was disqualified!"));
                    } else {
                        this.removeParticipant(faction1, true);

                        faction1.getFaction().sendMessage(CC.translate("&cYour faction has been disqualified from the faction war because one or more members went offline!"));
                        faction2.getFaction().sendMessage(CC.translate("&cYour faction has automatically advanced to the next round due to the opponent faction was disqualified!"));
                    }
                }

                continue;
            }

            FactionWarMatch match = new FactionWarMatch(this, arena.get(), faction1, faction2);

            this.activeMatches.add(match);

            this.factionsMatchesCache.put(faction1.getFactionUUID(), match);
            this.factionsMatchesCache.put(faction2.getFactionUUID(), match);
        }

        if (participantsCopy.size() == 1) {
            FactionWarParticipant faction = participantsCopy.remove(0);

            if (faction.hasDisconnectedMembers()) {
                this.removeParticipant(faction, true);

                faction.getFaction().sendMessage(CC.translate("&cYour faction has been disqualified from the faction war because one or more members went offline!"));
                return;
            }

            faction.getFaction().sendMessage(CC.translate("&aYour faction has automatically advanced to the next round due to the irregular amount of participants!"));
        }
    }

    public boolean tryFinishRound() {
        if (this.state != FactionWarState.PLAYING) {
            return false;
        }

        if (this.activeMatches.size() > 0) {
            return false;
        }

        if (this.participants.size() == 0) {
            this.finish(null);
        } else if (this.participants.size() == 1) {
            this.finish(this.participants.get(0));
        } else {
            this.startRoundCountdown();
        }

        return true;
    }

    void finish(FactionWarParticipant winner) {
        if (this.state == FactionWarState.ENDING) {
            return;
        }

        boolean hasWinner = winner != null;

        this.state = FactionWarState.ENDING;
        this.endedAt = System.currentTimeMillis();

        long upTime = this.getUpTimeMillis();

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&6&lFaction War &ehas ended..."));

        if (hasWinner) {
            Bukkit.broadcastMessage(CC.translate(" &7&l• &fWinner Faction&7: &e" + winner.getFactionName() + " &7(&a" + winner.getMembersCache().size() + "&7)"));
        } else {
            Bukkit.broadcastMessage(CC.translate(" &7&l• &fWinner Faction&7: &eNone &7(Draw)"));
        }

        Bukkit.broadcastMessage(CC.translate(" &7&l• &fUp time&7: &e" + TimeUtils.formatLongIntoMMSS(upTime)));
        Bukkit.broadcastMessage("");

        if (hasWinner) {
            Team team = winner.getFaction();

            team.setPoints(team.getPoints() + FactionWarManager.WIN_POINTS_REWARD);
            team.sendMessage("&aCongratulations! Your faction has earned &e" + FactionWarManager.WIN_POINTS_REWARD + " &apoints for winning the faction war.");
        }

        Bukkit.getPluginManager().callEvent(new FactionWarEndEvent(this, winner));

        this.participants.clear();
        this.activeMatches.clear();
        this.factionsMatchesCache.clear();
    }

    public void cancel() {
        this.activeMatches.forEach(FactionWarMatch::cancel);
    }

    public FactionWarParticipant getParticipantByFaction(ObjectId factionUUID) {
        return this.participants.stream().filter(participant -> participant.getFactionUUID().equals(factionUUID)).findFirst().orElse(null);
    }

    public FactionWarParticipant getParticipantByPlayer(UUID uuid) {
        Team team = Main.getInstance().getTeamHandler().getTeam(uuid);

        if (team == null) {
            return null;
        }

        return this.getParticipantByFaction(team.getUniqueId());
    }

    public boolean contains(FactionWarParticipant participant) {
        return this.participants.contains(participant);
    }

    public boolean contains(UUID factionUUID) {
        return this.participants.stream().filter(participant -> participant.getFactionUUID().equals(factionUUID)).findFirst().isPresent();
    }

    public FactionWarMatch getMatch(ObjectId factionUUID) {
        return this.factionsMatchesCache.get(factionUUID);
    }

    public FactionWarMatch getMatch(Player player) {
        Team team = Main.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return null;
        }

        return this.getMatch(team.getUniqueId());
    }

    public long getUpTimeMillis() {
        return this.endedAt - this.startedAt;
    }

    public int size() {
        return this.totalParticipants;
    }

    public boolean isFull() {
        return this.size() >= FactionWarManager.DEFAULT_WARS_SIZE;
    }

    @Getter
    @AllArgsConstructor
    public static enum FactionWarStage {

        STARTING("", 0),
        QUARTER_FINALS("Quarter-Finals", 1),
        SEMI_FINALS("Semi-Finals", 2),
        FINAL("Final", 3);

        private final String displayName;
        private final int round;

        public static FactionWarStage getByRound(int round) {
            for (FactionWarStage stage : values()) {
                if (stage.getRound() == round) {
                    return stage;
                }
            }

            return null;
        }
    }

    public static enum FactionWarState {

        STARTING,
        STARTING_ROUND,
        PLAYING,
        ENDING;
    }
}
