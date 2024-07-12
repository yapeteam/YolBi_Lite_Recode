package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.managers.BotManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Check Mode", "Hypixel", "Hypixel");

    public AntiBot() {
        super("AntiBot", ModuleCategory.COMBAT);
        addValues(mode);
    }

    @Listener
    private void onUpdate(EventUpdate event) {
        if (mode.is("Hypixel")) {
            for (int i = 0; i < mc.world.getLoadedEntityList().size(); i++) {
                Entity entity = mc.world.getLoadedEntityList().get(i);

                if (!(entity instanceof EntityPlayer)) continue;

                //noinspection UnnecessaryUnicodeEscape
                if (entity.getName().contains("\u00A7") || (entity.hasCustomName() && entity.getCustomNameTag().contains(entity.getName())) || (entity.getName().equals(mc.player.getName()) && entity != mc.player)) {
                    BotManager.addBot(entity);
                    mc.world.removeEntity(entity);
                }
            }
        }
    }
}
