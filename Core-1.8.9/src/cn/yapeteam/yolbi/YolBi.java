package cn.yapeteam.yolbi;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.command.CommandManager;
import cn.yapeteam.yolbi.config.ConfigManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.managers.BotManager;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationManager;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.render.JFrameRenderer;
import cn.yapeteam.yolbi.server.WebServer;
import cn.yapeteam.yolbi.shader.Shader;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
public class YolBi {
    public static YolBi instance = new YolBi();
    public static final String name = "YolBi Lite";
    public static final String version = "0.3.4";
    public static final File YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi");
    public static boolean initialized = false;
    private EventManager eventManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private FontManager fontManager;
    private NotificationManager notificationManager;
    private BotManager botManager;
    private JFrameRenderer jFrameRenderer;
    private TargetManager targetManager;

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
        if (initialized || instance == null) return;
        initialized = true;
        boolean ignored = YOLBI_DIR.mkdirs();
        System.setProperty("sun.java2d.opengl", "true");
        instance.eventManager = new EventManager();
        instance.commandManager = new CommandManager();
        instance.configManager = new ConfigManager();
        instance.moduleManager = new ModuleManager();
        instance.jFrameRenderer = new JFrameRenderer(0, 0, 0, 0);
        instance.botManager = new BotManager();
        instance.targetManager = new TargetManager();
        instance.notificationManager = new NotificationManager();
        instance.eventManager.register(instance.commandManager);
        instance.eventManager.register(instance.moduleManager);
        instance.eventManager.register(instance.botManager);
        instance.eventManager.register(instance.targetManager);
        instance.eventManager.register(Shader.class);
        instance.eventManager.register(ESPUtil.class);
        instance.eventManager.register(YolBi.class);
        instance.eventManager.register(RotationManager.class);
        instance.moduleManager.load();
        try {
            instance.getConfigManager().load();
            WebServer.start();
        } catch (Throwable e) {
            Logger.exception(e);
        }
        instance.getNotificationManager().post(
                new Notification(
                        "Injected successfully",
                        Easing.EASE_IN_OUT_QUAD,
                        Easing.EASE_IN_OUT_QUAD,
                        2500, NotificationType.INIT
                )
        );
    }

    @Listener
    private static void onTick(EventTick e) {
        if (!Natives.initialized) Natives.Init();
    }

    public void shutdown() {
        try {
            instance.jFrameRenderer.close();
            configManager.save();
            WebServer.stop();
            instance = new YolBi();
            System.gc();
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}