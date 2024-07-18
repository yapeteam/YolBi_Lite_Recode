package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "Noslow", category = ModuleCategory.MOVEMENT)
public class Noslow extends Module {
    private boolean throwingItem;
    //private boolean grim;

    private final BooleanValue autoThrow = new BooleanValue("C08", true);

    {
        addValues(autoThrow);
    }

    private final BooleanValue grimValue = new BooleanValue("Grim Mode" , false);
    {
        addValues(grimValue);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (autoThrow.getValue()) {
            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
                if (!throwingItem) {
//                    if(grimValue.getValue()) {
//                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
//                                C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                    }
//                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
//                            C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
                            mc.thePlayer.inventory.getCurrentItem()));
//                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
//                            C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    if(grimValue.getValue()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    throwingItem = true;
                }
                //if(grim)
            } else {
                throwingItem = false;
            }
        }
    }
}
