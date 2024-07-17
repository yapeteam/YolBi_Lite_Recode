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
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

import java.util.Random;

public class AutoClicker extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("cps", 17, 1, 100, 1);
    private final NumberValue<Double> range = new NumberValue<>("cps range", 1.5, 0.1d, 2.5d, 0.1);
    private final NumberValue<Integer> pressPercentage = new NumberValue<>("Press Percentage", 20, 0, 100, 1);
    private final BooleanValue leftClick = new BooleanValue("leftClick", true),
            rightClick = new BooleanValue("rightClick", false);

    private final BooleanValue noeat = new BooleanValue("No Click When Eating", true);

    private final BooleanValue nomine = new BooleanValue("No Click When Mining", true);
    private final ModeValue<String> clickprio = new ModeValue<>("Click Priority", "Left", "Left", "Right");
    private double delay = 1;

    @Override
    public void onEnable() {
        delay = generate(cps.getValue(), range.getValue());
        clickThread = new Thread(() -> {
            while (isEnabled()) {
                try {
                    delay = generate(cps.getValue(), range.getValue());
                    sendClick();
                } catch (Exception ex) {
                    Logger.exception(ex);
                }
            }
        });
        clickThread.start();
    }

    @Getter
    private Thread clickThread = null;

    public AutoClicker() {
        super("AutoClicker", ModuleCategory.COMBAT);
        addValues(cps, range, pressPercentage, leftClick, rightClick, noeat, nomine, clickprio);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Natives.SendLeft(false);
            Natives.SendRight(false);
        }));
    }

    private static final Random random = new Random();

    public static double generateNoise(double min, double max) {
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

    public static double generate(double cps, double range) {
        double noise = cps;
        for (int j = 0; j < 10; j++) {
            double newNoise = generateNoise(0, cps * 2);
            if (Math.abs(noise - newNoise) < range)
                noise = (noise + newNoise) / 2;
            else j--;
        }
        return noise;
    }

    private final Runnable leftClickRunnable = () -> {
        try {
            float pressPercentageValue = pressPercentage.getValue() / 100f;
            Natives.SendLeft(true);
            Thread.sleep((long) (1000 / delay * pressPercentageValue));
            Natives.SendLeft(false);
            Thread.sleep((long) (1000 / delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };


    private final Runnable rightClickRunnable = () -> {
        try {
            float pressPercentageValue = pressPercentage.getValue() / 100f;
            Natives.SendRight(true);
            Thread.sleep((long) (1000 / delay * pressPercentageValue));
            Natives.SendRight(false);
            Thread.sleep((long) (1000 / delay * (1 - pressPercentageValue)));
        } catch (InterruptedException ignored) {
        }
    };

    public void sendClick() {
        if (!isEnabled() || mc.currentScreen != null) return;

        boolean left = leftClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_LBUTTON);
        if (nomine.getValue() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
            left = false;
        boolean right = rightClick.getValue() && Natives.IsKeyDown(VirtualKeyBoard.VK_RBUTTON);
        //noinspection ConstantValue
        if ((mc.player.getHeldItem(EnumHand.MAIN_HAND) != null && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFood) && noeat.getValue())
            right = false;
        if (clickprio.getValue().equals("Left") && left) {
            leftClickRunnable.run();
        } else if (right) {
            rightClickRunnable.run();
            return;
        }
        if (clickprio.getValue().equals("Right") && right) {
            rightClickRunnable.run();
        } else if (left) {
            leftClickRunnable.run();
        }
    }

    @Override
    public String getSuffix() {
        return (cps.getValue() - range.getValue()) + " ~ " + (cps.getValue() + range.getValue());
    }
}
