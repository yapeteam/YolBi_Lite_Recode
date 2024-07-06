package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;

@Deprecated
public class BlatantAura extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Single", "Single", "Switch");
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 17.0, 1.0, 20.0, 1.0),
            cpsReach = new NumberValue<>("CPS Reach", 3.0, 1.0, 5.0, 1.0),
            hurttime = new NumberValue<>("Hurt Time", 10.0, 1.0, 10.0, 1.0),
            switchdelay = new NumberValue<>("Switch Delay", 500.0, 100.0, 1000.0, 100.0);

    protected BlatantAura(String name, ModuleCategory category) {
        super(name, category);
    }
}
