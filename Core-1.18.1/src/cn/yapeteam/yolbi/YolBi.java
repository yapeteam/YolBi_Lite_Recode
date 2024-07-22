package cn.yapeteam.yolbi;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.config.ConfigManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.server.WebServer;
import cn.yapeteam.yolbi.utils.font.FontManager;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class YolBi {
    public static YolBi instance = new YolBi();
    public static final String name = "YolBi Lite";
    public static final String version = "0.3.6";
    public static final File YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi");
    public static boolean initialized = false;
    private EventManager eventManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;

    public EventManager getEventManager() {
        if (eventManager == null)
            eventManager = new EventManager();
        return eventManager;
    }

    public static void initialize() {
        if (initialized || instance == null) return;
        initialized = true;
        boolean ignored = YOLBI_DIR.mkdirs();
        System.setProperty("sun.java2d.opengl", "true");
        instance.eventManager = new EventManager();
        instance.configManager = new ConfigManager();
        instance.moduleManager = new ModuleManager();
        instance.eventManager.register(instance.moduleManager);
        instance.moduleManager.load();
        FontManager.init();
        try {
            instance.getConfigManager().load();

            WebServer.start();
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }

    public void shutdown() {
        try {
            Logger.info("Shutting down Yolbi Lite");
            configManager.save();
            WebServer.stop();
            instance = new YolBi();
            //Logger.info("Shutting down Yolbi Lite");
            System.gc();
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}
