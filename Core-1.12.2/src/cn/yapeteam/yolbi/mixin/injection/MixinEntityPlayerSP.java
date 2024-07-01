package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.*;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventChat;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.player.EventPostMotion;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends EntityPlayerSP {
    public MixinEntityPlayerSP(Minecraft p_i47378_1_, World p_i47378_2_, NetHandlerPlayClient p_i47378_3_, StatisticsManager p_i47378_4_, RecipeBook p_i47378_5_) {
        super(p_i47378_1_, p_i47378_2_, p_i47378_3_, p_i47378_4_, p_i47378_5_);
    }

    @Inject(
            method = "onUpdate", desc = "()V",
            target = @Target(
                    value = "INVOKEVIRTUAL",
                    target = "net/minecraft/client/entity/EntityPlayerSP.isRiding()Z",
                    shift = Target.Shift.BEFORE
            )
    )
    public void onUpdate() {
        RotationManager.prevRenderPitchHead = RotationManager.renderPitchHead;
        RotationManager.renderPitchHead = rotationPitch;
        YolBi.instance.getEventManager().post(new EventUpdate());
    }

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public NetHandlerPlayClient sendQueue = null;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    public Entity ridingEntity;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private boolean serverSneakState;

    @Shadow
    public AxisAlignedBB getEntityBoundingBox() {
        return null;
    }

    @Shadow
    protected boolean isCurrentViewEntity() {
        return false;
    }

    @Shadow
    public boolean isSprinting() {
        return false;
    }

    @Shadow
    public boolean isSneaking() {
        return false;
    }

    @Overwrite(method = "onUpdateWalkingPlayer", desc = "()V")
    public void onUpdateWalkingPlayer() {
        boolean flag = this.isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking();
        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.sendQueue.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
        }
        EventMotion motionEvent = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        YolBi.instance.getEventManager().post(motionEvent);
        if (this.isCurrentViewEntity()) {
            double d0 = motionEvent.getX() - this.lastReportedPosX;
            double d1 = motionEvent.getY() - this.lastReportedPosY;
            double d2 = motionEvent.getZ() - this.lastReportedPosZ;
            double d3 = motionEvent.getYaw() - this.lastReportedYaw;
            double d4 = motionEvent.getPitch() - this.lastReportedPitch;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.sendPacket(new CPacketPlayer.PositionRotation(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                } else if (flag2) {
                    this.sendQueue.sendPacket(new CPacketPlayer.Position(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.isOnGround()));
                } else if (flag3) {
                    this.sendQueue.sendPacket(new CPacketPlayer.Rotation(motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                } else {
                    this.sendQueue.sendPacket(new CPacketPlayer(motionEvent.isOnGround()));
                }
            } else {
                this.sendQueue.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                flag2 = false;
            }

            ++this.positionUpdateTicks;

            if (flag2) {
                this.lastReportedPosX = motionEvent.getX();
                this.lastReportedPosY = motionEvent.getY();
                this.lastReportedPosZ = motionEvent.getZ();
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = motionEvent.getYaw();
                this.lastReportedPitch = motionEvent.getPitch();
            }
        }
        YolBi.instance.getEventManager().post(new EventPostMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
    }

    @Overwrite(method = "sendChatMessage", desc = "(Ljava/lang/String;)V")
    public void sendChatMessage(String message) {
        EventChat event = new EventChat(message);
        YolBi.instance.getEventManager().post(event);
        if (!event.isCancelled()) {
            message = event.getMessage();
            this.sendQueue.sendPacket(new CPacketChatMessage(message));
        }
    }
}