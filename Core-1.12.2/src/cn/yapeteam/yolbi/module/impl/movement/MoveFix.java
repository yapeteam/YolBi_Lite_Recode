package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventStrafe;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.RotationManager;
import net.minecraft.util.math.MathHelper;

public class MoveFix extends Module {
    public MoveFix() {
        super("MoveFix",ModuleCategory.MOVEMENT);
    }
    @Listener
    public void onStrafe(EventStrafe strafe){
        if(RotationManager.active){
            fix2(strafe);
        }
    }
    public static void fix2(EventStrafe event){
        if((event.getForward()*event.getForward()+ event.getStrafe()*event.getStrafe())!=0)
        {
            EventStrafe e=yawToStrafe(RotationManager.rotations.y,direction(event.getYaw()));
            event.setStrafe(e.getStrafe()*0.98f);
            event.setForward(e.getForward()*0.98f);
            event.setYaw(RotationManager.rotations.y);
        }
    }
    public static float direction(float yaw) {
        float rotationYaw = yaw;

        if (mc.player.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return rotationYaw;
    }
    public static EventStrafe yawToStrafe(float playerYaw,float moveYaw){
        int angleDiff = (int) ((MathHelper.wrapDegrees(moveYaw - playerYaw - 22.5f - 135.0f) + 180.0d) / (45.0d));
        EventStrafe event=new EventStrafe(0,0,0,0);
        switch (angleDiff){
            case 0:
                event.forward=1;
                event.strafe=0;
                break;
            case 1:
                event.forward=1;
                event.strafe=-1;
                break;
            case 2:
                event.forward=0;
                event.strafe=-1;
                break;
            case 3:
                event.forward=-1;
                event.strafe=-1;
                break;
            case 4:
                event.forward=-1;
                event.strafe=0;
                break;
            case 5:
                event.forward=-1;
                event.strafe=1;
                break;
            case 6:
                event.forward=0;
                event.strafe=1;
                break;
            case 7:
                event.forward=1;
                event.strafe=1;
                break;

        }
        if(mc.player.movementInput.sneak)event.slow(0.3f);
        System.out.println(angleDiff+" "+event.forward+" "+event.strafe );
        return event;
    }



}
