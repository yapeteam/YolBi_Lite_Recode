//package cn.yapeteam.yolbi.module.impl.combat;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
//import cn.yapeteam.loader.api.module.values.impl.NumberValue;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventMotion;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.MathHelper;
//import net.minecraft.util.ChatComponentText;
//
//@ModuleInfo(name = "KillAura", category = ModuleCategory.COMBAT)
//public class KillAura extends Module {
//    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", true);
//    private final NumberValue<Double> swingRange = new NumberValue<>("SwingRange", 4.0, 1.0, 6.0, 0.1);
//    private final NumberValue<Integer> maxAPS = new NumberValue<>("MaxAPS", 10, 1, 20, 1);
//    private final NumberValue<Integer> minAPS = new NumberValue<>("MinAPS", 5, 1, 20, 1);
//    private final BooleanValue showTarget = new BooleanValue("ShowTarget", true);
//    private final BooleanValue movementFix = new BooleanValue("MovementFix", true);
//
//    {
//        addValues(keepSprint, swingRange, maxAPS, minAPS, showTarget, movementFix);
//    }
//
//    private long lastAttackTime;
//    private Entity currentTarget;
//    private long attackCount;
//    private long startTime;
//
//    @Listener
//    private void onMotion(EventMotion event) {
//        Minecraft mc = Minecraft.getMinecraft();
//        EntityPlayerSP player = mc.thePlayer;
//
//        if (player != null) {
//            if (startTime == 0) {
//                startTime = System.currentTimeMillis();
//            }
//
//            findTarget(mc);
//            if (currentTarget != null) {
//                faceTarget(player, currentTarget);
//                if (shouldAttack()) {
//                    if (movementFix.getValue()) {
//                        applyMovementFix(event, player);
//                    }
//                    attackTarget(mc, player, currentTarget);
//                }
//            }
//        }
//    }
//
//    private void findTarget(Minecraft mc) {
//        double range = swingRange.getValue();
//        currentTarget = null;
//        for (Entity entity : mc.theWorld.loadedEntityList) {
//            if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getDistanceToEntity(mc.thePlayer) <= range) {
//                currentTarget = entity;
//                break;
//            }
//        }
//    }
//
//    private void faceTarget(EntityPlayerSP player, Entity target) {
//        double deltaX = target.posX - player.posX;
//        double deltaY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight());
//        double deltaZ = target.posZ - player.posZ;
//        double distance = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
//
//        float yaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
//        float pitch = (float) -(MathHelper.atan2(deltaY, distance) * 180.0D / Math.PI);
//
//        player.rotationYaw = yaw;
//        player.rotationPitch = pitch;
//    }
//
//    private boolean shouldAttack() {
//        int minAps = minAPS.getValue();
//        int maxAps = maxAPS.getValue();
//        int aps = minAps + (int) (Math.random() * (maxAps - minAps));
//
//        long timeBetweenAttacks = 1000 / aps;
//        long currentTime = System.currentTimeMillis();
//
//        return currentTime - lastAttackTime >= timeBetweenAttacks;
//    }
//
//    private void attackTarget(Minecraft mc, EntityPlayerSP player, Entity target) {
//        if (keepSprint.getValue() && !player.isSprinting()) {
//            player.setSprinting(true);
//        }
//
//        mc.playerController.attackEntity(player, target);
//        player.swingItem();
//        lastAttackTime = System.currentTimeMillis();
//        attackCount++;
//
//        double distance = player.getDistanceToEntity(target);
//        double timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0;
//        double cps = attackCount / timeElapsed;
//
//        mc.thePlayer.addChatMessage(new ChatComponentText(
//                String.format("Attack Distance: %.2f, CPS: %.2f, Target: %s", distance, cps, target.getName())
//        ));
//
//        if (showTarget.getValue()) {
//            mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "Target: " + target.getName(), mc.displayWidth / 2, mc.displayHeight / 2 - 10, 0xFFFFFF);
//        }
//    }
//
//    private void applyMovementFix(EventMotion event, EntityPlayerSP player) {
//        // 防止倒着疾跑
//        if (player.movementInput.moveForward < 0) {
//            player.movementInput.moveForward = 0;
//        }
//
//        // 修复打人时的移动
//        if (currentTarget != null) {
//            double deltaX = currentTarget.posX - player.posX;
//            double deltaZ = currentTarget.posZ - player.posZ;
//            double angleToTarget = Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI - 90.0F;
//
//            double angleDifference = angleToTarget - player.rotationYaw;
//            if (angleDifference < -180.0D) {
//                angleDifference += 360.0D;
//            } else if (angleDifference > 180.0D) {
//                angleDifference -= 360.0D;
//            }
//
//            player.movementInput.moveForward = (float) (Math.cos(Math.toRadians(angleDifference)) * player.movementInput.moveForward);
//            player.movementInput.moveStrafe = (float) (Math.sin(Math.toRadians(angleDifference)) * player.movementInput.moveStrafe);
//        }
//
//        // 应用修正后的移动
//        float forward = player.movementInput.moveForward;
//        float strafe = player.movementInput.moveStrafe;
//        float yaw = player.rotationYaw;
//
//        if (forward == 0.0F && strafe == 0.0F) {
//            event.setX(0.0D);
//            event.setZ(0.0D);
//        } else {
//            if (forward != 0.0F) {
//                if (strafe > 0.0F) {
//                    yaw += (forward > 0.0F) ? -45 : 45;
//                } else if (strafe < 0.0F) {
//                    yaw += (forward > 0.0F) ? 45 : -45;
//                }
//                strafe = 0.0F;
//                if (forward > 0.0F) {
//                    forward = 1.0F;
//                } else if (forward < 0.0F) {
//                    forward = -1.0F;
//                }
//            }
//
//            double speed = player.capabilities.getWalkSpeed();
//            double x = (double) (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
//            double z = (double) (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
//
//            event.setX(x);
//            event.setZ(z);
//        }
//    }
//}
package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.util.ChatComponentText;

@ModuleInfo(name = "KillAura", category = ModuleCategory.COMBAT)
public class KillAura extends Module {
    private final BooleanValue dev = new BooleanValue("Dev", false);
    private final BooleanValue keepSprint = new BooleanValue("KeepSprint", false);
    private final NumberValue<Double> swingRange = new NumberValue<>("SwingRange", 4.0, 1.0, 6.0, 0.1);
    private final NumberValue<Integer> maxAPS = new NumberValue<>("MaxAPS", 10, 1, 20, 1);
    private final NumberValue<Integer> minAPS = new NumberValue<>("MinAPS", 5, 1, 20, 1);
    //private final BooleanValue showTarget = new BooleanValue("ShowTarget", true);
    private final BooleanValue movementFix = new BooleanValue("MovementFix", false);

    {
        addValues(dev,keepSprint, swingRange, maxAPS, minAPS, movementFix);
    }

    private long lastAttackTime;
    private Entity currentTarget;
    private long attackCount;
    private long startTime;

    @Listener
    private void onMotion(EventMotion event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (player != null) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            findTarget(mc);
            if (currentTarget != null) {
                faceTarget(player, currentTarget);
                if (shouldAttack()) {
                    if (movementFix.getValue()) {
                        applyMovementFix(player);
                    }
                    attackTarget(mc, player, currentTarget);
                }
            }
        }
    }

    private void findTarget(Minecraft mc) {
        double range = swingRange.getValue();
        currentTarget = null;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getDistanceToEntity(mc.thePlayer) <= range) {
                currentTarget = entity;
                break;
            }
        }
    }

    private void faceTarget(EntityPlayerSP player, Entity target) {
        double deltaX = target.posX - player.posX;
        double deltaY = target.posY + target.getEyeHeight() - (player.posY + player.getEyeHeight());
        double deltaZ = target.posZ - player.posZ;
        double distance = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(MathHelper.atan2(deltaY, distance) * 180.0D / Math.PI);

        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
    }

    private boolean shouldAttack() {
        int minAps = minAPS.getValue();
        int maxAps = maxAPS.getValue();
        int aps = minAps + (int) (Math.random() * (maxAps - minAps));

        long timeBetweenAttacks = 1000 / aps;
        long currentTime = System.currentTimeMillis();

        return currentTime - lastAttackTime >= timeBetweenAttacks;
    }

    private void attackTarget(Minecraft mc, EntityPlayerSP player, Entity target) {
        if (keepSprint.getValue() && !player.isSprinting()) {
            player.setSprinting(true);
        }

        mc.playerController.attackEntity(player, target);
        player.swingItem();
        lastAttackTime = System.currentTimeMillis();
        attackCount++;

        double distance = player.getDistanceToEntity(target);
        double timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0;
        double cps = attackCount / timeElapsed;

        if (dev.getValue()){
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    String.format("Attack Distance: %.2f, CPS: %.2f, Target: %s", distance, cps, target.getName())
            ));
        }

//        if (showTarget.getValue()) {
//            mc.ingameGUI.drawCenteredString(mc.fontRendererObj, "Target: " + target.getName(), mc.displayWidth / 2, mc.displayHeight / 2 - 10, 0xFFFFFF);
//        }
    }

    private void applyMovementFix(EntityPlayerSP player) {
        // 防止倒着疾跑
        if (player.movementInput.moveForward < 0) {
            player.movementInput.moveForward = 0;
        }
    }
}
