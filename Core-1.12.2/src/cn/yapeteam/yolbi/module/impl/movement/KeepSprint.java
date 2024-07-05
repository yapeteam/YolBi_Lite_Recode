package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
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
            if (mc.thePlayer != null && mc.thePlayer.isSprinting()) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}
