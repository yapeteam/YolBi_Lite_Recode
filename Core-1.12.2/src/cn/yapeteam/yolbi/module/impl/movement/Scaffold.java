package cn.yapeteam.yolbi.module.impl.movement;

//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
//import cn.yapeteam.yolbi.module.Module;
//import cn.yapeteam.yolbi.module.ModuleCategory;
//import cn.yapeteam.yolbi.module.ModuleInfo;
//import cn.yapeteam.yolbi.module.values.impl.ModeValue;
//import cn.yapeteam.yolbi.module.values.impl.NumberValue;
//import cn.yapeteam.yolbi.utils.math.MathUtils;
//import cn.yapeteam.yolbi.utils.network.PacketUtil;
//import cn.yapeteam.yolbi.utils.player.*;
//import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
//import cn.yapeteam.yolbi.utils.vector.Vector2f;
//import cn.yapeteam.yolbi.utils.vector.Vector3d;
//import lombok.AllArgsConstructor;
//import net.minecraft.block.BlockAir;
//import net.minecraft.client.settings.KeyBinding;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.RayTraceResult;
//import net.minecraft.util.math.Vec3d;
//
//@ModuleInfo(name = "Scaffold", category = ModuleCategory.MOVEMENT)
//public class Scaffold extends Module {
//    private final ModeValue<String> sameY = new ModeValue<>("Same Y", "Off", "Off", "On", "Auto Jump");
//    private final NumberValue<Integer> minRotationSpeed = new NumberValue<>("Min Rotation Speed", 0, 0, 10, 1);
//    private final NumberValue<Integer> maxRotationSpeed = new NumberValue<>("Max Rotation Speed", 5, 0, 10, 1);
//    private final NumberValue<Integer> minPlaceDelay = new NumberValue<>("Min Place Delay", 0, 0, 5, 1);
//    private final NumberValue<Integer> maxPlaceDelay = new NumberValue<>("Max Place Delay", 0, 0, 5, 1);
//
//    private Vec3d targetBlock;
//    private EnumFacingOffset enumFacing;
//    private BlockPos blockFace;
//    private float targetYaw, targetPitch;
//    private int ticksOnAir;
//    private double startY;
//
//    public Scaffold() {
//        NumberValue.setBound(minRotationSpeed, maxRotationSpeed);
//        NumberValue.setBound(minPlaceDelay, maxPlaceDelay);
//        addValues(sameY, minRotationSpeed, maxRotationSpeed, minPlaceDelay, maxPlaceDelay);
//    }
//
//
//    @Override
//    protected void onEnable() {
//        if (mc.player == null) {
//            setEnabled(false);
//            return;
//        }
//        startY = Math.floor(mc.player.posY);
//        targetBlock = null;
//    }
//
//    @Override
//    protected void onDisable() {
//        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
//    }
//
//    public void calculateRotations() {
//        /* Calculating target rotations */
//        getRotations(-45);
//
//        /* Smoothing rotations */
//        final double minRotationSpeed = this.minRotationSpeed.getValue().doubleValue();
//        final double maxRotationSpeed = this.maxRotationSpeed.getValue().doubleValue();
//        float rotationSpeed = (float) MathUtils.getRandom(minRotationSpeed, maxRotationSpeed);
//
//        if (rotationSpeed != 0) {
//            RotationManager.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, MovementFix.OFF);
//        }
//    }
//
//
//    @Listener
//    public void onPreUpdate(EventUpdate event) {
//        //Used to detect when to place a block, if over air, allow placement of blocks
//        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
//            ticksOnAir++;
//            ReflectUtil.SetPressed(mc.gameSettings.keyBindSneak, true);
//        } else {
//            ticksOnAir = 0;
//            ReflectUtil.SetPressed(mc.gameSettings.keyBindSneak, false);
//        }
//
//        // Gets block to place
//        targetBlock = convertVec3(PlayerUtil.getPlacePossibility(0, 0, 0));
//
//        if (targetBlock == null) {
//            return;
//        }
//
//        //Gets EnumFacing
//        enumFacing = PlayerUtil.getEnumFacing(convertVec3(targetBlock));
//
//        if (enumFacing == null) {
//            return;
//        }
//
//        final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);
//
//        blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);
//
//        if (enumFacing == null)
//            return;
//
//        this.calculateRotations();
//
//        if (targetBlock == null || enumFacing == null || blockFace == null) {
//            return;
//        }
//
//        if (this.sameY.is("Auto Jump")) {
//            ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, (mc.player.onGround && PlayerUtil.isMoving()) || mc.gameSettings.keyBindJump.isPressed());
//        }
//
//        // Same Y
//        final boolean sameY = ((!this.sameY.is("Off")) && !mc.gameSettings.keyBindJump.isKeyDown()) && PlayerUtil.isMoving();
//
//        if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
//            return;
//        }
//
//        if (ticksOnAir >= MathUtils.getRandom(minPlaceDelay.getValue(), maxPlaceDelay.getValue())) {
//
//            Vec3 hitVec = this.getHitVec();
//
//            // todo: fix animation
//            //  if (mc.playerController.(mc.player)) {
//            //      PacketUtil.sendPacket(new CPacketAnimation());
//            //  }
//
//            ReflectUtil.SetRightClickDelayTimer(mc, 0);
//            ticksOnAir = 0;
//        } else if (Math.random() > 0.92 && ReflectUtil.GetRightClickDelayTimer(mc) <= 0) {
//            PacketUtil.sendPacket(new CPacketPlayerBlockPlacement(mc.player.getHeldItem()));
//            ReflectUtil.SetRightClickDelayTimer(mc, 0);
//        }
//
//        //For Same Y
//        if (mc.player.onGround || (mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtil.isMoving())) {
//            startY = Math.floor(mc.player.posY);
//        }
//
//        if (mc.player.posY < startY) {
//            startY = mc.player.posY;
//        }
//    }
//
//    public void getRotations(final float yawOffset) {
//        boolean found = false;
//        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
//            final Vector2f rotations = RotationManager.calculate(
//                    new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());
////            targetYaw = rotations.x;
//            targetYaw = rotations.x;
//            targetPitch = 78.1F;
//        } else {
//            found = true;
//            // target yaw and pitch stays the same
//        }
//
//    }
//
//    @AllArgsConstructor
//    public static class Vec3 {
//        public double xCoord;
//        public double yCoord;
//        public double zCoord;
//    }
//
//    private net.minecraft.util.math.Vec3d convertVec3(Vec3 vec3) {
//        if (vec3 == null)
//            return null;
//        return new net.minecraft.util.math.Vec3d(vec3.xCoord, vec3.yCoord, vec3.zCoord);
//    }
//
//    private Vec3 convertVec3(net.minecraft.util.math.Vec3d vec3) {
//        if (vec3 == null)
//            return null;
//        return new Vec3(vec3.xCoord, vec3.yCoord, vec3.zCoord);
//    }
//
//    private Vector3d convertVec3d(Vec3 vec3) {
//        return new Vector3d(vec3.xCoord, vec3.yCoord, vec3.xCoord);
//    }
//
//    public Vec3 getHitVec() {
//        /* Correct HitVec */
//        Vec3 hitVec = new Vec3(blockFace.getX() + Math.random(), blockFace.getY() + Math.random(), blockFace.getZ() + Math.random());
//
//        final RayTraceResult movingObjectPosition = RayCastUtil.rayCast(RotationManager.rotations, mc.playerController.getBlockReachDistance());
//
//        switch (enumFacing.getEnumFacing()) {
//            case DOWN:
//                hitVec.yCoord = blockFace.getY();
//                break;
//
//            case UP:
//                hitVec.yCoord = blockFace.getY() + 1;
//                break;
//
//            case NORTH:
//                hitVec.zCoord = blockFace.getZ();
//                break;
//
//            case EAST:
//                hitVec.xCoord = blockFace.getX() + 1;
//                break;
//
//            case SOUTH:
//                hitVec.zCoord = blockFace.getZ() + 1;
//                break;
//
//            case WEST:
//                hitVec.xCoord = blockFace.getX();
//                break;
//        }
//
//        if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockFace) &&
//                movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
//            hitVec = convertVec3(movingObjectPosition.hitVec);
//        }
//
//        return hitVec;
//    }
//}
//