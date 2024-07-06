package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.managers.BotManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.MovementFix;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT, Keyboard.KEY_R);
        minCps.setCallback((oldV, newV) -> newV > maxCps.getValue() ? oldV : newV);
        maxCps.setCallback((oldV, newV) -> newV < minCps.getValue() ? oldV : newV);
        minRotationSpeed.setCallback((oldV, newV) -> newV > maxRotationSpeed.getValue() ? oldV : newV);
        maxRotationSpeed.setCallback((oldV, newV) -> newV < minRotationSpeed.getValue() ? oldV : newV);
        addValues(maxCps, minCps, searchRange, autoBlock, blockDelay, maxRotationSpeed, minRotationSpeed, autoRod, player, monster, animal, villager, invisibility, death);
    }

    private final NumberValue<Double> searchRange = new NumberValue<>("Range", 3.0, 0.0, 8.0, 0.1);
    private final NumberValue<Double> maxCps = new NumberValue<>("MaxCPS", 8.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> minCps = new NumberValue<>("MinCPS", 6.0, 1.0, 20.0, 1.0);
    private final NumberValue<Double> maxRotationSpeed = new NumberValue<>("MaxRotationSpeed", 60.0, 1.0, 180.0, 5.0);
    private final NumberValue<Double> minRotationSpeed = new NumberValue<>("MinRotationSpeed", 40.0, 1.0, 180.0, 5.0);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
    private final NumberValue<Double> blockDelay = new NumberValue<>("BlockDelay", autoBlock::getValue, 2.0, 1.0, 10.0, 1.0);
    private final BooleanValue autoRod = new BooleanValue("AutoRod", false);
    private final BooleanValue player = new BooleanValue("Player", true);
    private final BooleanValue monster = new BooleanValue("Monster", false);
    private final BooleanValue animal = new BooleanValue("Animal", false);
    private final BooleanValue villager = new BooleanValue("Villager", false);
    private final BooleanValue invisibility = new BooleanValue("Invisibility", false);
    private final BooleanValue death = new BooleanValue("Death", false);

    private final TimerUtil timer = new TimerUtil();
    private EntityLivingBase target = null;
    private boolean blocking = false;
    private boolean fishingRodThrow = false;
    private int fishingRodSwitchOld = 0;

    @Listener
    private void onTick(EventMotion event) {
        try {
            if (mc.world == null || mc.player == null) return;

            if (mc.world.loadedEntityList.isEmpty()) return;

            target = null;

            if (!autoBlock.getValue()) {
                blocking = false;
            }

            for (Entity entity : mc.world.loadedEntityList) {
                if (shouldAddEntity(entity)) {
                    target = (EntityLivingBase) entity;
                    break;
                }
            }

            // Rotations
            if (target != null) {
                float[] rotation = RotationsUtil.getRotationsToEntity(target, true);
                if (maxRotationSpeed.getValue().equals(180.0) && minRotationSpeed.getValue().equals(180.0)) {
                    mc.player.rotationYaw = rotation[0];
                    mc.player.rotationPitch = rotation[1];
                } else {
                    double rotationSpeed = maxRotationSpeed.getValue().equals(minRotationSpeed.getValue()) ? maxRotationSpeed.getValue() : MathUtils.getRandom(minCps.getValue(), maxCps.getValue());
                    Vector2f rotationVec = new Vector2f(rotation[0], rotation[1]);

                    RotationManager.setRotations(rotationVec, rotationSpeed / 18, MovementFix.NORMAL);
                    RotationManager.smooth();

                    mc.player.rotationYaw = RotationManager.rotations.x;
                    mc.player.rotationPitch = RotationManager.rotations.y;
                }
            }

            // Attack & AutoRod
            if (target != null) {
                int cps = (int) (minCps.getValue().equals(maxCps.getValue()) ? maxCps.getValue() : MathUtils.getRandom(minCps.getValue(), maxCps.getValue()));

                if (mc.player.ticksExisted % blockDelay.getValue().intValue() == 0) {
                    startBlock();
                } else {
                    stopBlock();
                }

                if (shouldAttack(cps)) {
                    stopBlock();
                    ReflectUtil.Minecraft$clickMouse(mc);
                    reset();
                }

                if (autoRod.getValue()) {
                    for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
                        if (i > 9) break;

                        ItemStack itemStack = mc.player.inventory.mainInventory.get(i);

                        if (itemStack.getItem() instanceof ItemFishingRod) {
                            if (fishingRodThrow) {
                                ReflectUtil.SetRightClickDelayTimer(mc, 0);
                                ReflectUtil.Minecraft$rightClickMouse(mc);
                                mc.player.inventory.currentItem = fishingRodSwitchOld;
                                fishingRodThrow = false;
                            } else {
                                fishingRodSwitchOld = mc.player.inventory.currentItem;
                                mc.player.inventory.currentItem = i;
                                ReflectUtil.SetRightClickDelayTimer(mc, 0);
                                ReflectUtil.Minecraft$rightClickMouse(mc);
                                fishingRodThrow = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                RotationManager.resetRotation(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch));
                stopBlock();
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void startBlock() {
        if (autoBlock.getValue() && !blocking) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                Natives.SendRight(true);
                blocking = true;
            }
        }
    }

    private void stopBlock() {
        if (autoBlock.getValue() && blocking) {
            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword) {
                Natives.SendRight(false);
                blocking = false;
            }
        }
    }

    @Override
    protected void onEnable() {
        if (mc.world == null || mc.player == null) return;

//        RotationManager.resetRotation(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch));
    }

    @Override
    protected void onDisable() {
        if (mc.world == null || mc.player == null) return;

        stopBlock();
    }

    private boolean shouldAddEntity(Entity entity) {
        if (entity == mc.player) return false;

        if (!(entity instanceof EntityLivingBase)) return false;

        if (!death.getValue() && !entity.isEntityAlive()) return false;

        if (mc.player.getDistanceToEntity(entity) > searchRange.getValue()) return false;

        if (YolBi.instance.getModuleManager().getModule(AntiBot.class).isEnabled() && BotManager.bots.contains(entity))
            return false;

        if (!invisibility.getValue() && entity.isInvisible()) return false;

        if (player.getValue() && entity instanceof EntityPlayer) {
            return true;
        }

        if (monster.getValue() && entity instanceof EntityMob) {
            return true;
        }

        if (animal.getValue() && entity instanceof EntityAnimal) {
            return true;
        }

        return villager.getValue() && entity instanceof EntityVillager;
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
        return searchRange.getValue() + " | " + minCps.getValue() + "-" + maxCps.getValue();
    }
}
