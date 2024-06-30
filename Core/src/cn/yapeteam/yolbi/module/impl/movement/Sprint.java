package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    private boolean sprinting;
    private final BooleanValue autoSprint = new BooleanValue("AutoSprint", true);

    {
        addValues(autoSprint);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (mc.thePlayer == null) return;

        if (autoSprint.getValue()) {
            if (mc.thePlayer.movementInput.moveForward > 0) {
                if (!mc.thePlayer.isSprinting()) {
                    mc.thePlayer.setSprinting(true);
                    sprinting = true;
                }
            } else {
                if (sprinting) {
                    mc.thePlayer.setSprinting(false);
                    sprinting = false;
                }
            }
        }
    }
}
