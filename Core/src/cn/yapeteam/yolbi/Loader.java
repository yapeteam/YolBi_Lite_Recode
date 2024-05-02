package cn.yapeteam.yolbi;

import cn.yapeteam.loader.SocketSender;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;

import java.awt.*;
import java.io.IOException;

@SuppressWarnings("unused")
public class Loader {
    public static void start() {
        try {
            Logger.info("Start Loading!");
            Logger.info("Initializing MixinLoader...");
            MixinManager.init();
            Logger.warn("Start transforming!");
            MixinManager.transform();
            Logger.success("Welcome {} ver {}", YolBi.name, YolBi.version);
            SocketSender.send("CLOSE");
            SocketSender.close();
            YolBi.initialize();
            new Thread(() -> {
                try {
                    YolBi.instance.getHttpSeverV3().start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            YolBi.instance.getNotificationManager().post(
                    new Notification(
                            "Injected successfully",
                            Easing.EASE_IN_OUT_QUAD,
                            Easing.EASE_IN_OUT_QUAD,
                            2500, NotificationType.INIT
                    )
            );
        } catch (Throwable e) {
            Logger.exception(e);
            try {
                Logger.writeCache();
                Desktop.getDesktop().open(Logger.getLog());
            } catch (Throwable ignored) {
            }
        }
    }
}
