package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
public class ClickUI extends Module {

    private final BooleanValue pauseGame = new BooleanValue("PauseGame", true);
    private final BooleanValue blur = new BooleanValue("Blur background", () -> !mc.gameSettings.ofFastRender, true);
    private final BooleanValue rainbow = new BooleanValue("RainBow", false);
    private final NumberValue<Integer> blurRadius = new NumberValue<>("blurRadius", blur::getValue, 3, 0, 50, 1);
    //public  final BooleanValue notiff = notification;
    public ClickUI() {
        super("ClickGUI", ModuleCategory.VISUAL, Keyboard.KEY_RCONTROL);
        if (ReflectUtil.hasOptifine)
            blur.setCallback((oldV, newV) -> !mc.gameSettings.ofFastRender && newV);
        else blur.setVisibility(() -> true);
        //public final BooleanValue notif = notification.getValue();
        //notification.setValue(false);
        //addValues(blur,rainbow,blurRadius);
        addValues(blur, rainbow, blurRadius);
    }

    @Getter
    private ImplScreen screen = null;

    @Override
    protected void onEnable() {
        setEnabled(false);
        if (ReflectUtil.hasOptifine && mc.gameSettings.ofFastRender)
            blur.setValue(false);
        if (screen == null) screen = new ImplScreen();
        mc.displayGuiScreen(screen);
    }
}
