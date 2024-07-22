package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.module.impl.player.MurdererFinder;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.src.Config;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class NameTags extends Module {
    private final BooleanValue armor = new BooleanValue("Armor", true);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", true);
    FontRenderer fr = mc.fontRendererObj;

    public NameTags() {
        super("NameTags", ModuleCategory.VISUAL, Keyboard.KEY_NONE);
        addValues(armor,invisibles);
    }

    @Listener
    public void onRender(EventRender3D render) {
        ArrayList<EntityLivingBase> validEnt = new ArrayList<>();
        for (EntityLivingBase player2 : mc.theWorld.playerEntities) {
            if (player2.isEntityAlive()) {
                if (player2.isInvisible() && !this.invisibles.getValue()) {
                    if (!validEnt.contains(player2)) continue;
                    validEnt.remove(player2);
                    continue;
                }
                if (player2 == mc.thePlayer) {
                    if (!validEnt.contains(player2)) continue;
                    validEnt.remove(player2);
                    continue;
                }
                if (validEnt.size() > 100) break;
                if (validEnt.contains(player2)) continue;
                validEnt.add(player2);
                continue;
            }
            if (!validEnt.contains(player2)) continue;
            validEnt.remove(player2);
        }
        validEnt.forEach(player -> {
            float x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) render.getPartialTicks() - ReflectUtil.GetRenderManager$renderPosX(mc.getRenderManager()));
            float y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) render.getPartialTicks() - ReflectUtil.GetRenderManager$renderPosY(mc.getRenderManager()));
            float z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) render.getPartialTicks() - ReflectUtil.GetRenderManager$renderPosZ(mc.getRenderManager()));
            this.renderNametag((EntityPlayer) player, x, y, z);
        });
    }

    private String getHealth(EntityPlayer player) {
        DecimalFormat numberFormat = new DecimalFormat("#");
        return numberFormat.format(player.getHealth() / 2.0f + player.getAbsorptionAmount() / 2.0f);
    }

    private void drawNames(EntityPlayer entity) {
        String name = this.getPlayerName(entity);
        String healthText = this.getHealth(entity);
        int nameWidth = fr.getStringWidth(name) / 2;
        int healthWidth = fr.getStringWidth("Health:" + healthText) / 2;
        int width = Math.max(nameWidth, healthWidth);
        float health = entity.getHealth();
        int color = health > 20.0 ? -65292 : (health >= 10.0 ? -16711936 : (health >= 3.0 ? -23296 : -65536));
        int healthBar = (int) ((width + 3) * health / entity.getMaxHealth());
        RenderUtil.drawBorderedRect(-width - 3, -13, width + 3, 10, 1.0f, new Color(0, 0, 0, 100).getRGB(), new Color(0, 0, 0, 100).getRGB());
        RenderUtil.drawBorderedRect(-healthBar, 9, healthBar, 10, 1.0f, new Color(255, 255, 255, 100).getRGB(), -65536);
        GlStateManager.disableDepth();
        fr.drawString(name, 0, -11, new Color(255, 255, 255).getRGB());
        fr.drawString("\247fHealth:\247r" + healthText, 0, 0, color);
//        Managers.fontManager.sans18.drawCenteredString(name, 0, -11, new Color(255, 255, 255).getRGB());
//        Managers.fontManager.sans18.drawCenteredString("\247fHealth:\247r" + healthText, 0, 0, color);
        GlStateManager.enableDepth();
    }

    private void startDrawing(float x, float y, float z, EntityPlayer player) {
        float invert = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        double size = Config.zoomMode ? (double) (this.getSize(player) / 10.0f) * 4.0 * 0.5 : (double) (this.getSize(player) / 10.0f) * 4.0 * 1.5;
        GL11.glPushMatrix();
        RenderUtil.startDrawing();
        GL11.glTranslatef(x, y, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef((-mc.getRenderManager().playerViewY), 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, invert, 0.0f, 0.0f);
        GL11.glScaled((-0.01666666753590107 * size), (-0.01666666753590107 * size), (0.01666666753590107 * size));
    }

    private void stopDrawing() {
        RenderUtil.stopDrawing();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private void renderNametag(EntityPlayer player, float x, float y, float z) {
        y = (float) ((double) y + (1.55 + (player.isSneaking() ? 0.5 : 0.7)));
        this.startDrawing(x, y, z, player);
        this.drawNames(player);
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        if (this.armor.getValue()) {
            this.renderArmor(player);
        }
        this.stopDrawing();
    }

    private void renderArmor(EntityPlayer player) {
        ItemStack armourStack;
        ItemStack[] renderStack = player.inventory.armorInventory;
        int xOffset = 0;
        for (ItemStack aRenderStack : renderStack) {
            armourStack = aRenderStack;
            if (armourStack == null) continue;
            xOffset -= 8;
        }
        if (player.getHeldItem() != null) {
            xOffset -= 8;
            ItemStack stock = player.getHeldItem().copy();
            if (stock.hasEffect() && (stock.getItem() instanceof ItemTool || stock.getItem() instanceof ItemArmor)) {
                stock.stackSize = 1;
            }
            this.renderItemStack(stock, xOffset);
            xOffset += 16;
        }
        renderStack = player.inventory.armorInventory;
        for (int index = 3; index >= 0; --index) {
            armourStack = renderStack[index];
            if (armourStack == null) continue;
            this.renderItemStack(armourStack, xOffset);
            xOffset += 16;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private String getPlayerName(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        String prefix = "";
        if (YolBi.instance.getModuleManager().getModule(MurdererFinder.class).findSword(player) != -1){
            prefix += "\247c[Killer]\247f";
        }

        return prefix + name;
    }

    private float getSize(EntityPlayer player) {
        return Math.max(mc.thePlayer.getDistanceToEntity(player) / 4.0f, 2.0f);
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -30);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, -30);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        double s = 0.5;
        GlStateManager.scale(s, s, s);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }
}
