package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import lombok.Getter;
import lombok.val;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT);
        minRotationSpeed.setCallback((oldV, newV) -> newV > maxRotationSpeed.getValue() ? oldV : newV);
        maxRotationSpeed.setCallback((oldV, newV) -> newV < minRotationSpeed.getValue() ? oldV : newV);
        addValues(cps, cpsRange, searchRange, autoBlock, blockDelay, maxRotationSpeed, minRotationSpeed, autoRod, invisibility, death);
    }

    private final NumberValue<Double> searchRange = new NumberValue<>("Range", 3.0, 0.0, 8.0, 0.1);
    private final NumberValue<Double> cps = new NumberValue<>("CPS", 8.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> cpsRange = new NumberValue<>("Random Tick", 1.5, 0.1, 5.0, 0.1);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("MaxRotationSpeed", 60.0, 1.0, 180.0, 5.0);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("MinRotationSpeed", 40.0, 1.0, 180.0, 5.0);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
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
            if (mc.theWorld == null || mc.thePlayer == null) return;
            if (mc.theWorld.loadedEntityList.isEmpty()) return;
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

                if (mc.thePlayer.ticksExisted % blockDelay.getValue().intValue() == 0) {
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
                    for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                        if (i > 9) break;

                        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

                        if (itemStack != null && itemStack.getItem() instanceof ItemFishingRod) {
                            if (fishingRodThrow) {
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                mc.thePlayer.inventory.currentItem = fishingRodSwitchOld;
                                fishingRodThrow = false;
                            } else {
                                fishingRodSwitchOld = mc.thePlayer.inventory.currentItem;
                                mc.thePlayer.inventory.currentItem = i;
                                Natives.SendRight(true);
                                Natives.SendRight(false);
                                fishingRodThrow = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                if (RotationManager.active)
                    RotationManager.stop();
                stopBlock();
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void startBlock() {
        if (autoBlock.getValue() && !blocking) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                Natives.SendRight(true);
                blocking = true;
            }
        }
    }

    private void stopBlock() {
        if (autoBlock.getValue() && blocking) {
            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                Natives.SendRight(false);
                blocking = false;
            }
        }
    }

    @Override
    protected void onEnable() {
        if (mc.theWorld == null || mc.thePlayer == null) {
            setEnabled(false);
        }
        mc.thePlayer.addChatMessage(new ChatComponentText(
                String.format("&6[Yolbi] &3You are using a blatant module and it will not get bypassed")
        ));
    }

    @Override
    protected void onDisable() {
        if (mc.theWorld == null || mc.thePlayer == null) return;
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
