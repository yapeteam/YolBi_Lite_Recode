package cn.yapeteam.yolbi.ui.webui;

import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.annotations.Super;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IDisplayHandler;
import net.montoyo.mcef.api.IJSQueryHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@Getter
public abstract class WebScreen extends GuiScreen
        implements IDisplayHandler, IJSQueryHandler {
    private IBrowser browser = null;
    private final String root;

    public WebScreen(String root) {
        this.root = root;
        MCEF.PROXY.registerDisplayHandler(this);
        MCEF.PROXY.registerJSQueryHandler(this);
    }

    @Shadow
    public int width, height;
    private final Minecraft mc = Minecraft.getMinecraft();

    @Super
    @Override
    public void initGui() {
        if (browser == null) {
            //Create a browser and resize it to fit the screen
            browser = MCEF.PROXY.createBrowser("yolbi://" + root + "/index.html", true);
        }
        //Resize the browser if window size changed
        if (browser != null)
            browser.resize(mc.displayWidth, mc.displayHeight);
        //Create GUI
        Keyboard.enableRepeatEvents(true);
    }

    @Super
    @Override
    public void drawScreen(int i1, int i2, float f) {
        //Renders the browser if itsn't null
        if (browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            browser.draw(.0d, height, width, .0d); //Don't forget to flip Y axis.
            GlStateManager.enableDepth();
        }
    }

    @Super
    @Override
    public void handleInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                mc.displayGuiScreen(null);
                return;
            }

            boolean pressed = Keyboard.getEventKeyState();
            char key = Keyboard.getEventCharacter();
            int num = Keyboard.getEventKey();

            if (browser != null) { //Inject events into browser
                if (pressed)
                    browser.injectKeyPressedByKeyCode(num, key, 0);
                else
                    browser.injectKeyReleasedByKeyCode(num, key, 0);

                if (key != 0)
                    browser.injectKeyTyped(key, 0);
            }
        }

        while (Mouse.next()) {
            int btn = Mouse.getEventButton();
            boolean pressed = Mouse.getEventButtonState();
            int sx = Mouse.getEventX();
            int sy = Mouse.getEventY();
            int wheel = Mouse.getEventDWheel();

            if (browser != null) { //Inject events into browser.
                int y = mc.displayHeight - sy; //Don't forget to flip Y axis.

                if (wheel != 0)
                    browser.injectMouseWheel(sx, y, 0, 1, wheel);
                else if (btn == -1)
                    browser.injectMouseMove(sx, y, 0, y < 0);
                else
                    browser.injectMouseButton(sx, y, 0, btn + 1, pressed, 1);
            }
        }
    }

    @Super
    @Override
    public void onGuiClosed() {
        //Make sure to close the browser when you don't need it anymore.
        if (browser != null) {
            browser.close();
            browser = null;
        }

        Keyboard.enableRepeatEvents(false);
    }

    @Super
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
