//v1

//package cn.yapeteam.yolbi.module.impl.movement;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventMotion;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.item.ItemFood;
//import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
//
//import java.util.Random;
//
//@ModuleInfo(name = "Grim-Noslow", category = ModuleCategory.MOVEMENT)
//public class GrimNoslow extends Module {
//    private boolean throwingItem;
//    private final BooleanValue autoThrow = new BooleanValue("Throw-Mode", true);
//    private long lastActionTime;
//    private Random random;
//
//    public GrimNoslow() {
//        addValues(autoThrow);
//        this.random = new Random();
//    }
//
//    @Listener
//    private void onMotion(EventMotion event) {
//        if (autoThrow.getValue()) {
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastActionTime < 2000 + random.nextInt(1000)) {
//                return; // Add random delay between actions
//            }
//
//            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
//                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
//                if (!throwingItem) {
//                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
//                            mc.thePlayer.inventory.getCurrentItem()));
//
//                    throwingItem = true;
//                    lastActionTime = currentTime;
//                }
//            } else {
//                throwingItem = false;
//            }
//        }
//    }
//}


//v2

//package cn.yapeteam.yolbi.module.impl.movement;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventMotion;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.multiplayer.WorldClient;
//import net.minecraft.entity.player.InventoryPlayer;
//import net.minecraft.init.Items;
//import net.minecraft.item.ItemFood;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
//import net.minecraft.network.play.client.C09PacketHeldItemChange;
//
//import java.util.Random;
////public static final Item AIR_ITEM = null; // or some other appropriate value
//
//@ModuleInfo(name = "Grim-Noslow", category = ModuleCategory.MOVEMENT)
//public class GrimNoslow extends Module {
//    private boolean throwingItem;
//    private final BooleanValue autoThrow = new BooleanValue("Throw-Mode", true);
//    private long lastActionTime;
//    private Random random;
//
//    public GrimNoslow() {
//        addValues(autoThrow);
//        this.random = new Random();
//    }
//
//    @Listener
//    private void onMotion(EventMotion event) {
//        if (autoThrow.getValue()) {
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastActionTime < 2000 + random.nextInt(1000)) {
//                return; // Add random delay between actions
//            }
//
//            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
//                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
//                if (!throwingItem) {
//                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
//                            mc.thePlayer.inventory.getCurrentItem()));
//
//                    // Directly drop the item after placing it
//                    ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
//                    if (currentItem != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood) {
//                        InventoryPlayer inventory = mc.thePlayer.inventory;
//                        WorldClient world = mc.theWorld;
//                        EntityPlayerSP player = mc.thePlayer;
//
//                        // Select an empty slot in the hotbar to switch to, to avoid dropping the item in use
//                        int emptySlot = -1;
//                        for (int i = 0; i < 9; i++) {
//                            if (inventory.mainInventory[i] == null || inventory.mainInventory[i].getItem() == null) {
//                            //if (inventory.mainInventory[i] == null || inventory.mainInventory[i].getItem() == Items.AIR) {
//                                emptySlot = i;
//                                break;
//                            }
//                        }
//
//                        // Switch to the empty slot
//                        if (emptySlot != -1) {
//                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(emptySlot));
//                            inventory.mainInventory[emptySlot] = inventory.getCurrentItem().splitStack(1);
//                        } else {
//                            player.dropPlayerItemWithRandomChoice(inventory.getCurrentItem(), false);
//                            inventory.setInventorySlotContents(inventory.currentItem, null);
//                        }
//                    }
//
//                    throwingItem = true;
//                    lastActionTime = currentTime;
//                }
//            } else {
//                throwingItem = false;
//            }
//        }
//    }
//}

//v3
//package cn.yapeteam.yolbi.module.impl.movement;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventMotion;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.ItemFood;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
//
//@ModuleInfo(name = "Grim-Noslow", category = ModuleCategory.MOVEMENT)
//public class GrimNoslow extends Module {
//    private final BooleanValue autoThrow = new BooleanValue("Throw-Mode", true);
//    private Minecraft mc = Minecraft.getMinecraft();
//
//    public GrimNoslow() {
//        addValues(autoThrow);
//    }
//
//    @Listener
//    private void onMotion(EventMotion event) {
//        if (autoThrow.getValue()) {
//            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
//                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
//                // Send block placement packet to server (simulating drop)
//                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
//
//                // Clear the current equipped item (optional, depending on desired behavior)
//                mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem] = null;
//            }
//        }
//    }
//}
//
//
//
//v4
//
//package cn.yapeteam.yolbi.module.impl.movement;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.player.EventMotion;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.item.ItemFood;
//import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
//import net.minecraft.client.settings.KeyBinding;
//import org.lwjgl.input.Keyboard;
//
//@ModuleInfo(name = "Noslow", category = ModuleCategory.MOVEMENT)
//public class Noslow extends Module {
//    private boolean throwingItem;
//    private final BooleanValue autoThrow = new BooleanValue("C08", true);
//
//    {
//        addValues(autoThrow);
//    }
//
//    @Listener
//    private void onMotion(EventMotion event) {
//        if (autoThrow.getValue()) {
//            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
//                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
//                if (!throwingItem) {
//                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
//                            mc.thePlayer.inventory.getCurrentItem()));
//
//                    // 模拟按下Q键
//                    KeyBinding.onTick(mc.gameSettings.keyBindDrop.getKeyCode());
//                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindDrop.getKeyCode(), true);
//                    KeyBinding.onTick(mc.gameSettings.keyBindDrop.getKeyCode());
//
//                    throwingItem = true;
//                }
//            } else {
//                if (throwingItem) {
//                    // 模拟释放Q键
//                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindDrop.getKeyCode(), false);
//                }
//                throwingItem = false;
//            }
//        }
//    }
//}
//
//v5
package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.module.Module;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "Grim-Noslow", category = ModuleCategory.MOVEMENT)
public class GrimNoslow extends Module {
    private boolean throwingItem;
    private final BooleanValue autoThrow = new BooleanValue("C08", true);

    {
        addValues(autoThrow);
    }

    @Listener
    private void onMotion(EventMotion event) {
        if (autoThrow.getValue()) {
            if (mc.thePlayer != null && mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null
                    && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
                if (!throwingItem) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
                            mc.thePlayer.inventory.getCurrentItem()));

                    // 模拟丢弃一个物品
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

                    throwingItem = true;
                }
            } else {
                throwingItem = false;
            }
        }
    }
}



