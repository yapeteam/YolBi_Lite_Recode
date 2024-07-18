package cn.yapeteam.yolbi.mcef;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.event.impl.game.EventLoop;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.ui.browser.BrowserHandler;
import net.minecraft.client.Minecraft;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.client.ClientProxy;
import org.cef.browser.CefBrowserFactory;
import org.lwjgl.input.Keyboard;

public class MCEFListener {
    private static boolean mcefInited = false;

    @Listener
    private static void onTick(EventTick e) {
        if (!mcefInited) {
            mcefInited = true;
            CefBrowserFactory.Renderer = ImplCefRenderer.class;
            MCEF.INSTANCE.onInit(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().replaceAll("\\\\", "/"));
        }
    }

    @Listener
    private static void onUpdate(EventLoop e) {
        if (mcefInited) {
            ((ClientProxy) MCEF.PROXY).update();
        }
    }

    @Listener
    private static void onKey(EventKey e) {
        if (e.getKey() == Keyboard.KEY_F10)
            BrowserHandler.INSTANCE.display();
    }
}
