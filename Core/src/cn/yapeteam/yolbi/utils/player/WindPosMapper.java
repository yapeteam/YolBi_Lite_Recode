package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.loader.utils.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindPosMapper {
    private static final Random random = new Random();

    public static float wrapAngleTo180_float(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public static List<Vector2f> generatePath(Vector2f start, Vector2f end) {
        List<Vector2f> path = new ArrayList<>();
        float wind = 14.0f;
        float gravity = 25.0f;
        float maxStep = 15.0f;
        float targetArea = 5.0f;

        float currentX = start.x;
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
            path.add(new Vector2f(currentX, currentY));

            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * distance / 1000.0f;
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
}
