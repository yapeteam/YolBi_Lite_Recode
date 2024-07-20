package cn.yapeteam.yolbi;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.command.CommandManager;
import cn.yapeteam.yolbi.config.ConfigManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.managers.BotManager;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.mcef.MCEFInitializer;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationManager;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.render.ExternalFrame;
import cn.yapeteam.yolbi.server.WebServer;
import cn.yapeteam.yolbi.shader.Shader;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import lombok.Getter;
import net.montoyo.mcef.MCEF;

import java.io.File;
import java.io.IOException;
import cn.yapeteam.yolbi.module.impl.misc.IRC;
//import cn.yapeteam.yolbi.event.impl.player.IRCListener;
import cn.yapeteam.yolbi.YolBi;

@Getter
public class YolBi {
    public static YolBi instance = new YolBi();
    public static final String name = "YolBi Lite";
    public static final String version = "0.3.6";
    public static final File YOLBI_DIR = new File(System.getProperty("user.home"), ".yolbi");
    public static boolean initialized = false;
    private EventManager eventManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private FontManager fontManager;
    private NotificationManager notificationManager;
    private BotManager botManager;
    private ExternalFrame jFrameRenderer;
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
        instance.jFrameRenderer = new ExternalFrame(0, 0, 0, 0);
        instance.botManager = new BotManager();
        instance.targetManager = new TargetManager();
        instance.notificationManager = new NotificationManager();
        instance.eventManager.register(instance.commandManager);
        instance.eventManager.register(instance.moduleManager);
        instance.eventManager.register(instance.botManager);
        instance.eventManager.register(instance.targetManager);
        instance.eventManager.register(Shader.class);
        instance.eventManager.register(ESPUtil.class);
        instance.eventManager.register(RotationManager.class);
        instance.eventManager.register(MCEFInitializer.class);
        instance.moduleManager.load();
        try {
            instance.getConfigManager().load();
            WebServer.start();
        } catch (Throwable e) {
            Logger.exception(e);
        }


//        YolBi instance = YolBi.getInstance();
//
//        // 初始化IRC模块
//        IRCModule ircModule = new IRCModule();
//        instance.getModuleManager().addModule(ircModule);
//
//        // 注册IRC事件监听器
//        IRCListener ircListener = new IRCListener();
//        instance.getEventBus().register(ircListener);



        instance.getNotificationManager().post(
                new Notification(
                        "Injected Yolbi successfully",
                        Easing.EASE_IN_OUT_QUAD,
                        Easing.EASE_IN_OUT_QUAD,
                        15000, NotificationType.INIT
                )
        );
    }

    public void shutdown() {
        try {
            Logger.info("Shutting down Yolbi Lite");
            instance.jFrameRenderer.close();
            configManager.save();
            WebServer.stop();
            MCEF.onMinecraftShutdown();
            instance = new YolBi();
            System.gc();
        } catch (IOException e) {
            Logger.exception(e);
        }
    }
}
