package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Overwrite;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventJump;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @author yuxiangll
 * @since 2024/1/7 21:39
 * IntelliJ IDEA
 */
@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase extends EntityLivingBase {
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public float getJumpUpwardsMotion() {
        return 0.42F;
    }

    @Shadow
    public boolean isPotionActive(Potion potionIn) {
        return false;
    }

    @Shadow
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return null;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot entityEquipmentSlot) {
        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {

    }

    @Shadow
    public float rotationYaw;

    @Shadow
    public boolean isSprinting() {
        return false;
    }

    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public boolean isAirBorne;

    @Overwrite(
            method = "jump",
            desc = "()V"
    )
    protected void jump() {
        this.motionY = this.getJumpUpwardsMotion();

        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
            this.motionY += (float) (this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
        }

        EventJump event = new EventJump(motionY, this.rotationYaw, this.isSprinting());
        //noinspection ConstantValue
        if ((EntityLivingBase) this == Minecraft.getMinecraft().player)
            YolBi.instance.getEventManager().post(event);
        this.motionY = event.getMotionY();

        if (event.isBoosting()) {
            float f = event.getYaw() * 0.017453292F;
            this.motionX -= MathHelper.sin(f) * 0.2F;
            this.motionZ += MathHelper.cos(f) * 0.2F;
        }

        this.isAirBorne = true;
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return null;
    }
}
