package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class AutoArmor extends Module {
    private final TimerUtil timer = new TimerUtil();
    private final ModeValue<String> mode = new ModeValue<>("Mode", "Basic", "Basic", "OpenInv", "FakeInv");
    private final NumberValue<Integer> delay = new NumberValue<>("delay", 1, 0, 10, 1);

    public AutoArmor() {
        super("AutoArmor", ModuleCategory.PLAYER);
        addValues(mode, delay);
    }


    @Override
    public String getSuffix() {
        return mode.getValue();
    }

    @Listener
    public void onTick(EventTick event) {
        String mode = this.mode.getValue();
        //if (Client.getModuleManager().isEnabled(InventoryManager.class))
        //    return;
        long delay = this.delay.getValue().longValue() * 50;
        if (mode.equalsIgnoreCase("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.hasTimePassed(delay)) {
                getBestArmor();
                timer.reset();
            }
        }
    }

    public void getBestArmor() {
        for (int type = 1; type < 5; type++) {
            if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                } else {
                    if (mode.is("FakeInv")) {
                        C16PacketClientStatus p = new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                        mc.thePlayer.sendQueue.addToSendQueue(p);
                    }
                    drop(4 + type);
                }
            }
            for (int i = 9; i < 45; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtection(is) > 0) {
                        shiftClick(i);
                        timer.reset();
                        if (delay.getValue().longValue() > 0)
                            return;
                    }
                }
            }
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType))
                    return false;
            }
        }
        return true;
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public static float getProtection(ItemStack stack) {
        float protection = 0;
        if ((stack.getItem() instanceof ItemArmor)) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            protection += armor.damageReduceAmount + (100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075f;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100f;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100f;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100f;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50f;
            protection += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) / 100f;
        }
        return protection;
    }
}
