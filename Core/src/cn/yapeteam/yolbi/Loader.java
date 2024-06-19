package cn.yapeteam.yolbi;

import cn.yapeteam.loader.JVMTIWrapper;
import cn.yapeteam.loader.NativeWrapper;
import cn.yapeteam.loader.SocketSender;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.mixin.MixinManager;

import java.awt.*;

@SuppressWarnings("unused")
public class Loader {
    public static void start() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            Logger.info("Start Loading!");
            Logger.info("Initializing MixinLoader...");
            MixinManager.init();
            Logger.warn("Start transforming!");
            MixinManager.transform();
            Logger.success("Welcome {} ver {}", YolBi.name, YolBi.version);
            SocketSender.send("CLOSE");
            SocketSender.close();
            YolBi.initialize();
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
