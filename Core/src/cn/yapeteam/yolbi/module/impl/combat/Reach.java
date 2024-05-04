package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.module.Module;

@ModuleInfo(name = "Reach", category = ModuleCategory.COMBAT)
public class Reach extends Module {
    public Reach() {
        addValues(new NumberValue<>("Range", 3.0, 3.0, 6.0, 0.1));
    }
}
