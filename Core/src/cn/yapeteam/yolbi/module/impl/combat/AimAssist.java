package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.ColorValue;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.math.MathConst;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.player.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@ModuleInfo(name = "AimAssist", category = ModuleCategory.COMBAT)
public class AimAssist extends Module {

    private final Comparator<Entity> angleComparator = Comparator.comparingDouble(entity -> RotationManager.getRotationsNeeded(entity)[0]);

    private final NumberValue Range = new NumberValue("Aim Range",5,3,10,1);

    public final ModeValue TargetPrority = new ModeValue("Target Prority", "Clip","Health","Distance","Angle","Clip");

    private final BooleanValue Weapons = new BooleanValue("Only Weapons",false);

    private final BooleanValue View = new BooleanValue("In View",true);

    private final NumberValue<Integer> HorizontalStrength = new NumberValue<>("Horizontal Strength",10, 1, 100, 1);
    private final NumberValue<Integer> VerticalStrength = new NumberValue<>("Vertical Strength", 45, 1, 100, 1);

    private final NumberValue PitchOffset = new NumberValue("Above or below waist",0.15, -1.7, 0.25, 0.050D);

    Entity target;

    private float randomYaw, randomPitch;


    public AimAssist() {
        addValues(Range, TargetPrority, Weapons, View, HorizontalStrength, VerticalStrength, PitchOffset);
    }


    // do render
    @Listener
    public void onUpdate(EventUpdate eventUpdate){
        if(!TargetPrority.getValue().toString().contains("Clip")){
            target = getTargets();
        }
    };



    @Listener
    public void ontick(EventTick eventTick){
        if(TargetPrority.getValue().toString().contains("Clip")){
            try{
                target = PlayerUtil.getMouseOver(1,Range.getValue().intValue());
            }catch (NullPointerException e){
                // just catch just in case

            }
        }
    };

    @Listener
    public void onRender(EventRender2D event){
        Vector2f rotations;
        if(!TargetPrority.getValue().toString().contains("Clip")){
            target = getTargets();

        }
        if(target != null){
            try{
                final Vector2f targetRotations = RotationManager.calculate(target);
                double n = PlayerUtil.fovFromEntity(target);

                double complimentSpeedX = n
                        * (ThreadLocalRandom.current().nextDouble( HorizontalStrength.getValue() - 1.47328,
                         HorizontalStrength.getValue() + 2.48293) / 100);
                float valX = (float) (-(complimentSpeedX + (n / (101.0D - (float) ThreadLocalRandom.current()
                        .nextDouble(HorizontalStrength.getValue().doubleValue() - 4.723847, HorizontalStrength.getValue().doubleValue())))));

                double ry = mc.thePlayer.rotationYaw;
                // you want to handle a variable to smooth instead of adding in a function because that changes the yaw and becomes very weird
                //mc.thePlayer.rotationYaw += valX;
                rotations = RotationManager.getsmoothrot(new Vector2f((float) ry, mc.thePlayer.rotationPitch),(new Vector2f((float) ry+valX, mc.thePlayer.rotationPitch)), HorizontalStrength.getValue());
                mc.thePlayer.rotationYaw = rotations.x;

                double complimentSpeed = PlayerUtil.PitchFromEntity(target,
                        (float) PitchOffset.getValue().doubleValue())
                        * (ThreadLocalRandom.current().nextDouble( VerticalStrength.getValue() - 1.47328,
                         VerticalStrength.getValue() + 2.48293) / 100);

                float val = (float) (-(complimentSpeed
                        + (n / (101.0D - (float) ThreadLocalRandom.current()
                        .nextDouble(VerticalStrength.getValue().doubleValue() - 4.723847,
                                VerticalStrength.getValue().doubleValue())))));

//                mc.thePlayer.rotationPitch += val;


//
//                // rotations that bypasses checks
                randomiseTargetRotations();
                targetRotations.x += randomYaw;
                targetRotations.y += randomPitch;
                Logger.log("AimAssist", "Target: " + target.getName() + " Rotation: " + targetRotations.x + " " + targetRotations.y);
                Logger.log("AimAssist", "Player: " + mc.thePlayer.getName() + " Rotation: " + mc.thePlayer.rotationYaw + " " + mc.thePlayer.rotationPitch);
                RotationManager.setRotations(targetRotations,  HorizontalStrength.getValue(), MovementFix.NORMAL);
                RotationManager.smooth();
            }catch (NullPointerException e){
                // this is for when joining world
            }



        }
    };

    public Entity getTargets(){
        // define targets first to eliminate any null pointer exceptions
        List<Entity> targets = TargetManager.getTargets(Range.getValue().intValue());
        if(View.getValue()){
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .filter(RayCastUtil::isInViewFrustrum)
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().toString().contains("Health")){
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .sorted(Comparator.comparingDouble(o -> ((AbstractClientPlayer) o).getHealth()).reversed())
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().toString().contains("Distance")) {
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .sorted(Comparator.comparingDouble(o -> mc.thePlayer.getDistanceToEntity((Entity) o)).reversed())
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().toString().contains("Angle")) {
            targets = TargetManager.getTargets(Range.getValue().intValue());
            targets.sort(this.angleComparator);
        }

        if(!targets.isEmpty()){
            return targets.get(0);
        }else{
            return null;
        }
    };

    /*
     * Randomising rotation target to simulate legit players
     */
    private void randomiseTargetRotations() {
        randomYaw += (float) (Math.random() * 0.2 - 0.1);
        randomPitch += (float) (Math.random() - 0.5f) * 2;
    }
}
