package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketSend;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.misc.TimerUtil;
import cn.yapeteam.yolbi.utils.network.PacketUtil;
import cn.yapeteam.yolbi.utils.player.BlockUtil;
import cn.yapeteam.yolbi.utils.player.InventoryUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketUseEntity;

import java.util.Arrays;

public class InvCleaner extends Module {
    private final NumberValue<Double> delay = new NumberValue<>("Delay", 120.0, 0.0, 300.0, 10.0);
    private final BooleanValue onlyWhileNotMoving = new BooleanValue("NoMove", true);
    private final BooleanValue inventoryOnly = new BooleanValue("InventoryOnly", true);
    private final NumberValue<Double> slotWeapon = new NumberValue<>("Weapon Slot", 1.0, 1.0, 9.0, 1.0);
    private final NumberValue<Double> slotBlock = new NumberValue<>("Block Slot", 2.0, 1.0, 9.0, 1.0);
    private final NumberValue<Double> slotPick = new NumberValue<>("Pickaxe Slot", 3.0, 1.0, 9.0, 1.0);
    private final NumberValue<Double> slotAxe = new NumberValue<>("Axe Slot", 4.0, 1.0, 9.0, 1.0);
    private final NumberValue<Double> slotShovel = new NumberValue<>("Shovel Slot", 5.0, 1.0, 9.0, 1.0);
    private final NumberValue<Double> slotBow = new NumberValue<>("Bow Slot", 6.0, 1.0, 9.0, 1.0);


    private final String[] shitItems = {"tnt", "stick", "egg", "string", "cake", "mushroom", "flint", "compass", "dyePowder", "feather", "bucket", "chest", "snow", "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "snowball", "piston"};
    private final String[] serverItems = {"selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};

    private final TimerUtil timer = new TimerUtil();
    private boolean isInvOpen;

    public InvCleaner() {
        super("InvCleaner", ModuleCategory.PLAYER);
        this.addValues(delay, onlyWhileNotMoving, inventoryOnly, slotWeapon, slotBlock, slotPick, slotAxe, slotShovel, slotBow);
    }

    @Listener
    private void onUpdate(EventUpdate e) {
        if (!e.isPre()) return;
        if (stop()) return;
        if (!mc.player.isHandActive() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiIngameMenu)) {
            long delay = this.delay.getValue().longValue();
            if (timer.hasTimePassed(delay)) {
                Slot slot = mc.player.inventoryContainer.getSlot(getDesiredSlot(ItemType.WEAPON));
                if (!slot.getHasStack() || !isBestWeapon(slot.getStack())) {
                    getBestWeapon();
                }
                getBestPickaxe();
                getBestAxe();
                getBestShovel();
                for (int i = 9; i < 45; i++) {
                    if (stop()) return;
                    slot = mc.player.inventoryContainer.getSlot(i);
                    ItemStack is = slot.getStack();
                    if (is != null && shouldDrop(is, i)) {
                        InventoryUtils.drop(i);
                        break;
                    }
                }
                swapBlocks();
                getBestBow();
                moveArrows();
                timer.reset();
            }
        }
    }

    @Listener
    private void onPacketSend(EventPacketSend e) {
        if (isInvOpen) {
            Packet<?> packet = e.getPacket();
//            if ((packet instanceof CPacketClientStatus && ((CPacketClientStatus) packet).getStatus() == CPacketClientStatus.State.OPEN_INVENTORY_ACHIEVEMENT)
//                    || packet instanceof CPacketCloseWindow) {
//                e.setCancelled(true);
            /*} else*/
            if (packet instanceof CPacketUseEntity) {
                fakeClose();
            }
        }
    }

    ;

    public static float getDamageScore(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0;

        float damage = 0;
        Item item = stack.getItem();

        if (item instanceof ItemSword) {
            damage += ((ItemSword) item).getDamageVsEntity();
        } else if (item instanceof ItemTool) {
            damage += item.getMaxDamage();
        }

        damage += EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) * 1.25F +
                EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) * 0.1F;

        return damage;
    }

    public static float getProtScore(ItemStack stack) {
        float prot = 0;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount
                    + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack)) * 0.0075F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantments.THORNS, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) / 25.F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack) / 100F;
        }
        return prot;
    }

    private int getDesiredSlot(ItemType tool) {
        int slot = 36;
        switch (tool) {
            case WEAPON:
                slot = (slotWeapon.getValue().intValue());
                break;
            case PICKAXE:
                slot = (slotPick.getValue().intValue());
                break;
            case AXE:
                slot = (slotAxe.getValue().intValue());
                break;
            case SHOVEL:
                slot = (slotShovel.getValue().intValue());
                break;
            case BLOCK:
                slot = (slotBlock.getValue().intValue());
                break;
            case BOW:
                slot = (slotBow.getValue().intValue());
                break;
        }
        return slot + 35;
    }

    private boolean isBestWeapon(ItemStack is) {
        float damage = getDamageScore(is);
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is2 = slot.getStack();
                if (getDamageScore(is2) > damage && is2.getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }
        return is.getItem() instanceof ItemSword;
    }

    private void getBestWeapon() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamageScore(is) > 0 && is.getItem() instanceof ItemSword) {
                    swap(i, getDesiredSlot(ItemType.WEAPON) - 36);
                    break;
                }
            }
        }
    }

    private boolean shouldDrop(ItemStack stack, int slot) {
        String stackName = stack.getDisplayName().toLowerCase();
        Item item = stack.getItem();
        String ulName = item.getUnlocalizedName();
        if (Arrays.stream(serverItems).anyMatch(stackName::contains)) return false;

        if (item instanceof ItemBlock) {
            return !BlockUtil.isValidBlock(((ItemBlock) item).getBlock(), true);
        }

        int weaponSlot = getDesiredSlot(ItemType.WEAPON);
        int pickaxeSlot = getDesiredSlot(ItemType.PICKAXE);
        int axeSlot = getDesiredSlot(ItemType.AXE);
        int shovelSlot = getDesiredSlot(ItemType.SHOVEL);

        if ((slot != weaponSlot || !isBestWeapon(mc.player.inventoryContainer.getSlot(weaponSlot).getStack()))
                && (slot != pickaxeSlot || !isBestPickaxe(mc.player.inventoryContainer.getSlot(pickaxeSlot).getStack()))
                && (slot != axeSlot || !isBestAxe(mc.player.inventoryContainer.getSlot(axeSlot).getStack()))
                && (slot != shovelSlot || !isBestShovel(mc.player.inventoryContainer.getSlot(shovelSlot).getStack()))) {
            if (item instanceof ItemArmor) {
                for (int type = 1; type < 5; type++) {
                    if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                        ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                        if (isBestArmor(is, type)) {
                            continue;
                        }
                    }
                    if (isBestArmor(stack, type)) {
                        return false;
                    }
                }
            }

            if ((item == Items.WHEAT) || item == Items.SPAWN_EGG || (item instanceof ItemPotion && isShitPotion(stack))) {
                return true;
            } else if (!(item instanceof ItemSword) && !(item instanceof ItemTool) && !(item instanceof ItemHoe) && !(item instanceof ItemArmor)) {
                return item instanceof ItemGlassBottle || Arrays.stream(shitItems).anyMatch(ulName::contains);
            }
            return true;
        }

        return false;
    }

    private void getBestPickaxe() {
        for (int i = 9; i < 45; ++i) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestPickaxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.PICKAXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.player.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestPickaxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void getBestAxe() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestAxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.AXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.player.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestAxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestShovel(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.SHOVEL);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.player.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestShovel(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestBow() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                String stackName = is.getDisplayName().toLowerCase();
                if (Arrays.stream(serverItems).anyMatch(stackName::contains) || !(is.getItem() instanceof ItemBow))
                    continue;
                if (isBestBow(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.BOW);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.player.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestBow(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void moveArrows() {
        for (int i = 36; i < 45; i++) {
            ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
            if (is != null && is.getItem() == Items.ARROW) {
                for (int j = 0; j < 36; j++) {
                    if (mc.player.inventoryContainer.getSlot(j).getStack() == null) {
                        fakeOpen();
                        InventoryUtils.click(i, 0, true);
                        fakeClose();
                        timer.reset();
                        break;
                    }
                }
            }
        }
    }

    private int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.func_190916_E() > stack && Arrays.stream(serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.func_190916_E();
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private void swapBlocks() {
        int mostBlocksSlot = getMostBlocks();
        int desiredSlot = getDesiredSlot(ItemType.BLOCK);
        if (mostBlocksSlot != -1 && mostBlocksSlot != desiredSlot) {
            // only switch if the hotbar slot doesn't already have blocks of the same quantity to prevent an inf loop
            Slot dss = mc.player.inventoryContainer.getSlot(desiredSlot);
            ItemStack dsis = dss.getStack();
            if (!(dsis != null && dsis.getItem() instanceof ItemBlock && dsis.func_190916_E() >= mc.player.inventoryContainer.getSlot(mostBlocksSlot).getStack().func_190916_E() && Arrays.stream(serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains))) {
                swap(mostBlocksSlot, desiredSlot - 36);
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.player.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemPickaxe && getToolScore(is) > value) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestShovel(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSpade)) {
            return false;
        } else {
            float score = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.player.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemSpade && getToolScore(is) > score) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestAxe(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemAxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.player.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getToolScore(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestBow(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBow)) {
            return false;
        } else {
            float value = getPowerLevel(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.player.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getPowerLevel(is) > value && is.getItem() instanceof ItemBow && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private float getPowerLevel(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemBow) {
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) * 0.5F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) * 0.1F;
        }
        return score;
    }

    private float getToolScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
           /* String name = item.getUnlocalizedName().toLowerCase();
            if (item instanceof ItemPickaxe) {
                score = tool.getStrVsBlock(stack, Blocks.stone) - (name.contains("gold") ? 5 : 0);
            } else if (item instanceof ItemSpade) {
                score = tool.getStrVsBlock(stack, Blocks.dirt) - (name.contains("gold") ? 5 : 0);
            } else {
                if (!(item instanceof ItemAxe)) return 1;
                score = tool.getStrVsBlock(stack, Blocks.log) - (name.contains("gold") ? 5 : 0);
            }*/
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack) * 0.0075F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) / 100F;
        }
        return score;
    }

    private boolean isShitPotion(ItemStack stack) {

        return false;
    }

    public boolean isBestArmor(ItemStack stack, int type) {
        String typeStr = "";
        switch (type) {
            case 1:
                typeStr = "helmet";
                break;
            case 2:
                typeStr = "chestplate";
                break;
            case 3:
                typeStr = "leggings";
                break;
            case 4:
                typeStr = "boots";
                break;
        }
        if (stack.getUnlocalizedName().contains(typeStr)) {
            float prot = getProtScore(stack);
            for (int i = 5; i < 45; i++) {
                Slot slot = mc.player.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getUnlocalizedName().contains(typeStr) && getProtScore(is) > prot) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    boolean inventoryPackets = true;

    private void fakeOpen() {
        if (!isInvOpen) {
            timer.reset();
//            if (!inventoryOnly.getValue() && inventoryPackets)
//                PacketUtil.sendPacketNoEvent(new CPacketClientStatus(CPacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            isInvOpen = true;
        }
    }

    private void fakeClose() {
        if (isInvOpen) {
            if (!inventoryOnly.getValue() && inventoryPackets)
                PacketUtil.sendPacketNoEvent(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
            isInvOpen = false;
        }
    }

    private void swap(int slot, int hSlot) {
        fakeOpen();
        InventoryUtils.swap(slot, hSlot);
        fakeClose();
        timer.reset();
    }

    public static boolean isMoving() {
        return mc.player != null && (mc.player.movementInput.field_192832_b != 0F || mc.player.movementInput.moveStrafe != 0F);
    }

    private boolean stop() {

        return (inventoryOnly.getValue() && !(mc.currentScreen instanceof GuiInventory)) || (onlyWhileNotMoving.getValue() && isMoving());
    }

    private enum ItemType {
        WEAPON, PICKAXE, AXE, SHOVEL, BLOCK, BOW
    }
}
