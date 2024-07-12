package cn.yapeteam.yolbi.mixin.injection;

import cn.yapeteam.ymixin.annotations.Inject;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.ymixin.annotations.Shadow;
import cn.yapeteam.ymixin.annotations.Target;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.impl.player.EventMoveInput;
import cn.yapeteam.yolbi.utils.misc.ObjectStore;
import net.minecraft.util.MovementInputFromOptions;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {
    @Shadow
    public float moveStrafe;
    @Shadow
    public float moveForward;
    @Shadow
    public boolean jump;
    @Shadow
    public boolean sneak;

    @Inject(
            method = "updatePlayerMoveState",
            desc = "()V",
            target = @Target(
                    value = "PUTFIELD",
                    target = "net/minecraft/util/MovementInputFromOptions.sneak Z",
                    shift = Target.Shift.AFTER
            )
    )
    public void updatePlayerMoveState() {
        final EventMoveInput event = new EventMoveInput(moveForward, moveStrafe, jump, sneak, 0.3D);
        YolBi.instance.getEventManager().post(event);
        final double sneakMultiplier = event.getSneakSlowDownMultiplier();
        ObjectStore.objects.put("sneakMultiplier", sneakMultiplier);
        ObjectStore.objects.put("moveStrafe", this.moveStrafe);
        ObjectStore.objects.put("moveForward", this.moveForward);
        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        this.jump = event.isJump();
        this.sneak = event.isSneak();
    }

    @Inject(
            method = "updatePlayerMoveState",
            desc = "()V",
            target = @Target("RETURN")
    )
    public void updatePlayerMoveStateReturn() {
        final double sneakMultiplier = (double) ObjectStore.objects.get("sneakMultiplier");
        if (this.sneak) {
            this.moveStrafe = (float) ((double) ((Float) ObjectStore.objects.get("moveStrafe")) * sneakMultiplier);
            this.moveForward = (float) ((double) ((Float) ObjectStore.objects.get("moveForward")) * sneakMultiplier);
        }
    }
}
