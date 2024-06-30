package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    private boolean sprinting;
    private final BooleanValue autoSprint = new BooleanValue("AutoSprint", true);

    {
        addValues(autoSprint);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (autoSprint.getValue()) {
            if (mc.thePlayer != null && mc.thePlayer.movementInput.moveForward > 0) {
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
