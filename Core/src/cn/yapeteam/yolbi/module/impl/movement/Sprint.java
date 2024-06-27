package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;

@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @Listener
    private void onStrafe(EventStrafe event) {
        if (!PlayerUtil.isMoving() || mc.thePlayer.isSneaking() || RotationManager.targetRotations != null && RotationsUtil.getRotationDifference(new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 30F) {
            mc.thePlayer.setSprinting(false);
            return;
        }

        if (mc.thePlayer.movementInput.moveForward >= 0.8F) mc.thePlayer.setSprinting(true);
    }
}
