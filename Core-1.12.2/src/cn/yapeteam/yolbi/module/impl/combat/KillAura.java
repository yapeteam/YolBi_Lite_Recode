package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import lombok.Getter;
import lombok.val;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

import static net.minecraft.util.EnumHand.OFF_HAND;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT);
        minRotationSpeed.setCallback((oldV, newV) -> newV > maxRotationSpeed.getValue() ? oldV : newV);
        maxRotationSpeed.setCallback((oldV, newV) -> newV < minRotationSpeed.getValue() ? oldV : newV);
        addValues(cps, cpsRange, searchRange, autoBlock, mode, blockDelay, maxRotationSpeed, minRotationSpeed, autoRod, invisibility, death);
    }

    private final NumberValue<Double> searchRange = new NumberValue<>("Range", 3.0, 0.0, 8.0, 0.1);
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 8.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> cpsRange = new NumberValue<>("Random Tick", 1.5, 0.1, 5.0, 0.1);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("MaxRotationSpeed", 60.0, 1.0, 180.0, 5.0);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("MinRotationSpeed", 40.0, 1.0, 180.0, 5.0);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
    private final ModeValue<String> mode = new ModeValue<>("Autoblock methods.", "Balant", "Balant", "Anticheat");
    private final NumberValue<Double> blockDelay = new NumberValue<>("BlockDelay", autoBlock::getValue, 2.0, 1.0, 10.0, 1.0);
    private final BooleanValue autoRod = new BooleanValue("AutoRod", false);
    private final BooleanValue invisibility = new BooleanValue("Invisibility", false);
    private final BooleanValue death = new BooleanValue("Death", false);
    private final TimerUtil timer = new TimerUtil();
    @Getter
    private EntityLivingBase target = null;
    private boolean blocking = false;
    private boolean fishingRodThrow = false;
    private int fishingRodSwitchOld = 0;

    @Listener
    private void onUpdate(EventRender2D event) {
        try {
            if (mc.world == null || mc.player == null) return;
            if (mc.world.loadedEntityList.isEmpty()) return;
            if (mc.currentScreen != null) return;
            target = null;
            if (!autoBlock.getValue())
                blocking = false;

            val targetList = TargetManager.getTargets(searchRange.getValue());
            targetList.removeIf(entity -> invisibility.getValue() && entity.isInvisible() || death.getValue() && entity.isDead);
            if (!targetList.isEmpty()) target = (EntityLivingBase) targetList.get(0);

            double rotationSpeed = MathUtils.getRandom(maxRotationSpeed.getValue(), minRotationSpeed.getValue());

            // Rotations
            if (target != null) {
                float[] rotation = RotationsUtil.getRotationsToEntity(target, true);
                Vector2f rotationVec = new Vector2f(rotation[0], rotation[1]);

                RotationManager.setRotations(rotationVec, rotationSpeed);
                RotationManager.smooth();
            }

            // Attack & AutoRod
            if (target != null) {
                int cps = (int) AutoClicker.generate(this.cps.getValue(), cpsRange.getValue());

                if (mc.player.ticksExisted % blockDelay.getValue().intValue() == 0) {
                    startBlock();
                } else {
                    stopBlock();
                }

                if (shouldAttack(cps)) {
                    stopBlock();
                    Natives.SendLeft(true);
                    Natives.SendLeft(false);
                    reset();
                }

                if (autoRod.getValue()) {
                    for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
                        if (i > 9) break;

                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                        if (itemStack != null && itemStack.getItem() instanceof ItemFishingRod) {
                            if (fishingRodThrow) {
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                mc.player.inventory.currentItem = fishingRodSwitchOld;
                                fishingRodThrow = false;
                            } else {
                                fishingRodSwitchOld = mc.player.inventory.currentItem;
                                mc.player.inventory.currentItem = i;
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                fishingRodThrow = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                RotationManager.setRotations(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), rotationSpeed);
                RotationManager.smooth();
                stopBlock();
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void startBlock() {
        if (autoBlock.getValue()) {
            if (this.mode.is("Balant")) {
                if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                    Natives.SendRight(true);
                }
            } else if (this.mode.is("Anticheat")) {
                if (!blocking) {
                    ItemStack shield = new ItemStack(Items.SHIELD);
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) {
                        mc.player.inventory.offHandInventory.set(0, shield);
                    }
                }
                mc.getConnection().sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
                mc.getConnection().sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(OFF_HAND));
            }
            blocking = true;

        }
    }

    private void stopBlock() {
        if (autoBlock.getValue() && blocking) {
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemShield) {
                mc.player.inventory.offHandInventory.set(0, ItemStack.field_190927_a);
            }
            blocking = false;
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                if (this.mode.is("Balant")) {
                    Natives.SendRight(false);
                } else if (this.mode.is("Anticheat")) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem % 8 + 1));
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                }
            }
        }
    }

    @Override
    protected void onEnable() {
        if (mc.world == null || mc.player == null) {
            setEnabled(false);
        }
    }

    @Override
    protected void onDisable() {
        if (mc.world == null || mc.player == null) return;
        stopBlock();
        if (RotationManager.active)
            RotationManager.stop();
    }

    @Listener
    private void onWorldLoad(EventLoadWorld e) {
        setEnabled(false);
    }

    private boolean shouldAttack(int cps) {
        int aps = 20 / cps;
        return timer.hasTimePassed(50 * aps);
    }

    private void reset() {
        timer.reset();
    }

    @Override
    public String getSuffix() {
        return searchRange.getValue() + " | " + (cps.getValue() - cpsRange.getValue()) + " ~ " + (cps.getValue() + cpsRange.getValue());
    }
}
