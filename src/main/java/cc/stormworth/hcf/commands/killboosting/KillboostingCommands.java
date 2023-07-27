package cc.stormworth.hcf.commands.killboosting;

import cc.stormworth.core.util.chat.CC;
import cc.stormworth.core.util.command.annotations.Command;
import cc.stormworth.core.util.command.annotations.Param;
import cc.stormworth.core.util.general.TaskUtil;
import cc.stormworth.core.uuid.utils.UUIDUtils;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.misc.kills.KillInfo;
import cc.stormworth.hcf.misc.kills.KillsManager;
import cc.stormworth.hcf.misc.kills.KillsMenu;
import cc.stormworth.hcf.misc.map.stats.StatsEntry;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class KillboostingCommands {

    @Command(names = "killboosting check", permission = "op", async = true)
    public static void killboosting(Player player, @Param(name = "target") UUID target) {

        List<KillInfo> kills = KillsManager.getAllKills(target);

        List<KillInfo> possibleBoostings = Lists.newArrayList();

        KillInfo lastestKill = null;

        for(KillInfo kill : kills) {

            if (lastestKill == null) {
                lastestKill = kill;
            } else {

                int seconds = (int) ((lastestKill.getDate() - kill.getDate()) / 1000);
                int minutes = seconds / 60;

                if (minutes < 5 && lastestKill.getVictim().equals(kill.getVictim())) {
                    possibleBoostings.add(kill);
                } else {
                    lastestKill = kill;
                }
            }
        }

        player.sendMessage(CC.translate("&eTotal Kills: &6" + kills.size()));
        player.sendMessage(CC.translate("&ePotential killboosting: &6" + possibleBoostings.size()));
        player.sendMessage(CC.translate("&eUse /killboosting view " + UUIDUtils.name(target) + " to view all possible killboosting in menu"));
    }


    @Command(names = "killboosting remove", permission = "op", async = true)
    public static void remove(Player player, @Param(name = "target") UUID target) {

        List<KillInfo> kills = KillsManager.getAllKills(target);

        List<KillInfo> possibleBoostings = Lists.newArrayList();

        KillInfo lastestKill = null;
        for(KillInfo kill : kills) {

            if (lastestKill == null) {
                lastestKill = kill;
            } else {

                int seconds = (int) ((lastestKill.getDate() - kill.getDate()) / 1000);
                int minutes = seconds / 60;

                System.out.println(minutes);
                System.out.println(lastestKill.getVictim().toString());
                System.out.println(kill.getVictim().toString());

                if (minutes < 5 && lastestKill.getVictim().equals(kill.getVictim())) {
                    possibleBoostings.add(kill);
                } else {
                    lastestKill = kill;
                }
            }
        }

        StatsEntry entry = Main.getInstance().getMapHandler().getStatsHandler().getStats(target);

        entry.setKills(entry.getKills() - possibleBoostings.size());

        TaskUtil.runAsync(Main.getInstance(), () -> Main.getInstance().getMapHandler().getStatsHandler().save());

        player.sendMessage(CC.translate("&eRemoved &6" + possibleBoostings.size() + " &ekills for &6" + UUIDUtils.name(target)));
    }

    @Command(names = "killboosting view", permission = "op", async = true)
    public static void view(Player player, @Param(name = "target") UUID target) {

        List<KillInfo> kills = KillsManager.getAllKills(target);


        List<KillInfo> possibleBoostings = Lists.newArrayList();

        KillInfo lastestKill = null;
        for(KillInfo kill : kills) {

            if (lastestKill == null) {
                lastestKill = kill;
            } else {

                int seconds = (int) ((lastestKill.getDate() - kill.getDate()) / 1000);
                int minutes = seconds / 60;

                System.out.println(minutes);

                if (minutes < 5){
                    possibleBoostings.add(kill);
                } else {
                    lastestKill = kill;
                }
            }

        }

        new KillsMenu(possibleBoostings, UUIDUtils.name(target)).openMenu(player);
    }

}
