package cn.yapeteam.yolbi.mcef;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.event.impl.game.EventLoop;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.ui.browser.BrowserHandler;
import cn.yapeteam.yolbi.ui.webui.impl.ClickUI;
import net.minecraft.client.Minecraft;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.client.ClientProxy;
import org.cef.browser.CefBrowserFactory;
import org.lwjgl.input.Keyboard;

public class MCEFInitializer {
    private static boolean initialized = false;

    @Listener
    private static void onTick(EventTick e) {
        if (!initialized) {
            initialized = true;
            CefBrowserFactory.Renderer = ImplCefRenderer.class;
            MCEF.PROXY.registerScheme("yolbi", YolBiScheme.class, true, false, false, true, true, false, false);
            MCEF.INSTANCE.onInit(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().replaceAll("\\\\", "/"));
            new BrowserHandler().onInit();
        }
    }

    @Listener
    private static void onUpdate(EventLoop e) {
        if (initialized) {
            ((ClientProxy) MCEF.PROXY).update();
        }
    }

    @Listener
    private static void onKey(EventKey e) {
        if (e.getKey() == Keyboard.KEY_F10) {
            Minecraft.getMinecraft().displayGuiScreen(ClickUI.instance);
            // BrowserHandler.INSTANCE.display();
        }
    }
}
