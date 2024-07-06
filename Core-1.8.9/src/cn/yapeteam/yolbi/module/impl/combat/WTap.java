package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;

public class WTap extends Module {
    private boolean unSprint, canDo;

    protected WTap() {
        super("WTap", ModuleCategory.COMBAT);
    }

    @Listener
    private void onAttack(EventAttack event) {
        canDo = Math.random() * 100 < 95;

        if (!canDo) return;

        if (mc.thePlayer.isSprinting() || Natives.IsKeyDown(VirtualKeyBoard.VK_LCONTROL)) {
            mc.thePlayer.setSprinting(true);
            unSprint = true;
        }
    }

    @Listener
    private void onPreMotion(EventMotion event) {
        if (!canDo) return;

        if (unSprint) {
            mc.thePlayer.setSprinting(false);
            unSprint = false;
        }
    }
}
