package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;

@ModuleInfo(name = "KeepSprint", category = ModuleCategory.MOVEMENT)
public class KeepSprint extends Module {
    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", true);

    public KeepSprint() {
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
