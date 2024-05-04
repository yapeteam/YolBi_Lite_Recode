package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;

import java.io.IOException;

@ModuleInfo(name = "SelfDestruct", category = ModuleCategory.MISC)
public class SelfDestruct extends Module {
    public SelfDestruct() {
    }
    public void onEnable() {
        try{

            MixinManager.destroyClient();
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

        this.setEnabled(false);
    }
}
