package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class ESP extends Module {
    private ClientTheme theme;

    public ESP() {
        super("ESP", ModuleCategory.VISUAL);
    }

    @Override
    protected void onEnable() {
        theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
    }

    @Listener
    private void onRender3D(EventRender3D event) {
        if (mc.world != null)
            for (Entity entity : mc.world.loadedEntityList)
                if (entity != mc.player && entity instanceof EntityLivingBase)
                    RenderUtil.drawEntityBox((EntityLivingBase) entity, new Color(theme.getColor(0)), true, true, 1, event.getPartialTicks());
    }
}
