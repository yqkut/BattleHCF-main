package cc.stormworth.hcf.misc.crazyenchants.utils;

import cc.stormworth.hcf.misc.crazyenchants.utils.enums.ShopOption;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.Category;
import cc.stormworth.hcf.misc.crazyenchants.utils.objects.LostBook;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.entity.Player;

public class CurrencyAPI {

    public static int getCurrency(Player player) {
        return player.getLevel();
    }

    public static void takeCurrency(Player player, Category category) {
        takeCurrency(player, category.getCost());
    }

    public static void takeCurrency(Player player, LostBook lostBook) {
        takeCurrency(player, lostBook.getCost());
    }

    public static void takeCurrency(Player player, ShopOption option) {
        takeCurrency(player, option.getCost());
    }

    public static void takeCurrency(Player player, int amount) {
        player.setLevel(player.getLevel() - amount);
    }

    public static void giveXP(Player player, int amount) {
        player.setLevel(player.getLevel() + amount);
    }

    public static void giveLevels(Player player, int amount) {
        takeTotalExperience(player, -amount);
    }

    public static boolean canBuy(Player player, Category category) {
        return canBuy(player, category.getCost());
    }

    public static boolean canBuy(Player player, LostBook lostBook) {
        return canBuy(player, lostBook.getCost());
    }

    public static boolean canBuy(Player player, ShopOption option) {
        return canBuy(player, option.getCost());
    }

    public static boolean canBuy(Player player, int cost) {
        return getCurrency(player) >= cost;
    }

    private static void takeTotalExperience(Player player, int amount) {
        int total = getTotalExperience(player) - amount;
        player.setTotalExperience(0);
        player.setTotalExperience(total);
        player.setLevel(0);
        player.setExp(0);
        while (total > player.getExpToLevel()) {
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel() + 1);
        }
        float xp = (float) total / (float) player.getExpToLevel();
        player.setExp(xp);
    }

    private static int getTotalExperience(Player player) {// https://www.spigotmc.org/threads/72804
        int experience;
        int level = player.getLevel();
        if (level >= 0 && level <= 15) {
            experience = (int) FastMath.ceil(FastMath.pow(level, 2) + (6 * level));
            int requiredExperience = 2 * level + 7;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += FastMath.ceil(currentExp * requiredExperience);
            return experience;
        } else if (level > 15 && level <= 30) {
            experience = (int) FastMath.ceil((2.5 * FastMath.pow(level, 2) - (40.5 * level) + 360));
            int requiredExperience = 5 * level - 38;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += FastMath.ceil(currentExp * requiredExperience);
            return experience;
        } else {
            experience = (int) FastMath.ceil((4.5 * FastMath.pow(level, 2) - (162.5 * level) + 2220));
            int requiredExperience = 9 * level - 158;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += FastMath.ceil(currentExp * requiredExperience);
            return experience;
        }
    }
}