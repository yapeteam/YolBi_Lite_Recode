package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.IMinecraft;
import com.google.common.base.Predicates;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import java.util.*;

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

    private int getPing(Entity entity) {
        val uniqueID = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID());
        return uniqueID.getResponseTime();
    }

    public static double fovFromEntity(Entity en) {
        return ((((double) (mc.player.rotationYaw - fovToEntity(en)) % 360.0D) + 540.0D) % 360.0D) - 180.0D;
    }


    public static float fovToEntity(Entity ent) {
        double x = ent.posX - mc.player.posX;
        double z = ent.posZ - mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }

    public static boolean isMoving() {
        return mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
    }

    public double direction() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static double PitchFromEntity(Entity en, float f) {
        return (double) (mc.player.rotationPitch - pitchToEntity(en, f));
    }

    public static float pitchToEntity(Entity ent, float f) {
        double x = mc.player.getDistanceToEntity(ent);
        double y = mc.player.posY - (ent.posY + f);
        double pitch = (((Math.atan2(x, y) * 180.0D) / 3.141592653589793D));
        return (float) (90 - pitch);
    }

    /**
     * Gets the block at a position
     *
     * @return block
     */
    public Block block(final double x, final double y, final double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    /**
     * Gets the block at a position
     *
     * @return block
     */
    public Block block(final BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getBlock();
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
        return mc.world.getBlockState(new BlockPos(mc.player).add(offsetX, offsetY, offsetZ)).getBlock();
    }


    /**
     * Checks if another players' team is the same as the players' team
     *
     * @return same team
     */
    public boolean sameTeam(final EntityLivingBase player) {
        if (player.getTeam() != null && mc.player.getTeam() != null) {
            final char c1 = player.getDisplayName().getFormattedText().charAt(1);
            final char c2 = mc.player.getDisplayName().getFormattedText().charAt(1);
            return c1 == c2;
        }
        return false;
    }


    public EnumFacingOffset getEnumFacing(final Vec3d position) {
        for (int x2 = -1; x2 <= 1; x2 += 2) {
            if (!(PlayerUtil.block(position.xCoord + x2, position.yCoord, position.zCoord) instanceof BlockAir)) {
                if (x2 > 0) {
                    return new EnumFacingOffset(EnumFacing.WEST, new Vec3d(x2, 0, 0));
                } else {
                    return new EnumFacingOffset(EnumFacing.EAST, new Vec3d(x2, 0, 0));
                }
            }
        }

        for (int y2 = -1; y2 <= 1; y2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord + y2, position.zCoord) instanceof BlockAir)) {
                if (y2 < 0) {
                    return new EnumFacingOffset(EnumFacing.UP, new Vec3d(0, y2, 0));
                }
            }
        }

        for (int z2 = -1; z2 <= 1; z2 += 2) {
            if (!(PlayerUtil.block(position.xCoord, position.yCoord, position.zCoord + z2) instanceof BlockAir)) {
                if (z2 < 0) {
                    return new EnumFacingOffset(EnumFacing.SOUTH, new Vec3d(0, 0, z2));
                } else {
                    return new EnumFacingOffset(EnumFacing.NORTH, new Vec3d(0, 0, z2));
                }
            }
        }

        return null;
    }


    /**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    public static Entity getMouseOver(final float partialTicks, final double Reach) {
        Entity pointedEntity = null;
        final Entity entity = mc.getRenderViewEntity();

        if (entity != null && mc.world != null) {
            mc.mcProfiler.startSection("pick");
            pointedEntity = null;
            double blockReachDistance = Reach;
            mc.objectMouseOver = entity.rayTrace(blockReachDistance, partialTicks);
            double distance = blockReachDistance;
            final Vec3d vec3 = entity.getPositionEyes(partialTicks);

            if (mc.objectMouseOver != null) {
                distance = mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3d vec31 = entity.getLook(partialTicks);
            final Vec3d vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
            pointedEntity = null;
            Vec3d vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = distance;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    pointedEntity = entity1;
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        boolean flag1 = false;

                        if (!flag1 && entity1 == entity.getRidingEntity()) {
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
        return mc.player.isInWater() || mc.player.isInLava();
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
        final AxisAlignedBB playerBB = PlayerUtil.mc.player.getEntityBoundingBox();
        final WorldClient world = PlayerUtil.mc.world;
        final int y = (int) playerBB.offset(0.0, -0.01, 0.0).minY;
        for (int x = MathHelper.floor(playerBB.minX); x < MathHelper.floor(playerBB.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(playerBB.minZ); z < MathHelper.floor(playerBB.maxZ) + 1; ++z) {
                final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
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
    public Vec3d getPlacePossibility(double offsetX, double offsetY, double offsetZ) {
        final List<Vec3d> possibilities = new ArrayList<>();
        final int range = (int) (5 + (Math.abs(offsetX) + Math.abs(offsetZ)));

        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);

                    if (!(block instanceof BlockAir)) {
                        for (int x2 = -1; x2 <= 1; x2 += 2)
                            possibilities.add(new Vec3d(mc.player.posX + x + x2, mc.player.posY + y, mc.player.posZ + z));

                        for (int y2 = -1; y2 <= 1; y2 += 2)
                            possibilities.add(new Vec3d(mc.player.posX + x, mc.player.posY + y + y2, mc.player.posZ + z));

                        for (int z2 = -1; z2 <= 1; z2 += 2)
                            possibilities.add(new Vec3d(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + z2));
                    }
                }
            }
        }

        possibilities.removeIf(vec3 -> mc.player.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) > 5 || !(PlayerUtil.block(vec3.xCoord, vec3.yCoord, vec3.zCoord) instanceof BlockAir));

        if (possibilities.isEmpty()) return null;

        possibilities.sort(Comparator.comparingDouble(vec3 -> {

            final double d0 = (mc.player.posX + offsetX) - vec3.xCoord;
            final double d1 = (mc.player.posY - 1 + offsetY) - vec3.yCoord;
            final double d2 = (mc.player.posZ + offsetZ) - vec3.zCoord;
            return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        }));

        return possibilities.get(0);
    }

    public Vec3d getPlacePossibility() {
        return getPlacePossibility(0, 0, 0);
    }

}