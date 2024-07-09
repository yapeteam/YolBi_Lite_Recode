package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;


public class Sprint extends Module {

    public Sprint() {
        super("Sprint", ModuleCategory.MOVEMENT);
    }

    @Listener
    private void onStrafe(EventStrafe event) {
        if (mc.player.isSneaking() || RotationManager.targetRotations != null && RotationsUtil.getRotationDifference(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch)) > 30F) {
            return;
        }

        if (PlayerUtil.isMoving() && !mc.player.isSprinting()) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_W, false);
            Natives.SetKeyBoard(VirtualKeyBoard.VK_W, true);
//            mc.thePlayer.setSprinting(true);
        }
    }
}
