package cn.yapeteam.yolbi.module.impl.misc;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.misc.ObjectStore;

public class AntiInvisible extends Module {
    public AntiInvisible() {
        super("AntiInvisible", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() {
        ObjectStore.objects.put("AntiInvisible", true);
    }

    @Override
    protected void onDisable() {
        ObjectStore.objects.put("AntiInvisible", false);
    }
}
