package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMouseOver;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;

public class Reach extends Module {

    private final NumberValue<Double> range = new NumberValue<>("Range", 3.0, 3.0, 6.0, 0.1);

    public Reach() {
        super("Reach", ModuleCategory.COMBAT);
        addValues(range);
    }

    @Listener
    public void onMouseOver(EventMouseOver event) {
        event.setReach(range.getValue().floatValue());
    }
}
