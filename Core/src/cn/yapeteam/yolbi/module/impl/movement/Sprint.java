package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;

@Deprecated // 死亡 想不出来不使用mc api还不和电脑快捷键冲突的方法
@ModuleInfo(name = "Sprint", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    private boolean unPressed;

    @Listener
    private void onStrafe(EventStrafe event) {
        if (PlayerUtil.isMoving()) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LCONTROL, false);
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LCONTROL, true);
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LCONTROL, false);
            unPressed = true;
        } else if (unPressed) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LCONTROL, false);
            unPressed = false;
        }
    }
}
