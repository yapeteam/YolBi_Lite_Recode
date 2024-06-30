package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@ModuleInfo(name = "Noslow", category = ModuleCategory.MOVEMENT)
public class Noslow extends Module {
    private boolean throwingItem;
    private final BooleanValue autoThrow = new BooleanValue("C08", true);

    {
        addValues(autoThrow);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (autoThrow.getValue()) {
            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
                if (!throwingItem) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
                            mc.thePlayer.inventory.getCurrentItem()));

                    throwingItem = true;
                }
            } else {
                throwingItem = false;
            }
        }
    }
}
