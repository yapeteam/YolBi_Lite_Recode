package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;

@ModuleInfo(name = "Eagle", category = ModuleCategory.MOVEMENT)
public class Eagle extends Module {

    private final BooleanValue onlyblocks = new BooleanValue("Only Blocks", true);

    private final BooleanValue onlybackwards = new BooleanValue("Only Backwards", true);

    private final BooleanValue onlyground = new BooleanValue("Only Ground", false);

    public Eagle() {
        addValues(onlyblocks, onlybackwards, onlyground);
    }

    public Block getBlockUnderPlayer() {
        return PlayerUtil.blockRelativeToPlayer(0, -1, 0);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.currentScreen != null) {
            reset();
            return;
        }
        if (mc.player.isInWater() || mc.player.isInLava()) return;
        if ((mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && onlyblocks.getValue())
            return;
        if ((mc.player.onGround || !onlyground.getValue()) &&
                (getBlockUnderPlayer() instanceof BlockAir) &&
                (mc.gameSettings.keyBindBack.isKeyDown() || !onlybackwards.getValue())) {
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LSHIFT, true);
        } else if (!(getBlockUnderPlayer() instanceof BlockAir)) {
            reset();
        }
    }

    private void reset() {
        if (Natives.IsKeyDown(VirtualKeyBoard.VK_LSHIFT))
            Natives.SetKeyBoard(VirtualKeyBoard.VK_LSHIFT, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null) return;
        reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        reset();
        super.onDisable();
    }
}