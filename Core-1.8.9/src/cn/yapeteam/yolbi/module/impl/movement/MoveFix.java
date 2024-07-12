package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMoveInput;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.MoveUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;

public class MoveFix extends Module {
    public MoveFix() {
        super("MoveFix", ModuleCategory.MOVEMENT);
    }

    @Listener
    private void onMoveInput(EventMoveInput event) {
        if (RotationManager.active && RotationManager.rotations != null) {
            final float yaw = RotationManager.rotations.x;
            MoveUtil.fixMovement(event, yaw);
        }
    }
}
