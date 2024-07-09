package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;

public class WTap extends Module {
    private boolean unsprint, wTap;

    public WTap() {
        super("WTap", ModuleCategory.COMBAT);
    }

    @Listener
    private void onAttack(EventAttack event) {
        wTap = Math.random() * 100 < 85;

        if (!wTap) return;

        if (mc.thePlayer.isSprinting() || Natives.IsKeyDown(VirtualKeyBoard.VK_LCONTROL)) {
            // 无法使用模拟按键 因为和windows快捷键冲突以及延迟过大
            mc.thePlayer.setSprinting(true);
            unsprint = true;
        }
    }

    @Listener
    private void onPreMotion(EventMotion event) {
        if (!wTap) return;

        if (unsprint) {
            mc.thePlayer.setSprinting(false);
            unsprint = false;
        }
    }
}
