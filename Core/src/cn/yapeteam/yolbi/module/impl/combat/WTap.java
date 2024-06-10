package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(name = "WTap", category = ModuleCategory.COMBAT)
public class WTap extends Module {
    private boolean sprinting;
    private int ticks;

    @Listener
    private void onAttack(EventPacketSend event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK)
                ticks = 0;
        }
        if (event.getPacket() instanceof C0BPacketEntityAction) {
            final C0BPacketEntityAction wrapper = event.getPacket();

            switch (wrapper.getAction()) {
                case START_SPRINTING:
                    sprinting = true;
                    break;

                case STOP_SPRINTING:
                    sprinting = false;
                    break;
            }
        }
    }

    @Listener
    private void onMotion(EventMotion e) {
        ticks++;

        switch (ticks) {
            case 1:
                if (sprinting)
                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                else
                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                break;
            case 2:
                if (!sprinting)
                    PacketUtil.sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                break;
        }
    }
}
