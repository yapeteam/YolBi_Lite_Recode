package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.loader.utils.vector.Vector2f;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindPosMapper {

    private static final Random random = new Random();

    List<Vector2f> path = new ArrayList<>();

    public static float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }

    private static double generate(double base, double range) {
        double noise = base;
        for (int j = 0; j < 10; j++) {
            double newNoise = generateNoise(0, base * 2);
            if (Math.abs(noise - newNoise) < range)
                noise = (noise + newNoise) / 2;
            else j--;
        }
        return noise;
    }


    public static double generateNoise(double min, double max) {
        double u1, u2, v1, v2, s;
        do {
            u1 = random.nextDouble() * 2 - 1;
            u2 = random.nextDouble() * 2 - 1;
            s = u1 * u1 + u2 * u2;
        } while (s >= 1 || s == 0);

        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        v1 = u1 * multiplier;
        v2 = u2 * multiplier;
        // 将生成的噪声值缩放到指定范围内
        return (v1 + v2) / 2 * (max - min) / 4 + (max + min) / 2;
    }

    public static List<Vector2f> generatePath(Vector2f start, Vector2f end) {
        List<Vector2f> path = new ArrayList<>();
        float wind = 6.0f;
        float gravity = 19.0f;
        float minWait = 5.0f;
        float maxWait = 15.0f;
        float maxStep = 7.0f;
        float targetArea = 15.0f;

        float currentX = wrapAngleTo180_float(start.x);
        float currentY = start.y;

        while (Math.hypot(currentX - end.x, currentY - end.y) > 1) {
            float deltaX = end.x - currentX;
            float deltaY = end.y - currentY;
            float distance = (float) Math.hypot(deltaX, deltaY);
            float randomDist = Math.min(maxStep, distance);
            float stepSize = Math.max(0.1f, randomDist / targetArea);
            float windX = (random.nextFloat() * 2 - 1) * wind;
            float windY = (random.nextFloat() * 2 - 1) * wind;
            float newX = currentX + deltaX / distance * stepSize + windX;
            float newY = currentY + deltaY / distance * stepSize + windY;

            currentX = newX;
            currentY = newY;
            path.add(new Vector2f(wrapAngleTo180_float(currentX), currentY));

            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * distance / 1000.0f;
            gravity = Math.min(maxWait, gravity + 0.05f);
        }

        path.add(end);
        return path;
    }

//    public static List<Vector2f> generatePath(Vector2f start, Vector2f end) {
//        double rotationSpeed = 1;
//        List<Vector2f> path = new ArrayList<>();
//
//        // first get the difference for the yaw and pitch
//        float deltaYaw = (end.x - start.x);
//        float deltaPitch = (end.y - start.y);
//
//        // now separate them into points
//        float currentYaw = start.x;
//        float currentPitch = start.y;
//
//        while (Math.abs(deltaYaw) > rotationSpeed || Math.abs(deltaPitch) > rotationSpeed) {
//            if (Math.abs(deltaYaw) > rotationSpeed) {
//                // now apply wind and gravity to the path
//                double gravity = Math.signum(deltaYaw) * rotationSpeed;
//                float wind = (float) ((random.nextFloat() * 2 - 1) * Math.abs(deltaYaw) * rotationSpeed * Math.random());
//                System.out.println(wind);
//                currentYaw += gravity + wind;
//                deltaYaw -= gravity + wind;
//            }
//
//            if (Math.abs(deltaPitch) > rotationSpeed) {
//                // now apply wind and gravity to the path
//                double gravity = Math.signum(deltaPitch) * rotationSpeed;
//                float wind = (float) ((random.nextFloat() * 2 - 1) * Math.abs(deltaYaw) * rotationSpeed * Math.random());
//                System.out.println(wind);
//                currentPitch += gravity + wind;
//                deltaPitch -= gravity + wind;
//            }
//
//            path.add(new Vector2f(currentYaw, currentPitch));
//        }
//
//        // add the end point to the path
//        path.add(end);
//
//        return path;
//    }


    public static void main(String[] args) {
        // Create start and end points
        Vector2f start = new Vector2f(0.0f, 0.0f);
        Vector2f end = new Vector2f(180.0f, 90.0f);

        // Define rotation speed
        double rotationSpeed = 10.0;

        // Generate path
        List<Vector2f> path = WindPosMapper.generatePath(start, end);

        // Print the path
        for (Vector2f point : path) {
            System.out.println("Yaw: " + point.x + ", Pitch: " + point.y);
        }
    }
}
