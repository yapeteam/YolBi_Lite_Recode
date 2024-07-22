package cn.yapeteam.yolbi.utils.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class PoseUtils {
    public static void rotateDeg(PoseStack poseStack, float angle, float x, float y, float z) {
        Vector3f vector3f = new Vector3f(x, y, z);
        Quaternion quaternion = rotationDegrees(vector3f, angle);
        poseStack.mulPose(quaternion);
    }

    public static float toRad(float angle) {
        return angle / 180.0F * (float) Math.PI;
    }

    public static Quaternion rotationDegrees(Vector3f vec, float angleDeg) {
        float f = toRad(angleDeg);
        AxisAngle4f axisangle4f = new AxisAngle4f(f, vec);
        return quaternion(axisangle4f);
    }

    public static final float PIHalf_f = (float) (Math.PI * 0.5f);
    public static final float PI2_f = (float) (Math.PI * 2.0f);
    public static final float PI_f = (float) Math.PI;

    private static float cosFromSinInternal(float sin, float angle) {
        float cos = (float) Math.sqrt(1.0f - sin * sin);
        float a = angle + PIHalf_f;
        float b = a - (int) (a / PI2_f) * PI2_f;
        if (b < 0.0)
            b = PI2_f + b;
        if (b >= PI_f)
            return -cos;
        return cos;
    }

    public static float cosFromSin(float sin, float angle) {
        return cosFromSinInternal(sin, angle);
    }

    public static Quaternion quaternion(AxisAngle4f axisAngle) {
        float sin = (float) Math.sin(axisAngle.angle * 0.5f);
        float cos = cosFromSin(sin, axisAngle.angle * 0.5f);
        return new Quaternion(axisAngle.x * sin, axisAngle.y * sin, axisAngle.z * sin, cos);
    }
}
