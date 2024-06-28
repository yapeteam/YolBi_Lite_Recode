package cn.yapeteam.yolbi.render;

import java.awt.*;
import java.util.List;

public interface Drawable {
    List<DrawableListener> getDrawableListeners();

    interface DrawableListener {
        void onDrawableUpdate(Graphics g);
    }
}