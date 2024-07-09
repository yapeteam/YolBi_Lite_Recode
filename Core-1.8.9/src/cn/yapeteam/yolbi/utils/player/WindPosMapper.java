package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.vector.Vector2f;
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

    // New method to calculate the shortest angle difference
    private static float shortestAngleDifference(float angle1, float angle2) {
        float difference = wrapAngleTo180_float(angle1 - angle2);
        if (difference > 180) {
            difference -= 360;
        } else if (difference < -180) {
            difference += 360;
        }
        return difference;
    }

    public static List<Vector2f> generatePath(Vector2f start, Vector2f end) {
        List<Vector2f> path = new ArrayList<>();
        float wind = 6.0f;
        float gravity = 19.0f;
        float maxStep = 17.0f;
        float targetArea = 1.0f;

        float currentX = start.x;
        float currentY = start.y;

        while (Math.hypot(currentX - end.x, currentY - end.y) > 1) {
            float angleToTarget = (float) Math.atan2(end.y - currentY, end.x - currentX);
            float currentAngle = (float) Math.atan2(currentY - start.y, currentX - start.x);
            float angleDifference = shortestAngleDifference((float) Math.toDegrees(angleToTarget), (float) Math.toDegrees(currentAngle));

            float stepSize = (float) Math.max(0.1f, Math.min(maxStep, Math.hypot(end.x - currentX, end.y - currentY)) / targetArea);
            float windX = (random.nextFloat() * 2 - 1) * wind;
            float windY = (random.nextFloat() * 2 - 1) * wind;

            // Adjusting deltaX and deltaY using the shortest angle difference
            float deltaX = (float) (Math.cos(Math.toRadians(angleDifference)) * stepSize) + windX;
            float deltaY = (float) (Math.sin(Math.toRadians(angleDifference)) * stepSize) + windY;

            currentX += deltaX;
            currentY += deltaY;
            path.add(new Vector2f(currentX, currentY));

            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * Math.hypot(end.x - currentX, end.y - currentY) / 1000.0f;
        }

        path.add(end);
        return path;
    }
}