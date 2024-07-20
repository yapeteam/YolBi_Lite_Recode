package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class KeepSprint extends Module {
    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", true);

    public KeepSprint() {
        super("KeepSprint", ModuleCategory.MOVEMENT);
        addValues(keepSprint);
    }

    @Listener
    private void onPacket(EventPacket event) {
        try {
            if (event.getPacket() instanceof C0BPacketEntityAction) {
                C0BPacketEntityAction packet = (C0BPacketEntityAction) event.getPacket();
                if (packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    event.setCancelled(true);
                }
            }
        } catch (ClassCastException exception) {
            // Handle exception if needed
        }
    }
}
