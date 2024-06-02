package cn.yapeteam.yolbi;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.command.CommandManager;
import cn.yapeteam.yolbi.config.ConfigManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.impl.visual.HeadUpDisplay;
import cn.yapeteam.yolbi.notification.NotificationManager;
import cn.yapeteam.yolbi.render.JFrameRenderer;
import cn.yapeteam.yolbi.server.WebServer;
import cn.yapeteam.yolbi.shader.Shader;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class YolBi {
    public static YolBi instance = new YolBi();
    public static final String name = "YolBi Lite";
    public static final String version = "0.3.0";
    public static final File YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi");
    private EventManager eventManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private FontManager fontManager;
    private NotificationManager notificationManager;
    private JFrameRenderer jFrameRenderer;

    public EventManager getEventManager() {
        if (eventManager == null)
            eventManager = new EventManager();
        return eventManager;
    }

    public FontManager getFontManager() {
        if (fontManager == null)
            fontManager = new FontManager();
        return fontManager;
    }

    public static void initialize() {
        if (YolBi.instance == null)
            YolBi.instance = new YolBi();
        boolean ignored = YOLBI_DIR.mkdirs();
        System.setProperty("sun.java2d.opengl", "true");
        instance.eventManager = new EventManager();
        instance.commandManager = new CommandManager();
        instance.configManager = new ConfigManager();
        instance.moduleManager = new ModuleManager();
        instance.notificationManager = new NotificationManager();
        instance.jFrameRenderer = new JFrameRenderer(0, 0, 0, 0);
        instance.eventManager.register(instance.commandManager);
        instance.eventManager.register(instance.moduleManager);
        instance.eventManager.register(instance.jFrameRenderer);
        instance.eventManager.register(Shader.class);
        instance.moduleManager.load();
        instance.moduleManager.getModule(HeadUpDisplay.class).setEnabled(true);
        YolBi.instance.getEventManager().register(ESPUtil.class);
        YolBi.instance.getEventManager().register(RotationManager.class);

        try {
            YolBi.instance.getConfigManager().load();
            WebServer.start();
            instance.jFrameRenderer.display();
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }

    public void shutdown() {
        try {
            configManager.save();
            WebServer.stop();
            instance.jFrameRenderer.close();
            YolBi.instance = new YolBi();
            System.gc();
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}
