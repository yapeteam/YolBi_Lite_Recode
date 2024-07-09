package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class BlatantVelocity extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Vanilla", "Vanilla", "Vulcan");
    private final NumberValue<Double>
            horizontal = new NumberValue<>("Horizontal", () -> mode.is("Vanilla"), 0.0, 0.0, 100.0, 1.0),
            vertically = new NumberValue<>("Vertically", () -> mode.is("Vanilla"), 0.0, 0.0, 100.0, 1.0);

    public BlatantVelocity() {
        super("BlatantVelocity", ModuleCategory.COMBAT);
        addValues(mode, horizontal, vertically);
    }

    @Listener
    private void onPacket(EventPacketReceive event) {
        switch (mode.getValue()) {
            case "Vanilla":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity velocity = event.getPacket();

                    if (velocity.getEntityID() != mc.thePlayer.getEntityId())
                        return;

                    if (horizontal.getValue() == 0.0 && vertically.getValue() == 0.0) {
                        event.setCancelled(true);
                    } else {
                        ReflectUtil.S12PacketEntityVelocity$setMotionX(velocity, velocity.getMotionX() * (horizontal.getValue() / 100));
                        ReflectUtil.S12PacketEntityVelocity$setMotionY(velocity, velocity.getMotionY() * (vertically.getValue() / 100));
                        ReflectUtil.S12PacketEntityVelocity$setMotionZ(velocity, velocity.getMotionZ() * (horizontal.getValue() / 100));
                    }
                }
                break;
            case "Vulcan":
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity velocity = event.getPacket();

                    if (velocity.getEntityID() != mc.thePlayer.getEntityId())
                        return;

                    event.setCancelled(true);
                }
        }
    }

    @Listener
    private void onSendPacket(EventPacketSend event) {
        if (mode.is("Vulcan")) {
            if (mc.thePlayer.hurtTime > 0 && event.getPacket() instanceof C0FPacketConfirmTransaction) {
                event.setCancelled(true);
            }
        }
    }
}
