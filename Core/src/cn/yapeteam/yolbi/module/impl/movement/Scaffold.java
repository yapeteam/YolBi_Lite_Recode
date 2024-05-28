package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.utils.vector.Vector3d;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import cn.yapeteam.yolbi.utils.player.*;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.util.vector.Vector2f;

@ModuleInfo(name = "Scaffold", category = ModuleCategory.MOVEMENT)
public class Scaffold extends Module {
    private final ModeValue<String> sameY = new ModeValue<>("Same Y", "Off", "Off", "On", "Auto Jump");
    private final NumberValue<Integer> minRotationSpeed = new NumberValue<>("Rotation Speed", 0, 0, 10, 1);
    private final NumberValue<Integer> maxRotationSpeed = new NumberValue<>("Rotation Speed", 5, 0, 10, 1);
    private final NumberValue<Integer> minPlaceDelay = new NumberValue<>("Place Delay", 0, 0, 5, 1);
    private final NumberValue<Integer> maxPlaceDelay = new NumberValue<>("Place Delay", 0, 0, 5, 1);

    private Vec3 targetBlock;
    private EnumFacingOffset enumFacing;
    private BlockPos blockFace;
    private float targetYaw, targetPitch;
    private int ticksOnAir;
    private double startY;

    public Scaffold() {
        addValues(sameY, minRotationSpeed, maxRotationSpeed, minPlaceDelay, maxPlaceDelay);
    }


    @Override
    protected void onEnable() {
        startY = Math.floor(mc.thePlayer.posY);
        targetBlock = null;
    }

    public void calculateRotations() {
        /* Calculating target rotations */
        getRotations(-45);

        /* Smoothing rotations */
        final double minRotationSpeed = this.minRotationSpeed.getValue().doubleValue();
        final double maxRotationSpeed = this.maxRotationSpeed.getValue().doubleValue();
        float rotationSpeed = (float) MathUtils.getRandom(minRotationSpeed, maxRotationSpeed);

        if (rotationSpeed != 0) {
            RotationManager.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, MovementFix.OFF);
        }
    }


    @Listener
    public void onPreUpdate(EventUpdate event) {
        //Used to detect when to place a block, if over air, allow placement of blocks
        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
            ticksOnAir++;
            ReflectUtil.SetPressed(mc.gameSettings.keyBindSneak, true);
        } else {
            ticksOnAir = 0;
            ReflectUtil.SetPressed(mc.gameSettings.keyBindSneak, false);
        }

        // Gets block to place
        targetBlock = convertVec3(PlayerUtil.getPlacePossibility(0, 0, 0));

        if (targetBlock == null) {
            return;
        }

        //Gets EnumFacing
        enumFacing = PlayerUtil.getEnumFacing(convertVec3(targetBlock));

        if (enumFacing == null) {
            return;
        }

        final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

        blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);

        if (blockFace == null || enumFacing == null) {
            return;
        }

        this.calculateRotations();

        if (targetBlock == null || enumFacing == null || blockFace == null) {
            return;
        }

        if (this.sameY.is("Auto Jump")) {
            ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, (mc.thePlayer.onGround && PlayerUtil.isMoving()) || mc.gameSettings.keyBindJump.isPressed());
        }

        // Same Y
        final boolean sameY = ((!this.sameY.is("Off")) && !mc.gameSettings.keyBindJump.isKeyDown()) && PlayerUtil.isMoving();

        if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
            return;
        }

        if (ticksOnAir >= MathUtils.getRandom(minPlaceDelay.getValue(), maxPlaceDelay.getValue())) {

            Vec3 hitVec = this.getHitVec();

            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockFace, enumFacing.getEnumFacing(), convertVec3(hitVec))) {
                PacketUtil.sendPacket(new C0APacketAnimation());
            }

            ReflectUtil.SetRightClickDelayTimer(mc, 0);
            ticksOnAir = 0;
        } else if (Math.random() > 0.92 && ReflectUtil.GetRightClickDelayTimer(mc) <= 0) {
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            ReflectUtil.SetRightClickDelayTimer(mc, 0);
        }

        //For Same Y
        if (mc.thePlayer.onGround || (mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtil.isMoving())) {
            startY = Math.floor(mc.thePlayer.posY);
        }

        if (mc.thePlayer.posY < startY) {
            startY = mc.thePlayer.posY;
        }
    }

    public void getRotations(final float yawOffset) {
        double calculatedyaw = RotationManager.calculate(convertVec3d(targetBlock)).x;
        if (calculatedyaw == 180) {
            calculatedyaw = 45;
        } else if (calculatedyaw == 0) {
            calculatedyaw = -45;
        }

        System.out.println(calculatedyaw);
        targetYaw = (float) Math.round(calculatedyaw / 45) * 45 - 180 + yawOffset;

        targetPitch = RotationManager.calculate(convertVec3d(targetBlock)).y;
    }

    @AllArgsConstructor
    public static class Vec3 {
        public double xCoord;
        public double yCoord;
        public double zCoord;
    }

    private net.minecraft.util.Vec3 convertVec3(Vec3 vec3) {
        if (vec3 == null)
            return null;
        return new net.minecraft.util.Vec3(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    private Vec3 convertVec3(net.minecraft.util.Vec3 vec3) {
        if (vec3 == null)
            return null;
        return new Vec3(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    private Vector3d convertVec3d(Vec3 vec3) {
        return new Vector3d(vec3.xCoord, vec3.yCoord, vec3.xCoord);
    }

    public Vec3 getHitVec() {
        /* Correct HitVec */
        Vec3 hitVec = new Vec3(blockFace.getX() + Math.random(), blockFace.getY() + Math.random(), blockFace.getZ() + Math.random());

        final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationManager.rotations, mc.playerController.getBlockReachDistance());

        switch (enumFacing.getEnumFacing()) {
            case DOWN:
                hitVec.yCoord = blockFace.getY();
                break;

            case UP:
                hitVec.yCoord = blockFace.getY() + 1;
                break;

            case NORTH:
                hitVec.zCoord = blockFace.getZ();
                break;

            case EAST:
                hitVec.xCoord = blockFace.getX() + 1;
                break;

            case SOUTH:
                hitVec.zCoord = blockFace.getZ() + 1;
                break;

            case WEST:
                hitVec.xCoord = blockFace.getX();
                break;
        }

        if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockFace) &&
                movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
            hitVec = convertVec3(movingObjectPosition.hitVec);
        }

        return hitVec;
    }
}
