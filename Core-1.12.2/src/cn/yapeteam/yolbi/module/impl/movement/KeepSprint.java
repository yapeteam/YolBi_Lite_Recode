package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;

@ModuleInfo(name = "KeepSprint", category = ModuleCategory.MOVEMENT)
public class KeepSprint extends Module {
    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", true);

    {
        addValues(keepSprint);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (keepSprint.getValue()) {
            if (mc.player != null && mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
        }
    }
}
