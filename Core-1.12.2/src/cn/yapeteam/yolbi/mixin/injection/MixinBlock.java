package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.block.EventBlockBB;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author yuxiangll
 * @since 2024/1/7 21:23
 * IntelliJ IDEA
 */
@Mixin(Block.class)
public class MixinBlock {
    @Shadow
    protected BlockStateContainer blockState;

    @Shadow
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getBoundingBox(worldIn, pos);
    }

    @Overwrite(
            method = "addCollisionBoxToList",
            desc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V"
    )
    protected static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox) {
        if (blockBox != null) {
            AxisAlignedBB axisalignedbb = blockBox.offset(pos);

            WorldClient world = Minecraft.getMinecraft().world;

            if (world != null) {
                EventBlockBB eventBlockBB = new EventBlockBB(pos, world.getBlockState(pos).getBlock(), axisalignedbb);
                YolBi.instance.getEventManager().post(eventBlockBB);

                axisalignedbb = eventBlockBB.getAxisAlignedBB() == null ? null : eventBlockBB.getAxisAlignedBB();
            }

            if (axisalignedbb != null && entityBox.intersectsWith(axisalignedbb)) {
                collidingBoxes.add(axisalignedbb);
            }
        }
    }
}
