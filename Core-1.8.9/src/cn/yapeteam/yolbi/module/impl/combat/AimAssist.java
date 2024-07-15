package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.managers.TargetManager;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import cn.yapeteam.yolbi.utils.player.*;
import cn.yapeteam.yolbi.utils.vector.Vector2f;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AimAssist extends Module {
    private final NumberValue<Integer> Range = new NumberValue<>("Aim Range", 5, 3, 10, 1);

    public final ModeValue<String> TargetPriority = new ModeValue<>("Target Priority", "Distance", "Distance", "Health", "Angle", "Clip");
    private final BooleanValue View = new BooleanValue("In View", true);
    private final BooleanValue WeaponOnly = new BooleanValue("Weapon Only", true);
    private final BooleanValue ClickAim = new BooleanValue("Click Aim", true);
    private final NumberValue<Float> Strength = new NumberValue<>("Strength", 100f, 1f, 180f, .5f);


    public AimAssist() {
        super("AimAssist", ModuleCategory.COMBAT);
        addValues(Range, TargetPriority, ClickAim, WeaponOnly, View, Strength);
    }

    private final List<Vector2f> aimPath = new ArrayList<>();

    @Override
    protected void onDisable() {
        aimPath.clear();
    }

    private Entity target = null;

    @Listener
    private void onTick(EventTick e) {
        if (mc.thePlayer == null)
            return;
        if (mc.currentScreen != null) return;
        if (target != null && (target.isDead | target.getDistanceSqToEntity(mc.thePlayer) > Range.getValue()))
            target = null;
        if (TargetPriority.is("Clip"))
            target = PlayerUtil.getMouseOver(1, Range.getValue());
        else{
            target = getTargets();
        }
    }

    @Listener
    public void onUpdate(EventRender2D event) {
        if (mc.currentScreen == null && mc.inGameHasFocus) {
            if (!WeaponOnly.getValue() || PlayerUtil.holdingWeapon()) {
                if (!(ClickAim.getValue() && !Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON))) {
                    if (target != null) {
//                        double n = PlayerUtil.calculateHorizontalAngleDifference(target);
//                        if (n > 1.0D || n < -1.0D) {
//                            float val = (float) (-(n / (101.0D - (Strength.getValue()))));
//                            mc.thePlayer.rotationYaw += val;
//                        }
                        Vector2f targetrot = RotationManager.calculate(target);
                        RotationManager.setRotations(RotationManager.calculate(target),10*0.01* Strength.getValue());
                    }

                }
            }
        }
    }

    @Override
    public String getSuffix() {
        return " Path: " + aimPath.size() + " Yaw: " + mc.thePlayer.rotationYaw + " Pitch: " + mc.thePlayer.rotationPitch;
    }

    public Entity getTargets() {
        // define targets first to eliminate any null pointer exceptions
        List<Entity> targets = TargetManager.getTargets(Range.getValue());
        if (View.getValue())
            targets = targets.stream()
                    .filter(RayCastUtil::isInViewFrustrum)
                    .collect(Collectors.toList());
        if (TargetPriority.is("Distance"))
            targets.sort(Comparator.comparingDouble(o -> mc.thePlayer.getDistanceToEntity(o)));
        else if (TargetPriority.is("Health"))
            targets.sort(Comparator.comparingDouble(o -> ((EntityLivingBase) o).getHealth()));
        else if (TargetPriority.is("Angle"))
            targets.sort(Comparator.comparingDouble(entity -> RotationManager.getRotationsNeeded(entity)[0]));
        return targets.isEmpty() ? null : targets.get(0);
    }
}
