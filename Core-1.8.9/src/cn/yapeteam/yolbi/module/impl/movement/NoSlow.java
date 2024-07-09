package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Normal", "Normal", "Grim");
    private final BooleanValue autoThrow = new BooleanValue("C08", () -> mode.is("Normal") || mode.is("Grim"), true);
    private boolean throwingItem;

    public NoSlow() {
        super("NoSlow", ModuleCategory.MOVEMENT);
        addValues(mode, autoThrow);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (mode.is("Normal")) {
            if (autoThrow.getValue()) {
                if (onEatSlowDown()) {
                    if (!throwingItem) {
                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        throwingItem = true;
                    }
                } else {
                    throwingItem = false;
                }
            }
        } else if (mode.is("Grim")) {
            if (autoThrow.getValue()) {
                if (onEatSlowDown()) {
                    if (!throwingItem) {
                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        // 模拟丢弃一个物品
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        throwingItem = true;
                    }
                } else {
                    throwingItem = false;
                }
            }
        }
    }

    private boolean onEatSlowDown() {
        return mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood;
    }
}