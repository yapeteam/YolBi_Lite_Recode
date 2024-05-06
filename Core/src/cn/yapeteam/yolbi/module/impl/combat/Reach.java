package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMouseOver;
import cn.yapeteam.yolbi.module.Module;

@ModuleInfo(name = "Reach", category = ModuleCategory.COMBAT)
public class Reach extends Module {

    private NumberValue<Double> range = new NumberValue<>("Range", 3.0, 3.0, 6.0, 0.1);

    public Reach() {
        addValues(range);
    }

    @Listener
    public void onMouseOver(EventMouseOver event) {
        Logger.info("Mouse over");
        event.setReach(range.getValue().floatValue());
    }
}
