package cn.yapeteam.yolbi.utils.player;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlockUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean isValidBlock(BlockPos pos) {
        return isValidBlock(mc.world.getBlockState(pos).getBlock(), false);
    }
    public static boolean isValidBock(final BlockPos blockPos) {
        final Block block = Minecraft.getMinecraft().world.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    public static Block getBlockAtPos(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlock();
    }

    public static boolean isValidBlock(Block block, boolean placing) {
        if (block instanceof BlockCarpet
                || block instanceof BlockSnow
                || block instanceof BlockContainer
                || block instanceof BlockBasePressurePlate
                || block.getMaterial(null).isLiquid()) {
            return false;
        }
        if (placing && (block instanceof BlockSlab
                || block instanceof BlockStairs
                || block instanceof BlockLadder
                || block instanceof BlockStainedGlassPane
                || block instanceof BlockWall
                || block instanceof BlockWeb
                || block instanceof BlockCactus
                || block instanceof BlockFalling
                || block == Blocks.GLASS_PANE
                || block == Blocks.IRON_BARS)) {
            return false;
        }
        return (block.getMaterial(null).isSolid() || !block.isTranslucent(null) || block.isFullBlock(null));
    }

    public static boolean isInLiquid() {
        if (mc.player == null) return false;
        for (int x = MathHelper.floor(mc.player.getEntityBoundingBox().minX); x < MathHelper.floor(mc.player.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor(mc.player.getEntityBoundingBox().minZ); z < MathHelper.floor(mc.player.getEntityBoundingBox().maxZ) + 1; z++) {
                BlockPos pos = new BlockPos(x, (int) mc.player.getEntityBoundingBox().minY, z);
                Block block = mc.world.getBlockState(pos).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        return false;
    }

    public static boolean isOnLiquid() {
        if (mc.player == null) return false;
        AxisAlignedBB boundingBox = mc.player.getEntityBoundingBox();
        if (boundingBox != null) {
            boundingBox = boundingBox.expand(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
            boolean onLiquid = false;
            int y = (int) boundingBox.minY;

            for (int x = MathHelper.floor(boundingBox.minX); x < MathHelper.floor(boundingBox.maxX + 1.0D); ++x) {
                for (int z = MathHelper.floor(boundingBox.minZ); z < MathHelper.floor(boundingBox.maxZ + 1.0D); ++z) {
                    Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != Blocks.AIR) {
                        if (!(block instanceof BlockLiquid)) return false;
                        onLiquid = true;
                    }
                }
            }

            return onLiquid;
        }
        return false;
    }

}
