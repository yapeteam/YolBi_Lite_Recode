package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
public class Velocity extends Module {
    private final NumberValue<Double> horizontal = new NumberValue<>("Horizontal velocity", 80.0, 0.0, 100.0, 1.0);

    private final NumberValue<Double> vertical = new NumberValue<>("Verical velocity", 80.0, 0.0, 100.0, 1.0);

    private final BooleanValue jump = new BooleanValue("Jump", true);

    //probability for having a perfect jump
    private final NumberValue<Double> probability = new NumberValue<>("Probability", 100.0, 0.0, 100.0, 1.0);

    //delay for jumping after the perfect jump
    private final NumberValue<Double> maxJumpDelay = new NumberValue<>("Max Jump Delay", 0.0, 0.0, 1000.0, 10.0);

    private final NumberValue<Double> minJumpDelay = new NumberValue<>("Min Jump Delay", 0.0, 0.0, 1000.0, 10.0);

    // how long you hold the space bar
    private final NumberValue<Double> maxJumpHold = new NumberValue<>("Max Jump Hold", 0.0, 0.0, 1000.0, 10.0);

    private final NumberValue<Double> minJumpHold = new NumberValue<>("Min Jump Hold", 0.0, 0.0, 1000.0, 10.0);



    public Velocity() {
        addValues(horizontal, vertical, jump, probability, maxJumpDelay, minJumpDelay, maxJumpHold, minJumpHold);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        System.out.println(ReflectUtil.Entity$getMotionX(mc.thePlayer));
    }

    public void jumpreset(){
        ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, true);

        // Create a ScheduledExecutorService
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // Generate a random delay in milliseconds
        int delay = (int) MathUtils.getRandom(minJumpHold.getValue().doubleValue(), maxJumpDelay.getValue().doubleValue()); // replace 1000 with the maximum delay you want

        // Schedule a task to set the jump keybind to false after the delay
        executorService.schedule(() -> ReflectUtil.SetPressed(mc.gameSettings.keyBindJump, false), delay, TimeUnit.MILLISECONDS);

        // Shut down the executor service
        executorService.shutdown();
    }

    @Listener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
            YolBi.instance.getNotificationManager().post(
                    new Notification(
                            "Velocity",
                            Easing.EASE_IN_OUT_QUAD,
                            Easing.EASE_IN_OUT_QUAD,
                            2500, NotificationType.WARNING
                    )
            );

            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            int x = packet.getMotionX();
            int y = packet.getMotionY();
            int z = packet.getMotionZ();

            // Calculate the new velocities based on the current velocities and the horizontal and vertical values
            double newX = x / 8000.0D * horizontal.getValue() / 100;
            double newY = y / 8000.0D * vertical.getValue() / 100;
            double newZ = z / 8000.0D * horizontal.getValue() / 100;



            // Set the new velocities
            ReflectUtil.Entity$setMotionX(mc.thePlayer, newX);
            ReflectUtil.Entity$setMotionY(mc.thePlayer, newY);
            ReflectUtil.Entity$setMotionZ(mc.thePlayer, newZ);

            if(jump.getValue()){
                if(Math.random() * 100 < probability.getValue()){
                    jumpreset();
                }else{
                    // this means to delay the jump
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    int delay = (int) MathUtils.getRandom(minJumpDelay.getValue().doubleValue(), maxJumpDelay.getValue().doubleValue());
                    executorService.schedule(() -> jumpreset(), delay, TimeUnit.MILLISECONDS);
                    executorService.shutdown();
                }
            }
        }
    }
}

