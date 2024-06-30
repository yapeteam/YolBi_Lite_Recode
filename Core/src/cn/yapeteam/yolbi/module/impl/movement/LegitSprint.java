package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;

@ModuleInfo(name = "LegitSprint", category = ModuleCategory.MOVEMENT)
public class LegitSprint extends Module {

    @Listener
    private void onStrafe(EventStrafe event) {
        if (mc.thePlayer.isSneaking() || RotationManager.targetRotations != null && RotationsUtil.getRotationDifference(new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 30F) {
            return;
        }

        if (PlayerUtil.isMoving() && !mc.thePlayer.isSprinting()) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_W, false);
            Natives.SetKeyBoard(VirtualKeyBoard.VK_W, true);
        }
    }
}