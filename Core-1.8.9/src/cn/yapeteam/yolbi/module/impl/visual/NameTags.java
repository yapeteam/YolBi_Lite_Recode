package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.font.FontUtil;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.combat.AntiBot;
import cn.yapeteam.yolbi.module.impl.player.MurdererFinder;
import cn.yapeteam.yolbi.module.impl.visual.nametags.DistanceNameTag;
import cn.yapeteam.yolbi.module.impl.visual.nametags.DistanceShortNameTag;
import cn.yapeteam.yolbi.module.values.impl.BooleanValue;
import cn.yapeteam.yolbi.module.values.impl.ModeValue;
import cn.yapeteam.yolbi.module.values.impl.NumberValue;
import cn.yapeteam.yolbi.utils.player.PlayerUtil;
import cn.yapeteam.yolbi.utils.player.RotationsUtil;
import cn.yapeteam.yolbi.utils.reflect.ReflectUtil;
import cn.yapeteam.yolbi.utils.render.GLUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Sigma
 */
public class NameTags extends Module {
    public ModeValue<Modes> mode = new ModeValue<>("Mode", Modes.Distance, Modes.values());
    public static Map<EntityLivingBase, double[]> entityPositions = new HashMap<>();
    public BooleanValue invis = new BooleanValue("ShowInvisible", true);
    public static NumberValue<Double> alpha = new NumberValue<>("Alpha", 80d, 10d, 255d, 1d);
    public BooleanValue armor = new BooleanValue("ShowArmor", false);

    public NameTags() {
        super("NameTags", ModuleCategory.VISUAL);
        this.addValues(mode, invis, armor, alpha);
    }

    @Listener
    public void update(EventRender3D e) {
        try {
            this.updatePositions();
        } catch (Exception ignored) {
        }
    }

    @Listener(Priority.HIGH)
    public void onRender2D(EventRender2D e) {
        GlStateManager.pushMatrix();
        for (EntityLivingBase entity : entityPositions.keySet()) {
            switch (mode.getValue()) {
                case Distance:
                    DistanceNameTag.renderNameTag(entity, invis.getValue(), entityPositions, armor.getValue(), alpha.getValue().intValue());
                    break;
                case Short:
                    DistanceShortNameTag.renderNameTag(entity, invis.getValue(), entityPositions, armor.getValue(), alpha.getValue().intValue());
                    break;
            }
        }
        GlStateManager.popMatrix();
    }

    private void updatePositions() {
        entityPositions.clear();
        float pTicks = Objects.requireNonNull(ReflectUtil.Minecraft$getTimer(mc)).renderPartialTicks;
        for (Entity o : mc.theWorld.loadedEntityList) {
            if ((o != mc.thePlayer) && ((o instanceof EntityPlayer))
                    && (!o.isInvisible() || !this.invis.getValue())) {
                double x = o.lastTickPosX + (o.posX - o.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX;
                double y = o.lastTickPosY + (o.posY - o.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY;
                double z = o.lastTickPosZ + (o.posZ - o.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ;
                y += o.height + 0.2D;
                double[] res = convertTo2D(x, y, z);
                if (res == null) continue;
                if ((res[2] >= 0.0D) && (res[2] < 1.0D)) {
                    entityPositions.put((EntityLivingBase) o,
                            new double[]{res[0], res[1], Math.abs(convertTo2D(x, y + 1.0D, z, o)[1] - convertTo2D(x, y, z, o)[1]), res[2]});
                }
            }
        }
    }

    public static String getTag(EntityLivingBase entity) {
        StringBuilder sb = new StringBuilder();
        if (entity instanceof EntityPlayer && MurdererFinder.instance.findSword((EntityPlayer) entity) != -1)
            sb.append("\247c[Killer]\247f");
        if (AntiBot.isBot(entity))
            sb.append("§c§l[Bot]§r§7");
        else if (PlayerUtil.sameTeam(entity))
            sb.append("§a[Team]");
        else sb.append("§f");
        sb.append(entity.getDisplayName().getUnformattedText());
        return sb.toString();
    }

    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        float pTicks = Objects.requireNonNull(ReflectUtil.Minecraft$getTimer(mc)).renderPartialTicks;
        float prevYaw = mc.thePlayer.rotationYaw;
        float prevPrevYaw = mc.thePlayer.prevRotationYaw;
        float[] rotations = RotationsUtil.getRotationsToPosition(
                ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
                ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
                ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6D
        );
        mc.getRenderViewEntity().rotationYaw = (mc.getRenderViewEntity().prevRotationYaw = rotations[0]);
        ReflectUtil.EntityRenderer$setupCameraTransform(mc.entityRenderer, pTicks, 0);
        double[] convertedPoints = convertTo2D(x, y, z);
        mc.getRenderViewEntity().rotationYaw = prevYaw;
        mc.getRenderViewEntity().prevRotationYaw = prevPrevYaw;
        ReflectUtil.EntityRenderer$setupCameraTransform(mc.entityRenderer, pTicks, 0);
        return convertedPoints;
    }

    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }

    public static int clampColor(int p_clamp_int_0_, int p_clamp_int_0_2, int p_clamp_int_0_3, int p_clamp_int_0_4) {
        return MathHelper.clamp_int(p_clamp_int_0_4, 0, 255) << 24 | MathHelper.clamp_int(p_clamp_int_0_, 0, 255) << 16 | MathHelper.clamp_int(p_clamp_int_0_2, 0, 255) << 8 | MathHelper.clamp_int(p_clamp_int_0_3, 0, 255);
    }

    public static int getColor(String s) {
        int color = Color.RED.getRGB();
        int i = 0;
        while (i < s.length()) {
            if (s.charAt(i) == '§' && i + 1 < s.length()) {
                int index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(s.charAt(i + 1)));
                if (index < 16) {
                    try {
                        Color c = new Color(FontUtil.colorCode[index]);
                        color = clampColor(c.getRed(), c.getGreen(), c.getBlue(), 255);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                    }
                }
            }
            ++i;
        }
        return color;
    }

    public static void renderArmor(EntityLivingBase entity) {
        ArrayList<ItemStack> list = new ArrayList<>();
        int j = 0;
        while (j < 5) {
            ItemStack equipmentInSlot = entity.getEquipmentInSlot(j);
            if (equipmentInSlot != null) {
                list.add(equipmentInSlot);
            }
            ++j;
        }
        int p_renderItemOverlays_3_ = -(list.size() * 9);
        for (ItemStack p_getEnchantmentLevel_1_ : list) {
            GLUtils.enableGUIStandardItemLighting();
            mc.getRenderItem().zLevel = -150.0f;
            fixGlintShit();
            mc.getRenderItem().renderItemIntoGUI(p_getEnchantmentLevel_1_, p_renderItemOverlays_3_ + 6, (int) (-42.0f));
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, p_getEnchantmentLevel_1_, p_renderItemOverlays_3_, -42);
            mc.getRenderItem().zLevel = 0.0f;
            p_renderItemOverlays_3_ += 3;
            GLUtils.disableStandardItemLighting();
            if (p_getEnchantmentLevel_1_ != null) {
                int n13 = 21;
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, p_getEnchantmentLevel_1_);
                int enchantmentLevel2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, p_getEnchantmentLevel_1_);
                int enchantmentLevel3 = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, p_getEnchantmentLevel_1_);
                if (enchantmentLevel > 0) {
                    drawEnchantTag("Sh" + getColor(enchantmentLevel) + enchantmentLevel, p_renderItemOverlays_3_, n13);
                    n13 += 6;
                }
                if (enchantmentLevel2 > 0) {
                    drawEnchantTag("Fir" + getColor(enchantmentLevel2) + enchantmentLevel2, p_renderItemOverlays_3_, n13);
                    n13 += 6;
                }
                if (enchantmentLevel3 > 0) {
                    drawEnchantTag("Kb" + getColor(enchantmentLevel3) + enchantmentLevel3, p_renderItemOverlays_3_, n13);
                } else if (p_getEnchantmentLevel_1_.getItem() instanceof ItemArmor) {
                    int enchantmentLevel4 = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, p_getEnchantmentLevel_1_);
                    int enchantmentLevel5 = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, p_getEnchantmentLevel_1_);
                    int enchantmentLevel6 = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, p_getEnchantmentLevel_1_);
                    if (enchantmentLevel4 > 0) {
                        drawEnchantTag("P" + getColor(enchantmentLevel4) + enchantmentLevel4, p_renderItemOverlays_3_, n13);
                        n13 += 6;
                    }
                    if (enchantmentLevel5 > 0) {
                        drawEnchantTag("Th" + getColor(enchantmentLevel5) + enchantmentLevel5, p_renderItemOverlays_3_, n13);
                        n13 += 6;
                    }
                    if (enchantmentLevel6 > 0) {
                        drawEnchantTag("Unb" + getColor(enchantmentLevel6) + enchantmentLevel6, p_renderItemOverlays_3_, n13);
                    }
                } else if (p_getEnchantmentLevel_1_.getItem() instanceof ItemBow) {
                    int enchantmentLevel7 = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, p_getEnchantmentLevel_1_);
                    int enchantmentLevel8 = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, p_getEnchantmentLevel_1_);
                    int enchantmentLevel9 = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, p_getEnchantmentLevel_1_);
                    if (enchantmentLevel7 > 0) {
                        drawEnchantTag("Pow" + getColor(enchantmentLevel7) + enchantmentLevel7, p_renderItemOverlays_3_, n13);
                        n13 += 6;
                    }
                    if (enchantmentLevel8 > 0) {
                        drawEnchantTag("Pun" + getColor(enchantmentLevel8) + enchantmentLevel8, p_renderItemOverlays_3_, n13);
                        n13 += 6;
                    }
                    if (enchantmentLevel9 > 0) {
                        drawEnchantTag("Fir" + getColor(enchantmentLevel9) + enchantmentLevel9, p_renderItemOverlays_3_, n13);
                    }
                } else if (p_getEnchantmentLevel_1_.getRarity() == EnumRarity.EPIC) {
                    drawEnchantTag("§6§lGod", p_renderItemOverlays_3_ - 2, n13);
                }
                float n14 = (float) (p_renderItemOverlays_3_ * 1.05) - 2.0f;

                if (p_getEnchantmentLevel_1_.getMaxDamage() - p_getEnchantmentLevel_1_.getItemDamage() > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableDepth();
                    YolBi.instance.getFontManager().getPingFang12().drawString("" + (p_getEnchantmentLevel_1_.getMaxDamage() - p_getEnchantmentLevel_1_.getItemDamage()), n14 + 6.0f, -32.0f, -1);
                    GlStateManager.enableDepth();
                    GlStateManager.popMatrix();
                }
                p_renderItemOverlays_3_ += 12;
            }
        }
    }

    private static void drawEnchantTag(String s, int n, int n2) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        n2 -= 6;
        YolBi.instance.getFontManager().getPingFang10().drawStringWithShadow(s, (float) (n + 9), (float) (-30 - n2), new Color(255));
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private static String getColor(int n) {
        if (n != 1) {
            if (n == 2) {
                return "§a";
            }
            if (n == 3) {
                return "§3";
            }
            if (n == 4) {
                return "§4";
            }
            if (n >= 5) {
                return "§6";
            }
        }
        return "§f";
    }

    public enum Modes {
        Distance,
        Short
    }
}
