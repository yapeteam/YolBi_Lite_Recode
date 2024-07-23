package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.utils.player.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;

public class MurdererFinder extends Module {
    public static MurdererFinder instance;
    public HashSet<String> names = new HashSet<>();

    public MurdererFinder() {
        super("MurdererFinder", ModuleCategory.PLAYER, Keyboard.KEY_NONE);
        instance = this;
    }

    @Listener
    public void onTick(EventTick event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        for (EntityPlayer target : mc.theWorld.playerEntities) {
            if (target.getHeldItem() != null && !target.equals(mc.thePlayer) && !target.isDead && findSword(target) != -1 && !names.contains(target.getName())) {
                names.add(target.getName());
                PlayerUtils.sendMessage("\247f" + target.getName() + "\247c是杀手,主播小心点!!!");
            }
        }
    }

    public int findSword(EntityPlayer target) {

        for (int i = 36; i < 45; i++) {
            Item item = target.inventoryContainer.getSlot(i).getStack().getItem();
            if (target.inventoryContainer.getSlot(i).getStack() != null && item instanceof ItemSword) {
                return i;
            }
        }
        return -1;
    }
}
