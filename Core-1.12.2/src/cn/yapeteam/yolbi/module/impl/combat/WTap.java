package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;

public class WTap extends Module {
    public WTap() {
        super("WTap", ModuleCategory.COMBAT);
    }

    private boolean unSprint, canDo;

    @Listener
    private void onAttack(EventAttack event) {
        canDo = Math.random() * 100 < 95;

        if (!canDo) return;

        if (mc.player.isSprinting() || Natives.IsKeyDown(VirtualKeyBoard.VK_LCONTROL)) {
            mc.player.setSprinting(true);
            unSprint = true;
        }
    }

    @Listener
    private void onPreMotion(EventMotion event) {
        if (!canDo) return;

        if (unSprint) {
            mc.player.setSprinting(false);
            unSprint = false;
        }
    }
}
