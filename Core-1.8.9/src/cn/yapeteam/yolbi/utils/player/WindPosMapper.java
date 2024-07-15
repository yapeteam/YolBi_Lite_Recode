package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindPosMapper {
    public static void main(String[] args) {
        Vector2f start = new Vector2f(0, 90.0f);
        Vector2f end = new Vector2f(-180, 90.0f);
        List<Vector2f> path = generatePath(start, end); // 增加点的数量
        // 打印路径点
        for (Vector2f point : path) {
            System.out.println("X: " + point.x + ", Y: " + point.y);
        }
    }

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
        float maxStep = 7.0f;
        float targetArea = 5.0f;

        start.x = wrapAngleTo180_float(start.x);
        start.y = wrapAngleTo180_float(start.y);
        end.x = wrapAngleTo180_float(end.x);
        end.y = wrapAngleTo180_float(end.y);

        float currentX = start.x;
        float currentY = start.y;
        float targetX = end.x;
        float targetY = end.y;

        while (Math.hypot(currentX - targetX, currentY - targetY) > 1) {
            float deltaX = shortestAngleDifference(targetX, currentX);
            float deltaY = shortestAngleDifference(targetY, currentY);
            float distance = (float) Math.hypot(deltaX, deltaY);
            float randomDist = Math.min(maxStep, distance);
            float stepSize = Math.max(0.1f, randomDist / targetArea);
            float windX = (random.nextFloat() * 2 - 1) * wind;
            float windY = (random.nextFloat() * 2 - 1) * wind;
            float newX = wrapAngleTo180_float(currentX + deltaX / distance * stepSize + windX);
            float newY = wrapAngleTo180_float(currentY + deltaY / distance * stepSize + windY);

            currentX = newX;
            currentY = newY;
            path.add(new Vector2f(currentX, currentY));

            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * distance / 1000.0f;
        }

        path.add(new Vector2f(targetX, targetY));
        return path;
    }
}
