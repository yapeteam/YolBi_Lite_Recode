package cn.yapeteam.yolbi.utils.player;

import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static int getItemDurability(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return stack.getMaxDamage() - stack.getItemDamage();
    }
}
