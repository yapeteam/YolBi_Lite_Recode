package cn.yapeteam.yolbi.utils.math;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {
    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }
}
