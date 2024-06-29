package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;

@ModuleInfo(name = "HUD", category = ModuleCategory.VISUAL)
public class HeadUpDisplay extends Module {
    @Listener
    private void onRender2D(EventRender2D event) {
        YolBi.instance.getFontManager().getPingFang18().drawString("YolBi Lite", 10, 10, 0xFFFFFF);
    }
}
