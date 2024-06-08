package cn.yapeteam.yolbi.render;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.render.EventExternalRender;
import cn.yapeteam.yolbi.module.impl.visual.ExternalRender;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class JFrameRenderer extends JFrame {
    private final TransparentPanel transparentPanel = new TransparentPanel();

    public JFrameRenderer(int x, int y, int width, int height) {
        super("External Window");
        setUndecorated(true);
        setPosition(x, y);
        setFrameSize(width, height);
        setResizable(false);
        transparentPanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setBackground(new Color(0, 0, 0, 0));
        add(transparentPanel);
        setAlwaysOnTop(true);
    }

    public void setPosition(int x, int y) {
        setLocation(x, y);
    }

    public void setFrameSize(int width, int height) {
        Dimension size = new Dimension(width, height);
        setSize(size);
        transparentPanel.setPreferredSize(size);
    }

    public void display() {
        externalRenderModule = YolBi.instance.getModuleManager().getModule(ExternalRender.class);
        YolBi.instance.getEventManager().register(this);
        setVisible(true);
        Natives.SetWindowsTransparent(true, getTitle());
    }

    public void close() {
        YolBi.instance.getEventManager().unregister(this);
        setVisible(false);
    }

    @Getter
    private final CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
    private ExternalRender externalRenderModule;

    class TransparentPanel extends JPanel {
        public TransparentPanel() {
            setOpaque(false);
            new Thread(() -> {
                while (true) {
                    //noinspection ConstantValue
                    if (transparentPanel == null || !isVisible()) continue;
                    long time = System.currentTimeMillis();
                    transparentPanel.repaint();
                    try {
                        if (externalRenderModule != null)
                            Thread.sleep(Math.max(0, 1000 / externalRenderModule.getFps().getValue() - (System.currentTimeMillis() - time)));
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
            new Timer(1000 / 10, e -> {
                int titleBarHeight = 30;
                ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
                int scaleFactor = scaledResolution.getScaleFactor();
                setPosition(Display.getX() + 6, Display.getY() + titleBarHeight);
                setFrameSize(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
            }).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            YolBi.instance.getEventManager().post(new EventExternalRender(g));
            if (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof GuiChat)
                drawables.forEach(drawable -> drawable.getDrawableListeners().forEach(action -> action.onDrawableUpdate(g)));
        }
    }
}
