package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.VirtualKeyBoard;
import lombok.Getter;
import net.minecraft.item.ItemFood;
import net.minecraft.util.MovingObjectPosition;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("cps", 17, 1, 100, 1);
    private final NumberValue<Double> range = new NumberValue<>("cps range", 1.5, 0.1d, 2.5d, 0.1);
    private final BooleanValue leftClick = new BooleanValue("leftClick", true),
            rightClick = new BooleanValue("rightClick", false);

    private final BooleanValue noeat = new BooleanValue("No Click When Eating", true);

    private final BooleanValue nomine = new BooleanValue("No Click When Mining", true);
    private final ModeValue<String> clickprio = new ModeValue<>("Click Priority", "Left", "Left", "Right");
    private double delay = 1;

    @Override
    public void onEnable() {
        delay = generate(cps.getValue(), range.getValue());
    }

    @Getter
    private final Thread clickThread = new Thread(() -> {
        while (true) {
            try {
                if (isEnabled() && mc.currentScreen == null) {
                    delay = generate(cps.getValue(), range.getValue());
                    if (clickprio.is("Left")) {
                        if (leftClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON) && !(nomine.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)) {
                            sendClick(0);
                        }
                        if (rightClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_RBUTTON) && !((mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && noeat.getValue())) {
                            sendClick(1);
                        }
                    } else {
                        if (rightClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_RBUTTON) && !((mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) && noeat.getValue())) {
                            sendClick(1);
                        }
                        if (leftClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON) && !(nomine.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)) {
                            sendClick(0);
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.exception(ex);
            }
        }
    });

    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        addValues(cps, range, leftClick, rightClick, noeat, nomine, clickprio);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Natives.SendLeft(false);
            Natives.SendRight(false);
        }));
        clickThread.start();
    }

    private final Random random = new Random();

    public double generateNoise(double min, double max) {
        double u1, u2, v1, v2, s;
        do {
            u1 = random.nextDouble() * 2 - 1;
            u2 = random.nextDouble() * 2 - 1;
            s = u1 * u1 + u2 * u2;
        } while (s >= 1 || s == 0);

        double multiplier = Math.sqrt(-2 * Math.log(s) / s);
        v1 = u1 * multiplier;
        v2 = u2 * multiplier;
        // 将生成的噪声值缩放到指定范围内
        return (v1 + v2) / 2 * (max - min) / 4 + (max + min) / 2;
    }

    private double generate(double cps, double range) {
        double noise = cps;
        for (int j = 0; j < 10; j++) {
            double newNoise = generateNoise(0, cps * 2);
            if (Math.abs(noise - newNoise) < range)
                noise = (noise + newNoise) / 2;
            else j--;
        }
        return noise;
    }

    public void sendClick(int button) throws InterruptedException {
        // Simulate left click
        if (button == 0) {
            Natives.SendLeft(true);
            Thread.sleep((long) (1000 / delay * 0.8));
            Natives.SendLeft(false);
            Thread.sleep((long) (1000 / delay * 0.2));
        }
        // Simulate right click
        else if (button == 1) {
            Natives.SendRight(true);
            Thread.sleep((long) (1000 / delay * 0.8));
            Natives.SendRight(false);
            Thread.sleep((long) (1000 / delay * 0.2));
        }
    }

    @Override
    public String getSuffix() {
        return (cps.getValue() - range.getValue()) + " ~ " + (cps.getValue() + range.getValue());
    }
}
