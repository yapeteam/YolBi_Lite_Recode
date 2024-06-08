package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.vector.Vector2f;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.player.RayCastUtil;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import cn.yapeteam.yolbi.utils.player.TargetManager;
import cn.yapeteam.yolbi.utils.player.WindPosMapper;
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

    private final NumberValue<Float> Speed = new NumberValue<>("Speed", 1f, 1f, 10f, 0.1f);


    public AimAssist() {
        addValues(Range, TargetPriority, View, Speed);
    }

    private final List<Vector2f> aimPath = new ArrayList<>();

    @Override
    protected void onDisable() {
        aimPath.clear();
    }

    private Entity target = null;

    @Listener
    private void onTick(EventTick e) {
        target = getTargets();
        if (target != null)
            aimPath.addAll(WindPosMapper.generatePath(new Vector2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), RotationManager.calculate(target)));
    }

    @Listener
    public void onRender(EventRender2D event) {
        YolBi.instance.getFontManager().getPingFang12().drawString("Target: " + (target == null ? "None" : target.getName()), 10, 10, 0xFFFFFF);
        try {
            if (!aimPath.isEmpty()) {
                int length = (int) (aimPath.size() * Speed.getValue() / 10);
                if (length > aimPath.size()) {
                    length = aimPath.size();
                }
                for (int i = 0; i < length; i++) {
                    mc.thePlayer.rotationYaw = aimPath.get(i).getX();
                    mc.thePlayer.rotationPitch = aimPath.get(i).getY();
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
        System.out.println(targets.size());
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
