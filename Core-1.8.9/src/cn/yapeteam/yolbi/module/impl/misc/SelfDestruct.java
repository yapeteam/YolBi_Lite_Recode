package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;

import java.io.IOException;

public class SelfDestruct extends Module {
    public SelfDestruct() {
        super("SelfDestruct", ModuleCategory.MISC);
    }

    public void onEnable() {
        try {
            enabled = false;
            if (mc.currentScreen != null) mc.displayGuiScreen(null);
            MixinManager.destroyClient();
            YolBi.instance.shutdown();
        } catch (IOException e) {
            YolBi.instance.getNotificationManager().post(
                    new Notification(
                            "SelfDestruct Failed",
                            Easing.EASE_IN_OUT_QUAD,
                            Easing.EASE_IN_OUT_QUAD,
                            2500, NotificationType.FAILED
                    )
            );
        }
    }
}
