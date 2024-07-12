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
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.AxisAlignedBB;
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
        if (mc.world == null) return null;
        List<Entity> filteredEntities = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.player) {
                filteredEntities.add(entity);
            }
        }
        filteredEntities.sort((a, b) -> {
            double distanceA = mc.player.getDistanceToEntity(a);
            double distanceB = mc.player.getDistanceToEntity(b);
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
        if (mc.world == null && mc.player == null) {
            return;
        }
        if (savePackets.isEmpty()) return;
        var p = savePackets.remove(0);
        String type = "";
        try {
            // SPacketEntity
            if (p instanceof SPacketEntity) {
                SPacketEntity p1 = (SPacketEntity) p;
                var entity = p1.getEntity(mc.world);
                entity.serverPosX += p1.getX();
                entity.serverPosY += p1.getY();
                entity.serverPosZ += p1.getZ();
                var d0 = entity.serverPosX / 32.0;
                var d1 = entity.serverPosY / 32.0;
                var d2 = entity.serverPosZ / 32.0;
                var f = p1.isRotating() ? (p1.getYaw() * 360) / 256f : entity.rotationYaw;
                var f1 = p1.isRotating() ? (p1.getPitch() * 360) / 256f : entity.rotationPitch;
                if (entity instanceof EntityLivingBase) {
                    entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, false);
                    //  entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
                }
                entity.onGround = p1.getOnGround();
                type = "s14";
            }
            // SPacketEntityTeleport
            if (p instanceof SPacketEntityTeleport) {
                SPacketEntityTeleport p1 = (SPacketEntityTeleport) p;
                var entity = mc.world.getEntityByID(p1.getEntityId());
                if (entity != null) {
                    // entity.serverPosX = p1.getX();
                    // entity.serverPosY = p1.getY();
                    // entity.serverPosZ = p1.getZ();
                    entity.serverPosX = (long) p1.getX();
                    entity.serverPosY = (long) p1.getY();
                    entity.serverPosZ = (long) p1.getZ();
                    var d0 = entity.serverPosX / 32.0;
                    var d1 = entity.serverPosY / 32.0;
                    var d2 = entity.serverPosZ / 32.0;
                    var f = (p1.getYaw() * 360) / 256f;
                    var f1 = (p1.getPitch() * 360) / 256f;
                    if (entity instanceof EntityLivingBase) {
                        if (Math.abs(entity.posX - d0) < 0.03125 && Math.abs(entity.posY - d1) < 0.015625
                                && Math.abs(entity.posZ - d2) < 0.03125) {
                            entity.setPositionAndRotationDirect(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
                        } else {
                            entity.setPositionAndRotationDirect(d0, d1, d2, f, f1, 3, true);
                        }
                    }
                    entity.onGround = p1.getOnGround();
                }
                type = "s18";
            }
            // SPacketTimeUpdate
            if (p instanceof SPacketTimeUpdate) {
                SPacketTimeUpdate p1 = (SPacketTimeUpdate) p;
                mc.world.setTotalWorldTime(p1.getTotalWorldTime());
                mc.world.setWorldTime(p1.getWorldTime());
                type = "s03";
            }
            // SPacketKeepAlive
            if (p instanceof SPacketKeepAlive) {
                SPacketKeepAlive p1 = (SPacketKeepAlive) p;
                CPacketKeepAlive packet = new CPacketKeepAlive(p1.getId());
                PacketUtil.skip(packet);
                PacketUtil.sendPacket(packet);
                type = "s00";
            }
            // SPacketEntityVelocity
            if (p instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity p1 = (SPacketEntityVelocity) p;
                if (processS12Mode.is("InPut")) {
                    var entity = mc.world.getEntityByID(p1.getEntityID());
                    if (entity != null) {
                        if (p1.getEntityID() == mc.player.getEntityId()) {
                            mc.player.setVelocity(
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
            // SPacketExplosion
            if (p instanceof SPacketExplosion) {
                SPacketExplosion p1 = (SPacketExplosion) p;
                if (processS27Mode.is("InPut")) {
                    var explosion = new Explosion(mc.world, target, p1.getX(),
                            p1.getY(), p1.getZ(), p1.getStrength(), p1.getAffectedBlockPositions());
                    explosion.doExplosionB(true);
                    mc.player.setVelocity(
                            p1.getX() + mc.player.motionX,
                            p1.getY() + mc.player.motionY,
                            p1.getZ() + mc.player.motionZ);
                }
                type = "s27";
            }
            // SPacketUpdateHealth
            if (p instanceof SPacketUpdateHealth) {
                SPacketUpdateHealth p1 = (SPacketUpdateHealth) p;
                mc.player.setPlayerSPHealth(p1.getHealth());
                mc.player.getFoodStats().setFoodLevel(p1.getFoodLevel());
                mc.player.getFoodStats().setFoodSaturationLevel(p1.getSaturationLevel());
                type = "s06";
            }
            // SPacketSoundEffect
            if (p instanceof SPacketSoundEffect) {
                SPacketSoundEffect p1 = (SPacketSoundEffect) p;
                mc.world.playSound(p1.getX(), p1.getY(), p1.getZ(),
                        p1.getSound(), p1.getCategory(), p1.getVolume(), p1.getPitch(), false);
                type = "s29";
            }
            // SPacketConfirmTransaction
            if (p instanceof SPacketConfirmTransaction) {
                SPacketConfirmTransaction p1 = (SPacketConfirmTransaction) p;
                var entityplayer = mc.player;
                Container container = null;
                if (p1.getWindowId() == 0) {
                    container = entityplayer.inventoryContainer;
                } else if (p1.getWindowId() == entityplayer.openContainer.windowId) {
                    container = entityplayer.openContainer;
                }
                if (container != null && !p1.wasAccepted()) {
                    CPacketConfirmTransaction packet = new CPacketConfirmTransaction(p1.getWindowId(), p1.getActionNumber(), true);
                    PacketUtil.skip(packet);
                    PacketUtil.sendPacket(packet);
                }
                type = "s32";
            }
            // SPacketEntityHeadLook
            if (p instanceof SPacketEntityHeadLook) {
                SPacketEntityHeadLook p1 = (SPacketEntityHeadLook) p;
                var entity = p1.getEntity(mc.world);
                entity.setRotationYawHead((p1.getYaw() * 360) / 256f);
                type = "s19";
            }
            //SPacketPlayerPosLook
            if (p instanceof SPacketPlayerPosLook) {
                SPacketPlayerPosLook p1 = (SPacketPlayerPosLook) p;
                var entityplayer = mc.player;
                var d0 = p1.getX();
                var d1 = p1.getY();
                var d2 = p1.getZ();
                entityplayer.setPositionAndRotation(d0, d1, d2, mc.player.rotationYaw, mc.player.rotationPitch);
                CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(
                        entityplayer.posX, entityplayer.getEntityBoundingBox().minY,
                        entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false);
                PacketUtil.skip(packet);
                PacketUtil.sendPacket(packet);
                if (mc.player.isOnLadder()) {
                    mc.player.prevPosX = mc.player.posX;
                    mc.player.prevPosY = mc.player.posY;
                    mc.player.prevPosZ = mc.player.posZ;
                    mc.displayGuiScreen(null);
                }
                type = "s08";
            }
            // SPacketSpawnMob
            if (p instanceof SPacketSpawnMob) {
                SPacketSpawnMob p1 = (SPacketSpawnMob) p;
                var d0 = p1.getX() / 32.0;
                var d1 = p1.getY() / 32.0;
                var d2 = p1.getZ() / 32.0;
                var f = (p1.getYaw() * 360) / 256f;
                var f1 = (p1.getPitch() * 360) / 256f;
                var entitylivingbase = (EntityLivingBase) EntityList.createEntityByID(p1.getEntityType(), mc.world);
                if (entitylivingbase != null) {
                    entitylivingbase.serverPosX = (long) p1.getX();
                    entitylivingbase.serverPosY = (long) p1.getY();
                    entitylivingbase.serverPosZ = (long) p1.getZ();
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
                    mc.world.addEntityToWorld(p1.getEntityID(), entitylivingbase);
                    var list = p1.getDataManagerEntries();
                    if (list != null) {
                        // entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
                        entitylivingbase.getDataManager().setEntryValues(list);
                    }
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
            if (p instanceof SPacketEntity) {
                SPacketEntity p1 = (SPacketEntity) p;
                var entity = p1.getEntity(mc.world);
                if (entity == target) {
                    if (target instanceof EntityLivingBase) {
                        distanceToPacket = mc.player.getDistance(target.posX + (p1.getX() / 32.0),
                                target.posY + (p1.getY() / 32.0), target.posZ + (p1.getZ() / 32.0));
                    }
                }
            }
            if (p instanceof SPacketEntityTeleport) {
                SPacketEntityTeleport p1 = (SPacketEntityTeleport) p;
                var entity = mc.world.getEntityByID(p1.getEntityId());
                if (entity == target) {
                    if (target instanceof EntityLivingBase) {
                        distanceToPacket = mc.player.getDistance(p1.getX() / 32.0, p1.getY() / 32.0, p1.getZ() / 32.0);
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
        if (s00.getValue() && packet instanceof SPacketTimeUpdate) {
            return true;
        }
        if (s03.getValue() && packet instanceof SPacketTimeUpdate) {
            return true;
        }
        if (s12.getValue() && packet instanceof SPacketEntityVelocity) {
            return true;
        }
        if (s32.getValue() && packet instanceof SPacketConfirmTransaction) {
            return true;
        }
        if (s27.getValue() && packet instanceof SPacketExplosion) {
            return true;
        }
        return (packet instanceof SPacketEntity
                || packet instanceof SPacketEntityTeleport
                || packet instanceof SPacketEntityHeadLook
                || packet instanceof SPacketPlayerPosLook
                || packet instanceof SPacketSpawnMob);
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
                cXYZ = mc.player.getDistance(cx, cy, cz);
                rXYZ = mc.player.getDistance(realX, realY, realZ);
                pXYZ = mc.player.getDistance(x, y, z);
            }
            if (packet instanceof SPacketEntity) {
                SPacketEntity packet1 = (SPacketEntity) packet;
                var entity = packet1.getEntity(mc.world);
                if (entity == target) {
                    x += packet1.getX() / 32.0;
                    y += packet1.getY() / 32.0;
                    z += packet1.getZ() / 32.0;
                }
            }
            if (packet instanceof SPacketEntityTeleport) {
                SPacketEntityTeleport packet1 = (SPacketEntityTeleport) packet;
                var entity = mc.world.getEntityByID(packet1.getEntityId());
                if (entity == target) {
                    x = packet1.getX() / 32.0;
                    y = packet1.getY() / 32.0;
                    z = packet1.getZ() / 32.0;
                }
            }
            if (mc.player != null && !mc.player.isDead && mc.world != null) {
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
            if (packet instanceof CPacketUseEntity) {
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
            return (target.posY - target.lastTickPosY) > 0 || mc.player.posY <= target.posY
                    || mc.player.onGround;
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
