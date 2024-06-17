package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "Eagle", category = ModuleCategory.MOVEMENT)
public class Eagle extends Module {

    private final BooleanValue onlyblocks = new BooleanValue("Only Blocks", true);

    private final BooleanValue onlybackwards = new BooleanValue("Only Backwards", true);

    private final BooleanValue onlyground = new BooleanValue("Only Ground", false);

    public Eagle() {
        addValues(onlyblocks, onlybackwards);
    }


    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return this.getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) return;
        if ((mc.thePlayer.getHeldItem() == null || (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) &&
                onlyblocks.getValue()) {
            return;
        }
        if ((mc.thePlayer.onGround || !onlyground.getValue()) &&
                (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) &&
                !(!mc.gameSettings.keyBindBack.isKeyDown() && onlybackwards.getValue())) {
            Natives.SetKeyBoard(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else if (!(PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir)) {
            Natives.SetKeyBoard(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;
        mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Natives.SetKeyBoard(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}
