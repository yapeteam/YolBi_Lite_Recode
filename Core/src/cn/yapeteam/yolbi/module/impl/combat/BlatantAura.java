package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.module.Module;

@Deprecated
@ModuleInfo(name = "BlatantAura", category = ModuleCategory.COMBAT)
public class BlatantAura extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Single", "Single", "Switch");
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 17.0, 1.0, 20.0, 1.0),
    cpsReach = new NumberValue<>("CPS Reach", 3.0, 1.0, 5.0, 1.0),
    hurttime = new NumberValue<>("Hurt Time", 10.0, 1.0, 10.0, 1.0),
    switchdelay = new NumberValue<>("Switch Delay", 500.0, 100.0, 1000.0, 100.0);
}
