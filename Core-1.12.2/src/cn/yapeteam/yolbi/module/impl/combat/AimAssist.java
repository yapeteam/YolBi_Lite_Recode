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
    private final BooleanValue ClickAim = new BooleanValue("Click Aim", true);

    private final NumberValue<Float> calcSpeed = new NumberValue<>("Calculation Speed", 50f, 40f, 100f, 0.5f);

    private final NumberValue<Float> rotSpeed = new NumberValue<>("Rotation Speed", 100f, 1f, 180f, .5f);

    public AimAssist() {
        super("AimAssist", ModuleCategory.COMBAT);
        addValues(Range, TargetPriority, ClickAim, View, calcSpeed, rotSpeed);
    }

    private final List<Vector2f> aimPath = new ArrayList<>();

    @Override
    protected void onDisable() {
        aimPath.clear();
    }

    private Entity target = null;

    @Listener
    private void onTick(EventTick e) {
        try {
            if (mc.player == null)
                return;
            if (mc.currentScreen != null) return;
            Entity target;
            if (TargetPriority.is("Clip"))
                target = PlayerUtil.getMouseOver(1, Range.getValue());
            else target = getTargets();
            if (this.target != target) {
                aimPath.clear();
                this.target = target;
            }
            if (target != null && !(ClickAim.getValue() && !Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON))) {
                val vector2fs = WindPosMapper.generatePath(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), RotationManager.calculate(target));
                aimPath.addAll(vector2fs);
                // no checks needed since we only use the first few points
            }
        } catch (Throwable ex) {
            Logger.exception(ex);
        }
    }

    @Listener
    public void onUpdate(EventRender2D event) {
        try {
            if (mc.currentScreen == null && !aimPath.isEmpty() && !(ClickAim.getValue() && !Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON))) {
                int length = (int) (aimPath.size() * calcSpeed.getValue() / 100);
                if (length > aimPath.size())
                    length = aimPath.size();
                for (int i = 0; i < length; i++) {
                    Vector2f rotations = aimPath.get(i);
                    RotationManager.setRotations(rotations, rotSpeed.getValue());
                    RotationManager.smooth();
                    mc.player.setSprinting(false);
                }
                aimPath.subList(0, length).clear();
            } else {
                RotationManager.setRotations(new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch), rotSpeed.getValue());
                RotationManager.smooth();
            }
        } catch (Throwable e) {
            Logger.exception(e);
        }
    }

    @Override
    public String getSuffix() {
        return " Path: " + aimPath.size() + " Yaw: " + mc.player.rotationYaw + " Pitch: " + mc.player.rotationPitch;
    }

    public Entity getTargets() {
        // define targets first to eliminate any null pointer exceptions
        List<Entity> targets = TargetManager.getTargets(Range.getValue());
        if (View.getValue())
            targets = targets.stream()
                    .filter(RayCastUtil::isInViewFrustrum)
                    .collect(Collectors.toList());
        if (TargetPriority.is("Distance"))
            targets.sort(Comparator.comparingDouble(o -> mc.player.getDistanceToEntity(o)));
        else if (TargetPriority.is("Health"))
            targets.sort(Comparator.comparingDouble(o -> ((EntityLivingBase) o).getHealth()));
        else if (TargetPriority.is("Angle"))
            targets.sort(Comparator.comparingDouble(entity -> RotationManager.getRotationsNeeded(entity)[0]));
        return targets.isEmpty() ? null : targets.get(0);
    }
}
