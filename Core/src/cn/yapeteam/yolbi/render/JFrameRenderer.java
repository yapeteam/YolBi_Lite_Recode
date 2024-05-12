package cn.yapeteam.yolbi.render;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoop;
import cn.yapeteam.yolbi.event.impl.render.EventExternalRender;
import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.*;

public class JFrameRenderer extends JFrame {
    private final TransparentPanel transparentPanel = new TransparentPanel();

    public JFrameRenderer() {
        super("External Window");
        setUndecorated(true);
        setLocation(0, 0);
        setResizable(false);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        transparentPanel.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        add(transparentPanel);
        setAlwaysOnTop(true);
        AWTUtilities.setWindowOpaque(this, false);
        AWTUtilities.setWindowShape(this, null);
    }

    public void setTransparent(boolean transparent, String windowTitle) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        if (transparent)
            wl |= WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        else
            wl &= ~(WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT);
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }

    public void display() {
        YolBi.instance.getEventManager().register(this);
        setVisible(true);
        setTransparent(true, getTitle());
    }

    public void close() {
        YolBi.instance.getEventManager().unregister(this);
        setVisible(false);
    }

    static class TransparentPanel extends JPanel {
        public TransparentPanel() {
            setOpaque(false);
        }


        @Override
        protected void paintComponent(Graphics g) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            YolBi.instance.getEventManager().post(new EventExternalRender(g));
        }
    }

    private long count = 0;

    @Listener
    private void onUpdate(EventLoop e) {
        if (count % 7 == 0)
            SwingUtilities.invokeLater(transparentPanel::repaint);
        count++;
    }
}
