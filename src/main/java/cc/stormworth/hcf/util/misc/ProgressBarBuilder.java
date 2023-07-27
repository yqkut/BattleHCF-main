package cc.stormworth.hcf.util.misc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;

@Getter
@Setter
@Accessors(fluent = true)
public class ProgressBarBuilder {

    private int blocksToDisplay;
    private char blockChar;
    private String completedColor;
    private String uncompletedColor;

    public ProgressBarBuilder(int blocksToDisplay) {
        this.blocksToDisplay = blocksToDisplay;
        blockChar = StringEscapeUtils.unescapeJava("\u2588").charAt(0);
        completedColor = ChatColor.GREEN.toString();
        uncompletedColor = ChatColor.GRAY.toString();
    }

    public ProgressBarBuilder() {
        this(10);
    }

    public static double pastpercentage(int value, int goal) {
        return (double) (value / goal) * 100.0D;
    }

    public static double percentage(int value, int goal) {
        return value > goal ? 100.0D : (((double) value / (double) goal) * 100.0D);
    }

    public String build(double percentage) {
        String[] blocks = new String[blocksToDisplay];
        Arrays.fill(blocks, uncompletedColor + blockChar);

        if (percentage > 100.0D) {
            percentage = 100.0D;
        }

        for (int i = 0; i < percentage / 10; i++) {
            blocks[i] = completedColor + blockChar;
        }

        return StringUtils.join(blocks);
    }
}