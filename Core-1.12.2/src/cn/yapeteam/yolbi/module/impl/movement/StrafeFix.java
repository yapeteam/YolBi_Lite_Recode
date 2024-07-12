package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.utils.player.RotationManager;

public class StrafeFix extends Module {
    private final BooleanValue allowSprint = new BooleanValue("Allow Sprint", true);

    public StrafeFix() {
        super("StrafeFix", ModuleCategory.MOVEMENT);
        this.addValues(allowSprint);
    }

    @Listener
    private void onStrafe(EventStrafe event) {
        if (!RotationManager.active) return;
        if (!allowSprint.getValue())
            mc.player.setSprinting(false);
        event.setYaw(RotationManager.rotations.x);
    }
}
