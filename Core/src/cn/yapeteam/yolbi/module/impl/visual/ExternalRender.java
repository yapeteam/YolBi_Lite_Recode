package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import lombok.Getter;

@Getter
@ModuleInfo(name = "ExternalRender", category = ModuleCategory.VISUAL)
public class ExternalRender extends Module {
    private final BooleanValue limitFps = new BooleanValue("Limit FPS", true);
    private final NumberValue<Integer> fps = new NumberValue<>("FPS", limitFps::getValue, 30, 1, 120, 1);

    public ExternalRender() {
        addValues(limitFps, fps);
    }

    @Override
    protected void onEnable() {
        YolBi.instance.getJFrameRenderer().display();
    }

    @Override
    protected void onDisable() {
        YolBi.instance.getJFrameRenderer().close();
    }
}
