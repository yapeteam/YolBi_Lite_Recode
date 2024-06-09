package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.vector.Vector2f;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.player.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "AimAssist", category = ModuleCategory.COMBAT)
public class AimAssist extends Module {
    private final NumberValue<Integer> Range = new NumberValue<>("Aim Range", 5, 3, 10, 1);

    public final ModeValue<String> TargetPriority = new ModeValue<>("Target Priority", "Distance", "Distance", "Health", "Angle");

    private final BooleanValue View = new BooleanValue("In View", true);
    private final BooleanValue ClickAim = new BooleanValue("Click Aim", true);

    private final NumberValue<Float> Speed = new NumberValue<>("Speed", 50f, 40f, 100f, 0.5f);


    public AimAssist() {
        addValues(Range, TargetPriority, ClickAim, View, Speed);
    }

    private final List<Vector2f> aimPath = new ArrayList<>();

    @Override
    protected void onDisable() {
        aimPath.clear();
    }

    @Listener
    private void onTick(EventTick e) {
        try {
            if (mc.thePlayer == null)
                return;
            if (mc.currentScreen != null) return;
            Entity target = getTargets();
            if (TargetPriority.is("Clip")) {
                target = PlayerUtil.getMouseOver(1, Range.getValue());
            }
            if (target != null && !(ClickAim.getValue() && !Natives.IsMouseDown(0)))
                aimPath.addAll(WindPosMapper.generatePath(new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), RotationManager.calculate(target)));
        } catch (Throwable ex) {
            Logger.exception(ex);
        }
    }

    @Listener
    public void onRender(EventRender2D event) {
        try {
            if (mc.currentScreen != null) return;
            if (!aimPath.isEmpty() && !(ClickAim.getValue() && !Natives.IsMouseDown(0))) {
                int length = (int) (aimPath.size() * Speed.getValue() / 100);
                if (length > aimPath.size())
                    length = aimPath.size();
                for (int i = 0; i < length; i++) {
                    Vector2f rotations = aimPath.get(i);
                    mc.thePlayer.rotationYaw = rotations.x;
                    mc.thePlayer.rotationPitch = rotations.y;
                    RotationManager.setRotations(rotations, Speed.getValue(), MovementFix.NORMAL);
                    RotationManager.smooth();
                }
                aimPath.subList(0, length).clear();
            }
        } catch (Throwable e) {
            Logger.exception(e);
        }
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
            targets.sort(Comparator.comparingDouble(o -> ((AbstractClientPlayer) o).getHealth()).reversed());
        else if (TargetPriority.is("Angle"))
            targets.sort(Comparator.comparingDouble(entity -> RotationManager.getRotationsNeeded(entity)[0]));
        return targets.isEmpty() ? null : targets.get(0);
    }
}
