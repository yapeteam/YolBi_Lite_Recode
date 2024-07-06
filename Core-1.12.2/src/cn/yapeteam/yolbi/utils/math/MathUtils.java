package cn.yapeteam.yolbi.utils.math;

import net.minecraft.util.math.AxisAlignedBB;

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

    public static AxisAlignedBB intersect(AxisAlignedBB axisAlignedBB1,AxisAlignedBB axisAlignedBB2) {
        double lvt_2_1_ = Math.max(axisAlignedBB1.minX, axisAlignedBB2.minX);
        double lvt_4_1_ = Math.max(axisAlignedBB1.minY, axisAlignedBB2.minY);
        double lvt_6_1_ = Math.max(axisAlignedBB1.minZ, axisAlignedBB2.minZ);
        double lvt_8_1_ = Math.min(axisAlignedBB1.maxX, axisAlignedBB2.maxX);
        double lvt_10_1_ = Math.min(axisAlignedBB1.maxY, axisAlignedBB2.maxY);
        double lvt_12_1_ = Math.min(axisAlignedBB1.maxZ, axisAlignedBB2.maxZ);
        return new AxisAlignedBB(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
    }
}
