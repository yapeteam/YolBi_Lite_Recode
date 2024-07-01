package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.block.EventBlockBB;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getBoundingBox(worldIn, pos);
    }

    @Overwrite(
            method = "addCollisionBoxToList",
            desc = "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V"
    )
    protected static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable AxisAlignedBB blockBox) {
        if (blockBox != Block.NULL_AABB) {
            IBlockState state = Minecraft.getMinecraft().world.getBlockState(pos);
            EventBlockBB eventBlockBB = new EventBlockBB(pos, state.getBlock(), state.getCollisionBoundingBox(Minecraft.getMinecraft().world, pos));
            YolBi.instance.getEventManager().post(eventBlockBB);
            AxisAlignedBB axisalignedbb = blockBox.offset(pos);
            if (entityBox.intersectsWith(axisalignedbb)) {
                collidingBoxes.add(axisalignedbb);
            }
        }
    }
}
