package cn.yapeteam.yolbi.render;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.render.EventExternalRender;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class ExternalFrame extends JFrame {
    @Getter
    private final TransparentPanel transparentPanel = new TransparentPanel();

    public ExternalFrame(int x, int y, int width, int height) {
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
        JFrameRendererModule = YolBi.instance.getModuleManager().getModule(cn.yapeteam.yolbi.module.impl.visual.JFrameRenderer.class);
        YolBi.instance.getEventManager().register(this);
        setVisible(true);
        Natives.SetWindowsTransparent(true, getTitle());
        transparentPanel.start();
    }

    public void close() {
        YolBi.instance.getEventManager().unregister(this);
        setVisible(false);
        transparentPanel.stop();
    }

    @Getter
    private final CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<>();
    private cn.yapeteam.yolbi.module.impl.visual.JFrameRenderer JFrameRendererModule;

    @Getter
    public class TransparentPanel extends JPanel {
        private int fps = 0;
        private long lastTime = 0;
        private int fpsCount = 0;
        private CountDownLatch latch;
        private final Runnable repaintTask = () -> {
            if (transparentPanel != null && isVisible()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime >= 1000) {
                    fps = fpsCount;
                    fpsCount = 0;
                    lastTime = currentTime;
                }
                latch = new CountDownLatch(1);
                transparentPanel.repaint();
                try {
                    latch.await();
                    if (!JFrameRendererModule.getLimitFps().getValue()) return;
                    long waitTime = 1000 / JFrameRendererModule.getFps().getValue() - (System.currentTimeMillis() - currentTime);
                    if (waitTime > 0)
                        Thread.sleep(waitTime);
                } catch (InterruptedException ignored) {
                }
            }
        };

        private Thread repaintThread;

        public TransparentPanel() {
            setOpaque(false);
            new Timer(1000 / 10, e -> {
                int titleBarHeight = 30;
                ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
                int scaleFactor = scaledResolution.getScaleFactor();
                setPosition(Display.getX() + 6, Display.getY() + titleBarHeight);
                setFrameSize(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
            }).start();
        }

        public void start() {
            if (repaintThread != null && repaintThread.isAlive()) return;
            repaintThread = new Thread(() -> {
                while (true) repaintTask.run();
            });
            repaintThread.start();
        }

        public void stop() {
            if (repaintThread != null && repaintThread.isAlive()) repaintThread.interrupt();
        }

        @Override
        protected void paintComponent(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            YolBi.instance.getEventManager().post(new EventExternalRender(g));
            if (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof GuiChat)
                drawables.forEach(drawable -> drawable.getDrawableListeners().forEach(action -> action.onDrawableUpdate(g)));
            fpsCount++;
            if (latch != null)
                latch.countDown();
        }
    }
}
