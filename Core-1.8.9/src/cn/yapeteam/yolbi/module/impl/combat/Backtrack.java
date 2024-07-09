package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import cn.yapeteam.yolbi.utils.render.RenderUtil;

import lombok.var;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("DuplicatedCode")
public class Backtrack extends Module {
    private final BooleanValue
            attackTimeFix = new BooleanValue("Attack Time Fix", false),
            renderBox = new BooleanValue("Render Box", false),
            onlyAttacking = new BooleanValue("Only Attacking", false),
            outline = new BooleanValue("Outline", false),
            rangeFix = new BooleanValue("Range Fix", true),
            s00 = new BooleanValue("S00", true),
            s03 = new BooleanValue("S03", true),
            s12 = new BooleanValue("S12", true),
            s27 = new BooleanValue("S27", true),
            s32 = new BooleanValue("S32", true),
            activity = new BooleanValue("Activity", true),
            updateOnAttacking = new BooleanValue("Update On Attacking", true);

    private final NumberValue<Float>
            hitRange = new NumberValue<>("Hit Range", 5.4f, 2f, 6f, 0.1f),
            minHitRange = new NumberValue<>("Min Hit Range", 2f, 1f, 6f, 0.1f),
            outlineWidth = new NumberValue<>("Outline Width", outline::getValue, 1.5f, 0.5f, 5f, 0.1f),
            updateOnAttackingDelay = new NumberValue<>("Update On Attacking Delay", 300f, 100f, 800f, 10f);

    private final NumberValue<Integer> backTrackDelay = new NumberValue<>("Backtrack Delay", 300, 100, 1000, 10);

    private final ModeValue<String>
            processS12Mode = new ModeValue<>("ProcessS12Mode", "InPut", "Cancel", "InPut"),
            processS27Mode = new ModeValue<>("ProcessS27Mode", "InPut", "Cancel", "InPut");

    public Backtrack() {
        super("Backtrack", ModuleCategory.COMBAT);
        addValues(
                attackTimeFix,
                renderBox,
                onlyAttacking,
                outline,
                rangeFix,
                s00,
                s03,
                s12,
                s27,
                s32,
                activity,
                hitRange,
                minHitRange,
                outlineWidth,
                backTrackDelay,
                processS12Mode,
                processS27Mode,
                updateOnAttacking,
                updateOnAttackingDelay
        );
    }

    private final List<Packet<?>> savePackets = new CopyOnWriteArrayList<>();
    double cXYZ = 0;
    boolean attacking = false;
    double updateAttack = 0;

    private final TimerUtil
            timer = new TimerUtil(),
            attackingTimer = new TimerUtil(),
            updateTimer = new TimerUtil();
    private double realX = 0, realY = 0, realZ = 0, rXYZ = 0, pXYZ = 0;
    private double x = 0, y = 0, z = 0;
    private double distanceToPacket = 0;

    private Entity getClosestEntity() {
        if (mc.theWorld == null) return null;
        List<Entity> filteredEntities = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                filteredEntities.add(entity);
            }
        }
        filteredEntities.sort((a, b) -> {
            double distanceA = mc.thePlayer.getDistanceToEntity(a);
            double distanceB = mc.thePlayer.getDistanceToEntity(b);
            return Double.compare(distanceB, distanceA);
        });
        return filteredEntities.isEmpty() ? null : filteredEntities.get(filteredEntities.size() - 1);
    }

    @Override
    protected void onEnable() {
        savePackets.clear();
        timer.reset();
        attackingTimer.reset();
    }

    private void processPacket1(Entity target) {
        if (mc.theWorld == null && mc.thePlayer == null) {
            return;
        }
        if (savePackets.isEmpty()) return;
        var p = savePackets.remove(0);
        String type = "";
        try {
            // S14PacketEntity
            if (p instanceof S14PacketEntity) {
                S14PacketEntity p1 = (S14PacketEntity) p;
                var entity = p1.getEntity(mc.theWorld);
                if (entity != null) {
                    entity.serverPosX += p1.func_149062_c();
                    entity.serverPosY += p1.func_149061_d();
                    entity.serverPosZ += p1.func_149064_e();
                    var d0 = entity.serverPosX / 32.0;
                    var d1 = entity.serverPosY / 32.0;
                    var d2 = entity.serverPosZ / 32.0;
                    var f = p1.func_149060_h() ? (p1.func_149066_f() * 360) / 256f : entity.rotationYaw;
                    var f1 = p1.func_149060_h() ? (p1.func_149063_g() * 360) / 256f : entity.rotationPitch;
                    if (entity instanceof EntityLivingBase) {
                        entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
                    }
                    entity.onGround = p1.getOnGround();
                }
                type = "s14";
            }
            // S18PacketEntityTeleport
            if (p instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport p1 = (S18PacketEntityTeleport) p;
                var entity = mc.theWorld.getEntityByID(p1.getEntityId());
                if (entity != null) {
                    entity.serverPosX = p1.getX();
                    entity.serverPosY = p1.getY();
                    entity.serverPosZ = p1.getZ();
                    var d0 = entity.serverPosX / 32.0;
                    var d1 = entity.serverPosY / 32.0;
                    var d2 = entity.serverPosZ / 32.0;
                    var f = (p1.getYaw() * 360) / 256f;
                    var f1 = (p1.getPitch() * 360) / 256f;
                    if (entity instanceof EntityLivingBase) {
                        if (Math.abs(entity.posX - d0) < 0.03125 && Math.abs(entity.posY - d1) < 0.015625
                                && Math.abs(entity.posZ - d2) < 0.03125) {
                            entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
                        } else {
                            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
                        }
                    }
                    entity.onGround = p1.getOnGround();
                }
                type = "s18";
            }
            // S03PacketTimeUpdate
            if (p instanceof S03PacketTimeUpdate) {
                S03PacketTimeUpdate p1 = (S03PacketTimeUpdate) p;
                mc.theWorld.setTotalWorldTime(p1.getTotalWorldTime());
                mc.theWorld.setWorldTime(p1.getWorldTime());
                type = "s03";
            }
            // S00PacketKeepAlive
            if (p instanceof S00PacketKeepAlive) {
                S00PacketKeepAlive p1 = (S00PacketKeepAlive) p;
                C00PacketKeepAlive packet = new C00PacketKeepAlive(p1.func_149134_c());
                PacketUtil.skip(packet);
                PacketUtil.sendPacket(packet);
                type = "s00";
            }
            // S12PacketEntityVelocity
            if (p instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity p1 = (S12PacketEntityVelocity) p;
                if (processS12Mode.is("InPut")) {
                    var entity = mc.theWorld.getEntityByID(p1.getEntityID());
                    if (entity != null) {
                        if (p1.getEntityID() == mc.thePlayer.getEntityId()) {
                            mc.thePlayer.setVelocity(
                                    (p1.getMotionX() * 100 / 100d) / 8000.0,
                                    (p1.getMotionY() * 100 / 100d) / 8000.0,
                                    (p1.getMotionZ() * 100 / 100d) / 8000.0);
                        } else {
                            entity.setVelocity(
                                    (p1.getMotionX() * 100 / 100d) / 8000.0,
                                    (p1.getMotionY() * 100 / 100d) / 8000.0,
                                    (p1.getMotionZ() * 100 / 100d) / 8000.0);
                        }
                    }
                }
                type = "s12";
            }
            // S27PacketExplosion
            if (p instanceof S27PacketExplosion) {
                S27PacketExplosion p1 = (S27PacketExplosion) p;
                if (processS27Mode.is("InPut")) {
                    var explosion = new Explosion(mc.theWorld, target, p1.getX(),
                            p1.getY(), p1.getZ(), p1.getStrength(), p1.getAffectedBlockPositions());
                    explosion.doExplosionB(true);
                    mc.thePlayer.setVelocity(
                            p1.func_149149_c() + mc.thePlayer.motionX,
                            p1.func_149144_d() + mc.thePlayer.motionY,
                            p1.func_149147_e() + mc.thePlayer.motionZ);
                }
                type = "s27";
            }
            // S06PacketUpdateHealth
            if (p instanceof S06PacketUpdateHealth) {
                S06PacketUpdateHealth p1 = (S06PacketUpdateHealth) p;
                mc.thePlayer.setPlayerSPHealth(p1.getHealth());
                mc.thePlayer.getFoodStats().setFoodLevel(p1.getFoodLevel());
                mc.thePlayer.getFoodStats().setFoodSaturationLevel(p1.getSaturationLevel());
                type = "s06";
            }
            // S29PacketSoundEffect
            if (p instanceof S29PacketSoundEffect) {
                S29PacketSoundEffect p1 = (S29PacketSoundEffect) p;
                mc.theWorld.playSound(p1.getX(), p1.getY(), p1.getZ(),
                        p1.getSoundName(), p1.getVolume(), p1.getPitch(), false);
                type = "s29";
            }
            // S32PacketConfirmTransaction
            if (p instanceof S32PacketConfirmTransaction) {
                S32PacketConfirmTransaction p1 = (S32PacketConfirmTransaction) p;
                var entityplayer = mc.thePlayer;
                Container container = null;
                if (p1.getWindowId() == 0) {
                    container = entityplayer.inventoryContainer;
                } else if (p1.getWindowId() == entityplayer.openContainer.windowId) {
                    container = entityplayer.openContainer;
                }
                if (container != null && !p1.func_148888_e()) {
                    C0FPacketConfirmTransaction packet = new C0FPacketConfirmTransaction(p1.getWindowId(), p1.getActionNumber(), true);
                    PacketUtil.skip(packet);
                    PacketUtil.sendPacket(packet);
                }
                type = "s32";
            }
            // S19PacketEntityHeadLook
            if (p instanceof S19PacketEntityHeadLook) {
                S19PacketEntityHeadLook p1 = (S19PacketEntityHeadLook) p;
                var entity = p1.getEntity(mc.theWorld);
                if (entity != null) {
                    entity.setRotationYawHead((p1.getYaw() * 360) / 256f);
                }
                type = "s19";
            }
            //S08PacketPlayerPosLook
            if (p instanceof S08PacketPlayerPosLook) {
                S08PacketPlayerPosLook p1 = (S08PacketPlayerPosLook) p;
                var entityplayer = mc.thePlayer;
                var d0 = p1.getX();
                var d1 = p1.getY();
                var d2 = p1.getZ();
                entityplayer.setPositionAndRotation(d0, d1, d2, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                C03PacketPlayer.C06PacketPlayerPosLook packet = new C03PacketPlayer.C06PacketPlayerPosLook(
                        entityplayer.posX, entityplayer.getEntityBoundingBox().minY,
                        entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false);
                PacketUtil.skip(packet);
                PacketUtil.sendPacket(packet);
                if (mc.thePlayer.isOnLadder()) {
                    mc.thePlayer.prevPosX = mc.thePlayer.posX;
                    mc.thePlayer.prevPosY = mc.thePlayer.posY;
                    mc.thePlayer.prevPosZ = mc.thePlayer.posZ;
                    mc.displayGuiScreen(null);
                }
                type = "s08";
            }
            // S0FPacketSpawnMob
            if (p instanceof S0FPacketSpawnMob) {
                S0FPacketSpawnMob p1 = (S0FPacketSpawnMob) p;
                var d0 = p1.getX() / 32.0;
                var d1 = p1.getY() / 32.0;
                var d2 = p1.getZ() / 32.0;
                var f = (p1.getYaw() * 360) / 256f;
                var f1 = (p1.getPitch() * 360) / 256f;
                var entitylivingbase = (EntityLivingBase) EntityList.createEntityByID(p1.getEntityType(), mc.theWorld);
                entitylivingbase.serverPosX = p1.getX();
                entitylivingbase.serverPosY = p1.getY();
                entitylivingbase.serverPosZ = p1.getZ();
                entitylivingbase.renderYawOffset = entitylivingbase.rotationYawHead = (p1.getHeadPitch() * 360) / 256f;
                var aentity = entitylivingbase.getParts();
                if (aentity != null) {
                    var i = p1.getEntityID() - entitylivingbase.getEntityId();
                    for (Entity entity : aentity) {
                        entity.setEntityId(entity.getEntityId() + i);
                    }
                }
                entitylivingbase.setEntityId(p1.getEntityID());
                entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
                entitylivingbase.motionX = (p1.getVelocityX() / 8000.0);
                entitylivingbase.motionY = (p1.getVelocityY() / 8000.0);
                entitylivingbase.motionZ = (p1.getVelocityZ() / 8000.0);
                mc.theWorld.addEntityToWorld(p1.getEntityID(), entitylivingbase);
                var list = p1.func_149027_c();
                if (list != null) {
                    entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
                }
                type = "s0f";
            }
        } catch (Throwable e) {
            Logger.error("Backtrack: processPacket1 " + type);
            Logger.exception(e);
        }
    }

    double getPacketsDistance(Entity target) {
        if (!savePackets.isEmpty()) {
            var p = savePackets.get(savePackets.size() - 1);
            if (p instanceof S14PacketEntity) {
                S14PacketEntity p1 = (S14PacketEntity) p;
                var entity = p1.getEntity(mc.theWorld);
                if (entity == target) {
                    if (target instanceof EntityLivingBase) {
                        distanceToPacket = mc.thePlayer.getDistance(target.posX + (p1.func_149062_c() / 32.0),
                                target.posY + (p1.func_149061_d() / 32.0), target.posZ + (p1.func_149064_e() / 32.0));
                    }
                }
            }
            if (p instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport p1 = (S18PacketEntityTeleport) p;
                var entity = mc.theWorld.getEntityByID(p1.getEntityId());
                if (entity == target) {
                    if (target instanceof EntityLivingBase) {
                        distanceToPacket = mc.thePlayer.getDistance(p1.getX() / 32.0, p1.getY() / 32.0, p1.getZ() / 32.0);
                    }
                }
            }
        }
        return distanceToPacket;
    }

    private void processPacket2(Entity target) {
        double _5 = minHitRange.getValue();
        if (cXYZ > _5) {
            if (attacking) {
                if (updateTimer.hasTimePassed(updateOnAttackingDelay.getValue().longValue())) {
                    updateAttack += 1;
                    updateTimer.reset();
                }
            } else {
                updateAttack = 0;
            }
            if (updateAttack > 0) {
                processPacket1(target);
                updateAttack--;
                return;
            }
            if (rangeFix.getValue() && (cXYZ > pXYZ)) {
                processPacket1(target);
                timer.reset();
                return;
            }
            if (getPacketsDistance(target) <= _5) {
                processPacket1(target);
                timer.reset();
                return;
            }
            if (timer.hasTimePassed(backTrackDelay.getValue())) {
                processPacket1(target);
                timer.reset();
                return;
            }
        }
    }

    void addPackets(Packet<?> packet, EventPacket event) {
        if (blockPacket(packet)) {
            savePackets.add(packet);
            event.setCancelled(true);
        }
    }

    boolean blockPacket(Packet<? extends INetHandler> packet) {
        if (s00.getValue() && packet instanceof S03PacketTimeUpdate) {
            return true;
        }
        if (s03.getValue() && packet instanceof S03PacketTimeUpdate) {
            return true;
        }
        if (s12.getValue() && packet instanceof S12PacketEntityVelocity) {
            return true;
        }
        if (s32.getValue() && packet instanceof S32PacketConfirmTransaction) {
            return true;
        }
        if (s27.getValue() && packet instanceof S27PacketExplosion) {
            return true;
        }
        return (packet instanceof S14PacketEntity
                || packet instanceof S18PacketEntityTeleport
                || packet instanceof S19PacketEntityHeadLook
                || packet instanceof S08PacketPlayerPosLook
                || packet instanceof S0FPacketSpawnMob);
    }

    @Listener
    private void onPacket(EventPacket e) {
        try {
            Packet<?> packet = e.getPacket();
            Entity target = getClosestEntity();
            if (target == null) return;
            double cx = target.posX, cy = target.posY, cz = target.posZ;
            double _3 = hitRange.getValue();
            if (target instanceof EntityLivingBase) {
                if (target.serverPosX != 0 && target.serverPosY != 0 && target.serverPosZ != 0 && target.width != 0 && target.height != 0) {
                    realX = target.serverPosX / 32d;
                    realY = target.serverPosY / 32d;
                    realZ = target.serverPosZ / 32d;
                }
                cXYZ = mc.thePlayer.getDistance(cx, cy, cz);
                rXYZ = mc.thePlayer.getDistance(realX, realY, realZ);
                pXYZ = mc.thePlayer.getDistance(x, y, z);
            }
            if (packet instanceof S14PacketEntity) {
                S14PacketEntity packet1 = (S14PacketEntity) packet;
                var entity = packet1.getEntity(mc.theWorld);
                if (entity == target) {
                    x += packet1.func_149062_c() / 32.0;
                    y += packet1.func_149061_d() / 32.0;
                    z += packet1.func_149064_e() / 32.0;
                }
            }
            if (packet instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport packet1 = (S18PacketEntityTeleport) packet;
                var entity = mc.theWorld.getEntityByID(packet1.getEntityId());
                if (entity == target) {
                    x = packet1.getX() / 32.0;
                    y = packet1.getY() / 32.0;
                    z = packet1.getZ() / 32.0;
                }
            }
            if (mc.thePlayer != null && !mc.thePlayer.isDead && mc.theWorld != null) {
                addPackets(packet, e);
            } else {
                processPacket1(target);
            }
            if (!thing2() || !thing3(target) || !thing5()) {
                processPacket1(target);
                if (activity.getValue()) {
                    timer.reset();
                }
            }
            if (rXYZ > _3 || pXYZ > _3) {
                processPacket1(target);
                timer.reset();
            } else {
                processPacket2(target);
            }
            if (packet instanceof C02PacketUseEntity) {
                attacking = true;
                attackingTimer.reset();
            }
            if (attacking) {
                if (attackingTimer.hasTimePassed(400)) {
                    attacking = false;
                }
            }
        } catch (Throwable ex) {
            Logger.exception(ex);
        }
    }

    @Override
    protected void onDisable() {
        savePackets.clear();
        timer.reset();
        attackingTimer.reset();
    }

    @Listener
    private void onRender3D(EventRender3D event) {
        if (renderBox.getValue())
            RenderUtil.drawEntityBox(getEntityBoundingBox(x, y, z, 0.6, 1.8), x, y, z, x, y, z, new Color(-1), outline.getValue(), true, outlineWidth.getValue(), event.getPartialTicks());
    }

    public AxisAlignedBB getEntityBoundingBox(double posX, double posY, double posZ, double width, double height) {
        double f = width / 2;
        return new AxisAlignedBB(
                posX - f, posY, posZ - f,
                posX + f, posY + height,
                posZ + f
        );
    }

    boolean thing2() {
        return true; // Adjust this to match your specific logic
    }

    boolean thing3(Entity target) {
        if (!attackTimeFix.getValue()) {
            return true;
        }
        if (target instanceof EntityLivingBase) {
            return (target.posY - target.lastTickPosY) > 0 || mc.thePlayer.posY <= target.posY
                    || mc.thePlayer.onGround;
        }
        return false;
    }

    boolean thing5() {
        if (onlyAttacking.getValue()) {
            return attacking;
        }
        return true;
    }
}
