package cc.stormworth.hcf.providers.scoreboard;

import cc.stormworth.core.util.time.TimeUtils;
import org.apache.commons.math3.util.FastMath;

public interface ScoreFunction<T> {

    ScoreFunction<Float> TIME_FANCY = (value) -> {
        if (value >= 60) {
            return (TimeUtils.formatIntoDetailedString(value.intValue()));
        } else {
            return (FastMath.round(10.0D * value) / 10.0D + "s");
        }
    };

    ScoreFunction<Float> TIME_SIMPLE = (value) -> (TimeUtils.formatIntoMMSS(value.intValue()));

    String apply(T value);
}