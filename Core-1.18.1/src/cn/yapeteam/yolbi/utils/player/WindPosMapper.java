package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindPosMapper {
    public static void main(String[] args) {
        Vector2f start = new Vector2f(-370, 90.0f);
        Vector2f end = new Vector2f(1800, 90.0f);
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

        float startYawOffset = start.x - start.x % 360.0f;
        float startPitchOffset = start.y - start.y % 360.0f;

        start.x = wrapAngleTo180_float(start.x);
        start.y = wrapAngleTo180_float(start.y);
        end.x = wrapAngleTo180_float(end.x);
        end.y = wrapAngleTo180_float(end.y);

        float currentX = start.x % 360.0f;
        float currentY = start.y % 360.0f;
        float targetX = end.x % 360.0f;
        float targetY = end.y % 360.0f;

        while (Math.hypot(currentX - targetX, currentY - targetY) > 1) {
            float deltaX = targetX - currentX;
            float deltaY = targetY - currentY;
            float distance = (float) Math.hypot(deltaX, deltaY);
            float randomDist = Math.min(maxStep, distance);
            float stepSize = Math.max(0.1f, randomDist / targetArea);
            float windX = (random.nextFloat() * 2 - 1) * wind;
            float windY = (random.nextFloat() * 2 - 1) * wind;
            float newX = currentX + deltaX / distance * stepSize + windX;
            float newY = currentY + deltaY / distance * stepSize + windY;

            currentX = newX;
            currentY = newY;
            path.add(new Vector2f(currentX + startYawOffset, currentY + startPitchOffset));

            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * distance / 1000.0f;
        }

        path.add(new Vector2f(targetX + startYawOffset, targetY + startPitchOffset));
        return path;
    }

    public static List<Vector2f> generatePath2(Vector2f start, Vector2f end) {
        float startYawOffset = start.x - start.x % 360.0f;
        float startPitchOffset = start.y - start.y % 360.0f;

        List<Vector2f> path = new ArrayList<>();
        float wind = 6.0f;
        float gravity = 19.0f;
        float maxStep = 17.0f;
        float targetArea = 1.0f;
        float currentDistanceThreshold = .1f; // 用于控制循环的阈值

        float currentX = start.x % 360.0f;
        float currentY = start.y % 360.0f;

        float targetX = end.x % 360.0f;
        float targetY = end.y % 360.0f;

        while (true) {
            float distanceToTarget = (float) Math.hypot(targetX - currentX, targetY - currentY);
            if (distanceToTarget < currentDistanceThreshold) {
                path.add(new Vector2f(targetX + startYawOffset, targetY + startPitchOffset));
                break;
            }

            float angleToTarget = (float) Math.atan2(targetY - currentY, targetX - currentX);
            float stepSize = Math.min(maxStep, distanceToTarget / targetArea);

            // 调整风力影响，添加随机性
            float windX = (random.nextFloat() * 2 - 1) * wind;
            float windY = (random.nextFloat() * 2 - 1) * wind;

            float deltaX = (float) (Math.cos(angleToTarget) * stepSize) + windX;
            float deltaY = (float) (Math.sin(angleToTarget) * stepSize) + windY;

            float newX = currentX + deltaX;
            float newY = currentY + deltaY;

            Vector2f lastPoint = null;
            if (!path.isEmpty()) lastPoint = path.get(path.size() - 1);

            // 检查新点是否与现有点过于接近
            if (!path.isEmpty() && !(lastPoint != null && Math.hypot(newX - lastPoint.x, newY - lastPoint.y) > currentDistanceThreshold)) {
                // 如果点过于接近，调整步长或添加随机偏移
                stepSize *= 0.9f; // 减小步长
                newX = currentX + (float) (Math.cos(angleToTarget) * stepSize);
                newY = currentY + (float) (Math.sin(angleToTarget) * stepSize);
            }
            path.add(new Vector2f(newX + startYawOffset, newY + startPitchOffset));
            currentX = newX;
            currentY = newY;

            // 更新风力和重力影响
            wind = Math.max(0.0f, wind - wind / 3.0f);
            wind += (random.nextFloat() * 2 - 1) * gravity * distanceToTarget / 1000.0f;
            currentDistanceThreshold *= 0.9f; // 逐渐减小阈值，实现平滑减速
        }

        return path;
    }
}
