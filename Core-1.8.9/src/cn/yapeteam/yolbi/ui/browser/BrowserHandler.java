package cn.yapeteam.yolbi.ui.browser;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.montoyo.mcef.api.*;

/**
 * An example mod that shows you how to use MCEF.
 * Assuming that it is client-side only and that onInit() is called on initialization.
 * This example shows a simple 2D web browser when pressing F6.
 *
 * @author montoyo
 */
public class BrowserHandler implements IDisplayHandler, IJSQueryHandler {
    public static BrowserHandler INSTANCE;

    public ScreenCfg hudBrowser = null;
    private final Minecraft mc = Minecraft.getMinecraft();
    @Setter
    private BrowserScreen backup = null;
    private API api;

    public API getAPI() {
        return api;
    }

    public void onInit() {
        INSTANCE = this;
        //Grab the API and make sure it isn't null.
        api = MCEFApi.getAPI();
        //Register this class to handle onAddressChange and onQuery events
        api.registerDisplayHandler(this);
        api.registerJSQueryHandler(this);
        YolBi.instance.getEventManager().register(this);
    }

    public boolean hasBackup() {
        return (backup != null);
    }

    public void showScreen(String url) {
        if (mc.currentScreen instanceof BrowserScreen)
            ((BrowserScreen) mc.currentScreen).loadURL(url);
        else if (hasBackup()) {
            mc.displayGuiScreen(backup);
            backup.loadURL(url);
            backup = null;
        } else
            mc.displayGuiScreen(new BrowserScreen(url));
    }

    public IBrowser getBrowser() {
        if (mc.currentScreen instanceof BrowserScreen)
            return ((BrowserScreen) mc.currentScreen).browser;
        else if (backup != null)
            return backup.browser;
        else
            return null;
    }

    public void display() {
        if (!(mc.currentScreen instanceof BrowserScreen)) {
            //Display the web browser UI.
            mc.displayGuiScreen(hasBackup() ? backup : new BrowserScreen());
            backup = null;
        }
    }

    @Override
    public void onAddressChange(IBrowser browser, String url) {
        //Called by MCEF if a browser's URL changes. Forward this event to the screen.
        if (mc.currentScreen instanceof BrowserScreen)
            ((BrowserScreen) mc.currentScreen).onUrlChanged(browser, url);
        else if (hasBackup())
            backup.onUrlChanged(browser, url);
    }

    @Override
    public void onTitleChange(IBrowser browser, String title) {
    }

    @Override
    public void onTooltip(IBrowser browser, String text) {
    }

    @Override
    public void onStatusMessage(IBrowser browser, String value) {
    }

    @Override
    public boolean handleQuery(IBrowser b, long queryId, String query, boolean persistent, IJSQueryCallback cb) {
        return false;
    }

    @Override
    public void cancelQuery(IBrowser b, long queryId) {
    }

    @Listener
    public void render2D(EventRender2D e) {
        if (hudBrowser != null)
            hudBrowser.drawScreen(0, 0, 0.f);
    }
}
