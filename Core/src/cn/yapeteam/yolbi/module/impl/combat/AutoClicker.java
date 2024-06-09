package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.loader.Natives;
import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventMouse;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

import java.util.Random;

@ModuleInfo(name = "AutoClicker", category = ModuleCategory.COMBAT, key = Keyboard.KEY_F)
public class AutoClicker extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("cps", 17, 1, 100, 1);
    private final NumberValue<Double> range = new NumberValue<>("cps range", 1.5, 0.1d, 2.5d, 0.1);
    private final BooleanValue leftClick = new BooleanValue("leftClick", true),
            rightClick = new BooleanValue("rightClick", false);

    public AutoClicker() {
        addValues(cps, range, leftClick, rightClick);
    }

    private double delay = 0, time = 0;

    @Override
    public void onEnable() {
        delay = generate(cps.getValue(), range.getValue());
        time = System.currentTimeMillis();
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

    public void sendClick(int button) {
        Natives.SetMouse(button, true);
    }

    private boolean left = false, right = false, skip = false;
    private long lastClickTime = 0;

    @Listener
    private void onMouse(EventMouse e) {
        if (mc.currentScreen == null && e.getButton() != -1) {
            if (e.isPressed()) {
                if (System.currentTimeMillis() - lastClickTime > 200) {
                    switch (e.getButton()) {
                        case 0:
                            left = true;
                            break;
                        case 1:
                            right = true;
                            break;
                    }
                }
            } else {
                if (!skip) {
                    switch (e.getButton()) {
                        case 0:
                            left = false;
                            Natives.SetMouse(0, false);
                            break;
                        case 1:
                            right = false;
                            Natives.SetMouse(1, false);
                            break;
                    }
                    skip = true;
                } else skip = false;
            }
            lastClickTime = System.currentTimeMillis();
        }
    }

    @Listener
    private void onRender2D(EventRender2D e) {
        delay = generate(cps.getValue(), range.getValue());
        if (mc.currentScreen != null) return;
        if (System.currentTimeMillis() - time >= (1000 / delay)) {
            if (leftClick.getValue() && left) {
                time = System.currentTimeMillis();
                sendClick(0);
            }
            if (rightClick.getValue() && right && mc.objectMouseOver != null) {
                time = System.currentTimeMillis();
                if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                    sendClick(1);
            }
        }
    }


    @Override
    public String getSuffix() {
        return cps.getValue() + ":" + String.format("%.2f", delay);
    }
}
