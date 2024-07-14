package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;

import java.io.IOException;

public class SelfDestruct extends Module {
    public SelfDestruct() {
        super("SelfDestruct", ModuleCategory.MISC);
    }

    public void onEnable() {
        try {
            enabled = false;
            if (mc.screen != null) mc.setScreen(null);
            MixinManager.destroyClient();
            YolBi.instance.shutdown();
        } catch (IOException ignored) {
        }
    }
}
