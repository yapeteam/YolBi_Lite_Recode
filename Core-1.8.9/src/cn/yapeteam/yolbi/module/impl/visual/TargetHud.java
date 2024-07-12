//package cn.yapeteam.yolbi.module.impl.visual;
//
//import cn.yapeteam.loader.api.module.ModuleCategory;
//import cn.yapeteam.loader.api.module.ModuleInfo;
//import cn.yapeteam.yolbi.event.Listener;
//import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
//import cn.yapeteam.yolbi.event.impl.render.EntityDamageEvent;
//import cn.yapeteam.yolbi.module.Module;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.network.NetworkPlayerInfo;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//import org.lwjgl.opengl.GL11;
//
//import java.awt.*;
//
//@ModuleInfo(name = "TargetHUD", category = ModuleCategory.VISUAL)
//public class TargetHud extends Module {
//    private final Minecraft mc = Minecraft.getMinecraft();
//    private boolean showOnPlayerHit = true; // 击中玩家时显示
//    private boolean showOnMobHit = true; // 击中生物时显示
//    private EntityLivingBase currentTarget = null;
//
//    @Listener
//    public void onRender2D(EventRender2D event) {
//        if (currentTarget == null) {
//            return;
//        }
//
//        int x = 200; // HUD 的 X 位置
//        int y = 200; // HUD 的 Y 位置
//        int width = 120; // HUD 的宽度
//        int height = 40; // HUD 的高度
//
//        FontRenderer fr = mc.fontRendererObj;
//
//        // 绘制背景
//        drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 120).getRGB());
//
//        // 绘制目标玩家头像
//        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(currentTarget.getUniqueID());
//        if (info != null) {
//            mc.getTextureManager().bindTexture(info.getLocationSkin());
//            drawPlayerHead(x + 2, y + 2, 36, 36);
//        }
//
//        // 绘制目标玩家名字
//        String name = currentTarget.getName();
//        fr.drawStringWithShadow(name, x + 40, y + 2, -1);
//
//        // 绘制目标玩家生命值
//        float health = currentTarget.getHealth();
//        float maxHealth = currentTarget.getMaxHealth();
//        int healthColor = Color.HSBtoRGB(Math.max(0.0F, Math.min(health / maxHealth, 1.0F)) / 3.0F, 1.0F, 1.0F);
//        String healthStr = String.format("%.1f", health / 2.0F);
//        fr.drawStringWithShadow("❤ " + healthStr, x + 40, y + 14, healthColor);
//
//        // 绘制与目标玩家的距离
//        double distance = mc.thePlayer.getDistanceToEntity(currentTarget);
//        String distanceStr = String.format("%.1f", distance);
//        fr.drawStringWithShadow("距离: " + distanceStr, x + 40, y + 26, -1);
//
//        // 绘制目标玩家护甲值
//        int armor = currentTarget.getTotalArmorValue();
//        fr.drawStringWithShadow("护甲: " + armor, x + 80, y + 26, -1);
//    }
//
//    @Listener
//    public void onEntityDamage(EntityDamageEvent event) {
//        if (event.getAttacker() == mc.thePlayer) {
//            if (event.getTarget() instanceof EntityPlayer && showOnPlayerHit) {
//                currentTarget = (EntityLivingBase) event.getTarget();
//            } else if (event.getTarget() instanceof EntityLivingBase && showOnMobHit) {
//                currentTarget = (EntityLivingBase) event.getTarget();
//            }
//        }
//    }
//
//    private void drawPlayerHead(int x, int y, int width, int height) {
//        GL11.glPushMatrix();
//        GlStateManager.enableBlend();
//        GL11.glTranslatef(x, y, 0);
//        GL11.glScalef(width / 32.0F, height / 32.0F, 1);
//        Gui.drawModalRectWithCustomSizedTexture(0, 0, 8, 8, 8, 8, 64, 64);
//        GL11.glPopMatrix();
//    }
//
//    private void drawRect(int left, int top, int right, int bottom, int color) {
//        Gui.drawRect(left, top, right, bottom, color);
//    }
//}


package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

// code by wzhy233
public class TargetHud extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();

    public TargetHud() {
        super("TargetHUD", ModuleCategory.VISUAL);
    }

    @Listener
    public void onRender2D(EventRender2D event) {
        EntityLivingBase target = getTarget();
        if (!(target instanceof EntityPlayer))
            return;

        int x = 200; // HUD 的 X 位置
        int y = 200; // HUD 的 Y 位置
        int width = 120; // HUD 的宽度
        int height = 40; // HUD 的高度

        FontRenderer fr = mc.fontRendererObj;

        // 绘制背景
        drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 120).getRGB());

        // 绘制目标玩家头像
        NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
        if (info != null) {
            mc.getTextureManager().bindTexture(info.getLocationSkin());
            drawPlayerHead(x + 2, y + 2, 36, 36);
        }

        // 绘制目标玩家名字
        String name = target.getName();
        fr.drawStringWithShadow(name, x + 40, y + 2, -1);

        // 绘制目标玩家生命值
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        int healthColor = Color.HSBtoRGB(Math.max(0.0F, Math.min(health / maxHealth, 1.0F)) / 3.0F, 1.0F, 1.0F);
        String healthStr = String.format("%.1f", health / 2.0F);
        fr.drawStringWithShadow("❤ " + healthStr, x + 40, y + 14, healthColor);

        // 绘制与目标玩家的距离
        double distance = mc.thePlayer.getDistanceToEntity(target);
        String distanceStr = String.format("%.1f", distance);
        fr.drawStringWithShadow("Distant: " + distanceStr, x + 40, y + 26, -1);

        // 绘制目标玩家护甲值
        int armor = target.getTotalArmorValue();
        fr.drawStringWithShadow("Armor: " + armor, x + 120, y + 26, -1);
    }

    private EntityLivingBase getTarget() {
        if (mc.pointedEntity instanceof EntityLivingBase) {
            return (EntityLivingBase) mc.pointedEntity;
        }
        return null;
    }

    private void drawPlayerHead(int x, int y, int width, int height) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(width / 32.0F, height / 32.0F, 1);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 8, 8, 8, 8, 64, 64);
        GL11.glPopMatrix();
    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}
