package cc.stormworth.hcf.util.player;

/**
 * @Author NulledCode
 * @Plugin BattleHCF
 * @Date 2022-04
 */
import cc.stormworth.hcf.Main;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static final String[] alpha = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static final String[] numeric = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    @SafeVarargs
    public static <T> T of(T... args) {
        return args[nextInt(args.length)];
    }

    public static <T> T of(Collection<T> collection) {
        if (collection instanceof List)
            return of((List<T>) collection);
        int index = nextInt(collection.size());
        Iterator<T> it = collection.iterator();
        for (int i = 0; i < index; i++)
            it.next();
        return it.next();
    }

    public static <T> T of(List<T> list) {
        return list.get(nextInt(list.size()));
    }

    @SafeVarargs
    public static <T> T of(List<T>... lists) {
        int var = 0;
        for (List<T> l : lists)
            var += l.size();
        var = nextInt(var);
        for (List<T> l : lists) {
            if (var >= l.size()) {
                var -= l.size();
            } else {
                return l.get(var);
            }
        }
        throw new IllegalArgumentException("Received lists is empty");
    }

    public static <T extends Enum<?>> T of(Class<T> enumClazz) {
        return of(enumClazz.getEnumConstants());
    }

    public static int intRange(int from, int to) {
        int min = Math.min(from, to);
        int max = Math.max(from, to);
        return min + nextInt(max - min + 1);
    }

    public static float floatRange(float from, float to) {
        float min = Math.min(from, to);
        float max = Math.max(from, to);
        return nextFloat() * (max - min) + min;
    }

    public static double doubleRange(double from, double to) {
        double min = Math.min(from, to);
        double max = Math.max(from, to);
        return nextDouble() * (max - min) + min;
    }

    public static float nextFloat() {
        return getRandom().nextFloat();
    }

    public static double nextDouble() {
        return getRandom().nextDouble();
    }

    public static boolean nextBoolean() {
        return getRandom().nextBoolean();
    }

    public static int nextInt(int limit) {
        return getRandom().nextInt(limit);
    }

    public static Random getRandom() {
        return ThreadLocalRandom.current();
    }

    public static String randomAlphaNumeric(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<String>();
        for (String text : alpha) {
            alphaText.add(text);
        }
        for (String nubmer : numeric) {
            alphaText.add(nubmer);
        }
        String randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = Main.RANDOM;
            Integer rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = alphaText.get(rand);
            }
            randomName += alphaText.get(rand);
        }
        return randomName;
    }

    public static Integer randomNumeric(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<String>();
        for (String nubmer : numeric) {
            alphaText.add(nubmer);
        }
        String randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = new Random();
            Integer rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = alphaText.get(rand);
            }
            randomName += alphaText.get(rand);
        }
        return Integer.parseInt(randomName);
    }

    public static String randomString(Integer maxChar) {
        ArrayList<String> alphaText = new ArrayList<String>();
        for (String text : alpha) {
            alphaText.add(text);
        }
        String randomName = null;
        for (int i = 0; i < maxChar; ++i) {
            Random random = new Random();
            Integer rand = random.nextInt(alphaText.size());
            if (randomName == null) {
                randomName = alphaText.get(rand);
            }
            randomName += alphaText.get(rand);
        }
        return randomName;
    }
}
