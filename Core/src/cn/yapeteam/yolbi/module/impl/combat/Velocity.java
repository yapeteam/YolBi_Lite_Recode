package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
public class Velocity extends Module {
    private final NumberValue<Double> horizontal = new NumberValue<>("Horizontal velocity", 80.0, 0.0, 100.0, 1.0);

    private final NumberValue<Double> vertical = new NumberValue<>("Verical velocity", 80.0, 0.0, 100.0, 1.0);

    public Velocity() {
        addValues(horizontal, vertical);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.hurtTime >= 8) {
            ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, true);
        }
        if (mc.thePlayer.hurtTime >= 4) {
            ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, false);
        } else if (mc.thePlayer.hurtTime > 1) {
            ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
        }
    }

    @Listener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            YolBi.instance.getNotificationManager().post(
                    new Notification(
                            "Velocity",
                            Easing.EASE_IN_OUT_QUAD,
                            Easing.EASE_IN_OUT_QUAD,
                            2500, NotificationType.WARNING
                    )
            );

            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            int x = packet.getMotionX();
            int y = packet.getMotionY();
            int z = packet.getMotionZ();

            // Calculate the new velocities based on the current velocities and the horizontal and vertical values
            double newX = x / 8000.0D * horizontal.getValue();
            double newY = y / 8000.0D * vertical.getValue();
            double newZ = z / 8000.0D * horizontal.getValue();

            // Set the new velocities
            ReflectUtil.setMotionX(mc.thePlayer, newX);
            ReflectUtil.setMotionY(mc.thePlayer, newY);
            ReflectUtil.setMotionZ(mc.thePlayer, newZ);
        }
    }
}

