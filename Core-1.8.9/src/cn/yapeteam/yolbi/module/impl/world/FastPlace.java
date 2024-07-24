package cn.yapeteam.yolbi.module.impl.world;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import net.minecraft.item.ItemBlock;
import org.lwjgl.input.Keyboard;

public class FastPlace extends Module {
    private final NumberValue<Integer> ticks = new NumberValue<>("Ticks", 1, 0, 4, 1);
    private boolean noblock;
    public FastPlace() {
        super("FastPlace", ModuleCategory.WORLD, Keyboard.KEY_NONE);
    }
    @Listener
    public void onTick(EventTick e) throws IllegalAccessException {
        if (mc.thePlayer.getHeldItem() !=null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            ReflectUtil.Minecraft$rightClickDelayTimer.setInt(mc,ticks.getValue());
            noblock = false;
        } else if (ReflectUtil.Minecraft$rightClickDelayTimer.getInt(mc) < 4 && !noblock){
            ReflectUtil.Minecraft$rightClickDelayTimer.setInt(mc,4);
            noblock = true;
        }
    }
}
