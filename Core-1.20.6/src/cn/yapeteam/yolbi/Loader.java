package cn.yapeteam.yolbi;

import cn.yapeteam.loader.*;
import cn.yapeteam.loader.logger.Logger;
import net.minecraft.client.Minecraft;

import java.awt.*;

@SuppressWarnings("unused")
public class Loader {
    public static void start() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            if (BootStrap.getVersion().first != Version.V1_20_6) {
                Logger.error("Unsupported Minecraft version: {}", BootStrap.getVersion().first.getVersion());
                SocketSender.send("CLOSE");
                return;
            }
            SocketSender.send("CLOSE");
            SocketSender.close();
            Minecraft.getInstance().stop();
            // YolBi.initialize();
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
