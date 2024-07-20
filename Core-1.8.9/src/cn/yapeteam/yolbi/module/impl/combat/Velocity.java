package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacket;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.notification.Notification;
import cn.yapeteam.yolbi.notification.NotificationType;
import cn.yapeteam.yolbi.utils.animation.Easing;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentText;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Velocity extends Module {

    //probability for having a perfect jump
    private final NumberValue<Double> probability = new NumberValue<>("Probability", 100.0, 0.0, 100.0, 1.0);

    //delay for jumping after the perfect jump
    private final NumberValue<Double> maxJumpDelay = new NumberValue<>("Max Jump Delay", 0.0, 0.0, 1000.0, 10.0);

    private final NumberValue<Double> minJumpDelay = new NumberValue<>("Min Jump Delay", 0.0, 0.0, 1000.0, 10.0);

    // how long you hold the space bar
    private final NumberValue<Double> maxJumpHold = new NumberValue<>("Max Jump Hold", 400.0, 0.0, 1000.0, 10.0);

    private final NumberValue<Double> minJumpHold = new NumberValue<>("Min Jump Hold", 400.0, 0.0, 1000.0, 10.0);


    public Velocity() {
        super("Velocity", ModuleCategory.COMBAT);
        NumberValue.setBound(minJumpDelay, maxJumpDelay);
        NumberValue.setBound(minJumpHold, maxJumpHold);
        addValues(probability, maxJumpDelay, minJumpDelay, maxJumpHold, minJumpHold);
    }

    public void jumpreset() {
        Natives.SetKeyBoard(VirtualKeyBoard.VK_SPACE, true);

        // Create a ScheduledExecutorService
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        // Generate a random delay in milliseconds
        int delay = (int) MathUtils.getRandom(minJumpHold.getValue(), maxJumpHold.getValue()); // replace 1000 with the maximum delay you want

        // Schedule a task to set the jump keybind to false after the delay
        executorService.schedule(() -> Natives.SetKeyBoard(VirtualKeyBoard.VK_SPACE, false), delay, TimeUnit.MILLISECONDS);

        // Shut down the executor service
        executorService.shutdown();
    }

    @Listener
    public void onPacket(EventPacket event) {
        if (mc.currentScreen != null) return;
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
//            YolBi.instance.getNotificationManager().post(
//                    new Notification(
//                            "Velocity",
//                            Easing.EASE_IN_OUT_QUAD,
//                            Easing.EASE_IN_OUT_QUAD,
//                            2500, NotificationType.WARNING
//                    )
//            );
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    String.format("[Yolbi] You are using a blatant module and it will not get bypassed")
            ));

            if ((new Random((long) (Math.random() * 114514)).nextInt(101) <= probability.getValue())) {
                jumpreset();
            } else {
                // this means to delay the jump
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                int delay = (int) MathUtils.getRandom(minJumpDelay.getValue(), maxJumpDelay.getValue());
                executorService.schedule(this::jumpreset, delay, TimeUnit.MILLISECONDS);
                executorService.shutdown();
            }
        }
    }
}

