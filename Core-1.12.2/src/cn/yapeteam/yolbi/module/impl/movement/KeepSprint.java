package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;

public class KeepSprint extends Module {
    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", true);

    public KeepSprint() {
        super("KeepSprint", ModuleCategory.MOVEMENT);
        addValues(keepSprint);
    }

    @Listener
    private void onMotion(EventPacket event) {
        EventPacket e = (EventPacket) event;
        try {
            if (e.isIncoming() && e.getPacket() instanceof CPacketEntityAction) {
                CPacketEntityAction packet = (CPacketEntityAction) e.getPacket();
                if (packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                    e.setCancelled(true);
                }
            }
        } catch (ClassCastException exception) {

        }
    }
}
