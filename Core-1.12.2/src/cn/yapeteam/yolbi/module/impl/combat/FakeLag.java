package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeLag extends Module {
    private final NumberValue<Double> delay = new NumberValue<>("Lag Delay", 100.0, 50.0, 2000.0, 50.0);
    private final BooleanValue render = new BooleanValue("Render", false);
    private final List<Packet<INetHandlerPlayServer>> packetList = new CopyOnWriteArrayList<>();
    private final TimerUtil timer = new TimerUtil();

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean attacked = false;

    public FakeLag() {
        super("FakeLag", ModuleCategory.COMBAT);
        addValues(delay, render);
    }

    @Override
    protected void onEnable() {
        attacked = false;
    }

    @Listener
    private void onAttack(EventAttack event) {
        attacked = true;
    }

    @Listener
    private void onTick(EventTick event) {
        if (mc.player == null || mc.world == null)
            return;

        if (attacked || mc.player.hurtTime > 0 || timer.hasTimePassed(delay.getValue().longValue())) {
            if (!this.packetList.isEmpty()) {
                for (Packet<INetHandlerPlayServer> packet : this.packetList) {
                    if (packet instanceof CPacketPlayer) {
                        //todo: what the fuck is the default value
                        x = ((CPacketPlayer) packet).getX(0);
                        y = ((CPacketPlayer) packet).getY(0);
                        z = ((CPacketPlayer) packet).getZ(0);
                        yaw = ((CPacketPlayer) packet).getYaw(0);
                        pitch = ((CPacketPlayer) packet).getPitch(0);
                    }

                    PacketUtil.sendPacketNoEvent(packet);
                    this.packetList.remove(packet);
                }
            }

            if (attacked) {
                attacked = false;
            }

            this.timer.reset();
        }
    }

    @Listener
    private void onPacketSend(EventPacketSend event) {
        Packet<INetHandlerPlayServer> packet = event.getPacket();

        if (packet instanceof CPacketPlayer || (packet instanceof CPacketUseEntity && ((CPacketUseEntity) packet).getAction() == CPacketUseEntity.Action.ATTACK) || packet instanceof CPacketEntityAction || packet instanceof CPacketAnimation) {
            packetList.add(packet);
            event.setCancelled(true);
        }
    }

    @Listener
    private void onRender3D(EventRender3D event) {
        if (render.getValue()) {
            if (mc.gameSettings.thirdPersonView == 1 || mc.gameSettings.thirdPersonView == 2) {
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                renderFrozenEntity(event);
                GlStateManager.enableAlpha();
                GlStateManager.resetColor();
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderFrozenEntity(EventRender3D event) {
        EntityOtherPlayerMP mp = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        mp.posX = this.x;
        mp.posY = this.y;
        mp.posZ = this.z;
        mp.prevPosX = mp.posX;
        mp.prevPosY = mp.posY;
        mp.prevPosZ = mp.posZ;
        mp.lastTickPosX = mp.posX;
        mp.lastTickPosY = mp.posY;
        mp.lastTickPosZ = mp.posZ;
        mp.rotationYaw = this.yaw;
        mp.rotationPitch = this.pitch;
        mp.rotationYawHead = mc.player.rotationYawHead;
        mp.prevRotationYaw = this.yaw;
        mp.prevRotationPitch = this.pitch;
        mp.prevRotationYawHead = mc.player.prevRotationYawHead;
        mp.swingProgress = mc.player.swingProgress;
        mp.swingProgressInt = mc.player.swingProgressInt;
        mc.getRenderManager().renderEntityStatic(mp, event.getPartialTicks(), false);
    }
}
