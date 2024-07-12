package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import cn.yapeteam.yolbi.utils.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class EventStrafe extends CancellableEvent implements IMinecraft {
    public float strafe;
    public float up;
    public float forward;
    public float friction;
    public float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.player.motionX *= motionMultiplier;
        mc.player.motionZ *= motionMultiplier;
    }

    public void slow(float v) {
        forward *= v;
        strafe *= v;
    }
}
