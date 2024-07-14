package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.module.impl.combat.CombatSettings;
import cn.yapeteam.yolbi.utils.IMinecraft;
import com.google.common.base.Predicates;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@UtilityClass
public class PlayerUtil implements IMinecraft {
    private final HashMap<Integer, Integer> GOOD_POTIONS = new HashMap<Integer, Integer>() {{
        put(6, 1); // Instant Health
        put(10, 2); // Regeneration
        put(11, 3); // Resistance
        put(21, 4); // Health Boost
        put(22, 5); // Absorption
        put(23, 6); // Saturation
        put(5, 7); // Strength
        put(1, 8); // Speed
        put(12, 9); // Fire Resistance
        put(14, 10); // Invisibility
        put(3, 11); // Haste
        put(13, 12); // Water Breathing
    }};

    CombatSettings settings = new CombatSettings();

    private int getPing(Entity entity) {
        val uniqueID = mc.getNetHandler().getPlayerInfo(entity.getUniqueID());
        return uniqueID != null ? uniqueID.getResponseTime() : 0;
    }

    public static double fovFromEntity(Entity en) {
        return ((((double) (mc.thePlayer.rotationYaw - fovToEntity(en)) % 360.0D) + 540.0D) % 360.0D) - 180.0D;
    }


    public static float fovToEntity(Entity ent) {
        double x = ent.posX - mc.thePlayer.posX;
        double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    public double direction() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static double PitchFromEntity(Entity en, float f) {
        return (double) (mc.thePlayer.rotationPitch - pitchToEntity(en, f));
    }

    public static float pitchToEntity(Entity ent, float f) {
        double x = mc.thePlayer.getDistanceToEntity(ent);
        double y = mc.thePlayer.posY - (ent.posY + f);
        double pitch = (((Math.atan2(x, y) * 180.0D) / 3.141592653589793D));
        return (float) (90 - pitch);
    }

    /**
     * Gets the block at a position
     *
     * @return block
     */
    public Block block(final double x, final double y, final double z) {
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    /**
     * Gets the block at a position
     *
     * @return block
     */
    public Block block(final BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    /**
     * Gets the distance between 2 positions
     *
     * @return distance
     */
    public double distance(final BlockPos pos1, final BlockPos pos2) {
        final double x = pos1.getX() - pos2.getX();
        final double y = pos1.getY() - pos2.getY();
        final double z = pos1.getZ() - pos2.getZ();
        return x * x + y * y + z * z;
    }

    /**
     * Gets the block relative to the player from the offset
     *
     * @return block relative to the player
     */
    public Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }


    /**
     * Checks if another players' team is the same as the players' team
     *
     * @return same team
     */
    public boolean sameTeam(final EntityLivingBase player) {
        if (player.getTeam() != null && mc.thePlayer.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }


    public EnumFacingOffset getEnumFacing(final Vec3 position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(PlayerUtil.block(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return new EnumFacingOffset(EnumFacing.WEST, new Vec3(x2, 0, 0));
                } else {
                    return new EnumFacingOffset(EnumFacing.EAST, new Vec3(x2, 0, 0));
                }
            }
        }

        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir)) {
                if (y2 < 0) {
                    return new EnumFacingOffset(EnumFacing.UP, new Vec3(0, y2, 0));
                }
            }
        }

        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return new EnumFacingOffset(EnumFacing.SOUTH, new Vec3(0, 0, z2));
                } else {
                    return new EnumFacingOffset(EnumFacing.NORTH, new Vec3(0, 0, z2));
                }
            }
        }

        return null;
    }

    public static double calculateHorizontalAngleDifference(Entity en) {
        return ((double) (mc.thePlayer.rotationYaw - getYaw(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
    }

    public static float getYaw(Entity ent) {
        double x = ent.posX - mc.thePlayer.posX;
        double z = ent.posZ - mc.thePlayer.posZ;
        double yaw = Math.atan2(x, z) * 57.29577951308232;
        return (float) (yaw * -1.0D);
    }

    public static boolean holdingWeapon() {
        if (mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        Item getItem = mc.thePlayer.getHeldItem().getItem();
        return getItem instanceof ItemSword || (settings.getAxe().getValue() && getItem instanceof ItemAxe) || (settings.getRod().getValue() && getItem instanceof ItemFishingRod) || (settings.getStick().getValue() && getItem == Items.stick);
    }

    /**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    public static Entity getMouseOver(final float partialTicks, final double Reach) {
        Entity pointedEntity = null;
        final Entity entity = mc.getRenderViewEntity();

        if (entity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            pointedEntity = null;
            double blockReachDistance = Reach;
            mc.objectMouseOver = entity.rayTrace(blockReachDistance, partialTicks);
            double distance = blockReachDistance;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);

            if (mc.objectMouseOver != null) {
                distance = mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = entity.getLook(partialTicks);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
            pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = distance;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    pointedEntity = entity1;
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        boolean flag1 = false;

                        if (!flag1 && entity1 == entity.ridingEntity) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            mc.mcProfiler.endSection();
            return pointedEntity;
        }
        return null;
    }


    /**
     * Checks if there is a block under the player
     *
     * @return block under
     */
    public boolean isBlockUnder(final double height) {
        return isBlockUnder(height, true);
    }

    public boolean isBlockUnder(final double height, final boolean boundingBox) {
        if (boundingBox) {
            for (int offset = 0; offset < height; offset += 2) {
                final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }
            }
        } else {
            for (int offset = 0; offset < height; offset++) {
                if (PlayerUtil.blockRelativeToPlayer(0, -offset, 0).isFullBlock()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBlockUnder() {
        return isBlockUnder(mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
    }

    /**
     * Checks if a potion is good
     *
     * @return good potion
     */
    public boolean goodPotion(final int id) {
        return GOOD_POTIONS.containsKey(id);
    }

    /**
     * Gets a potions ranking
     *
     * @return potion ranking
     */
    public int potionRanking(final int id) {
        return GOOD_POTIONS.getOrDefault(id, -1);
    }

    /**
     * Checks if the player is in a liquid
     *
     * @return in liquid
     */
    public boolean inLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    /**
     * Fake damages the player
     */


    /**
     * Checks if the player is near a block
     *
     * @return block near
     */
    public boolean blockNear(final int range) {
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    final Block block = blockRelativeToPlayer(x, y, z);

                    if (!(block instanceof BlockAir)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if the player is inside a block
     *
     * @return inside block
     */
    public boolean insideBlock() {
        if (mc.thePlayer.ticksExisted < 5) {
            return false;
        }

        final EntityPlayerSP player = PlayerUtil.mc.thePlayer;
        final WorldClient world = PlayerUtil.mc.theWorld;
        final AxisAlignedBB bb = player.getEntityBoundingBox();
        for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
                    final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)))) != null && player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sends a click to Minecraft legitimately
     */
    public void sendClick(final int button, final boolean state) {
        final int keyBind = button == 0 ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();

        KeyBinding.setKeyBindState(keyBind, state);

        if (state) {
            KeyBinding.onTick(keyBind);
        }
    }

    public static boolean onLiquid() {
        boolean onLiquid = false;
        final AxisAlignedBB playerBB = PlayerUtil.mc.thePlayer.getEntityBoundingBox();
        final WorldClient world = PlayerUtil.mc.theWorld;
        final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
        for (int x = MathHelper.floor_double(playerBB.minX); x < MathHelper.floor_double(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(playerBB.minZ); z < MathHelper.floor_double(playerBB.maxZ) + 1; ++z) {
                final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }


    // This methods purpose is to get block placement possibilities, blocks are 1 unit thick so please don't change it to 0.5 it causes bugs.
    public Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ) {
        final List<Vec3> possibilities = new ArrayList<>();
        final int range = (int) (5 + (Math.abs(offsetX) + Math.abs(offsetZ)));

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);

                    if (!(block instanceof BlockAir)) {
                        for (int x2 = -1; x2 <= 1; x2 += 2)
                            possibilities.add(new Vec3(mc.thePlayer.posX + x + x2, mc.thePlayer.posY + y, mc.thePlayer.posZ + z));

                        for (int y2 = -1; y2 <= 1; y2 += 2)
                            possibilities.add(new Vec3(mc.thePlayer.posX + x, mc.thePlayer.posY + y + y2, mc.thePlayer.posZ + z));

                        for (int z2 = -1; z2 <= 1; z2 += 2)
                            possibilities.add(new Vec3(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z + z2));
                    }
                }
            }
        }

        possibilities.removeIf(vec3 -> mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) > 5 || !(PlayerUtil.block(vec3.xCoord, vec3.yCoord, vec3.zCoord) instanceof BlockAir));

        if (possibilities.isEmpty()) return null;

        possibilities.sort(Comparator.comparingDouble(vec3 -> {

            final double d0 = (mc.thePlayer.posX + offsetX) - vec3.xCoord;
            final double d1 = (mc.thePlayer.posY - 1 + offsetY) - vec3.yCoord;
            final double d2 = (mc.thePlayer.posZ + offsetZ) - vec3.zCoord;
            return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

        }));

        return possibilities.get(0);
    }

    public Vec3 getPlacePossibility() {
        return getPlacePossibility(0, 0, 0);
    }

}