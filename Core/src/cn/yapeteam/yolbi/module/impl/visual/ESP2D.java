package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.loader.api.module.ModuleCategory;
import cn.yapeteam.loader.api.module.ModuleInfo;
import cn.yapeteam.loader.api.module.values.impl.BooleanValue;
import cn.yapeteam.loader.api.module.values.impl.ModeValue;
import cn.yapeteam.loader.api.module.values.impl.NumberValue;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.vector.Vector4d;
import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.utils.math.MathUtils;
import cn.yapeteam.yolbi.utils.player.InventoryUtils;
import cn.yapeteam.yolbi.utils.render.ESPUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
@ModuleInfo(name = "ESP2D", category = ModuleCategory.VISUAL)
public class ESP2D extends Module {
    private final BooleanValue Range = new BooleanValue("Range", false);

    private final NumberValue<Integer> DisplayRange = new NumberValue<>("DisplayRange", Range::getValue, 32, 10, 64, 1);

    private final ModeValue<String> colorMode = new ModeValue<>("Color Mode", "Custom", "Custom");

    public final BooleanValue outline = new BooleanValue("Outline", true);
    public final ModeValue<String> boxMode = new ModeValue<>("Mode", outline::getValue, "Box", "Box", "Gradient");
    public final BooleanValue outlineFont = new BooleanValue("OutlineFont", true);
    public final BooleanValue healthBar = new BooleanValue("Health-bar", true);
    public final ModeValue<String> hpBarMode = new ModeValue<>("HBar-Mode", healthBar::getValue, "Dot", "Dot", "Line");
    public final BooleanValue healthNumber = new BooleanValue("Health-Number", healthBar::getValue, true);
    public final ModeValue<String> hpMode = new ModeValue<>("HP-Mode", () -> healthNumber.getValue() && healthBar.getValue(), "Health", "Health", "Percent");
    public final BooleanValue hoverValue = new BooleanValue("Details-HoverOnly", false);
    public final BooleanValue itemTagsValue = new BooleanValue("ItemTags", true);
    public final BooleanValue itemValue = new BooleanValue("Item", itemTagsValue::getValue, true);
    public final BooleanValue tagsValue = new BooleanValue("Tags", true);
    public final BooleanValue tagsBGValue = new BooleanValue("Tags-Background", () -> tagsValue.getValue() || itemTagsValue.getValue(), true);
    public final BooleanValue armorBar = new BooleanValue("ArmorBar", true);
    public final BooleanValue armorItems = new BooleanValue("ArmorItems", true);
    public final BooleanValue armorDur = new BooleanValue("ArmorDur", armorItems::getValue, true);
    public final BooleanValue friendColor = new BooleanValue("FriendColor", true);
    public final BooleanValue localPlayer = new BooleanValue("Local-Player", true);
    public final BooleanValue mobs = new BooleanValue("Mobs", false);
    public final BooleanValue animals = new BooleanValue("Animals", false);

    private final int backgroundColor = new Color(0, 0, 0, 120).getRGB();
    private final int black = Color.BLACK.getRGB();

    public static final ArrayList<Entity> collectedEntities = new ArrayList<>();

    public ESP2D() {
        addValues(Range, DisplayRange, colorMode, outline, boxMode, outlineFont, healthBar, hpBarMode, healthNumber, hpMode, hoverValue, itemTagsValue, itemValue, tagsValue, tagsBGValue, armorBar, armorItems, armorDur, friendColor, localPlayer, mobs, animals);
    }


    @Override
    public void onDisable() {
        collectedEntities.clear();
    }

    @Listener
    public void onRender2D(EventRender2D event) {
        try {
            this.collectEntities();
            int black = this.black;
            boolean health = this.healthBar.getValue();
            for (Entity collectedEntity : collectedEntities) {
                int m;
                int color = this.getColor().getRGB();
                Vector4d position = ESPUtil.get(collectedEntity);
                if (position == null) continue;
                double posX = position.getX();
                double posY = position.getY();
                double endPosX = position.getZ();
                double endPosY = position.getW();
                String entityName = collectedEntity.getName(); // Assuming 'entity' is the entity you're working with
                if (this.boxMode.getValue().equals("Box")) {
                    newDrawRect(entityName + "_box_top", posX - 1.0, posY, posX + 0.5, endPosY + 0.5, black);
                    newDrawRect(entityName + "_box_left", posX - 1.0, posY - 0.5, endPosX + 0.5, posY + 0.5 + 0.5, black);
                    newDrawRect(entityName + "_box_right", endPosX - 0.5 - 0.5, posY, endPosX + 0.5, endPosY + 0.5, black);
                    newDrawRect(entityName + "_box_bottom", posX - 1.0, endPosY - 0.5 - 0.5, endPosX + 0.5, endPosY + 0.5, black);
                    newDrawRect(entityName + "_box_inner_left", posX - 0.5, posY, posX + 0.5 - 0.5, endPosY, color);
                    newDrawRect(entityName + "_box_inner_bottom", posX, endPosY - 0.5, endPosX, endPosY, color);
                    newDrawRect(entityName + "_box_inner_top", posX - 0.5, posY, endPosX, posY + 0.5, color);
                    newDrawRect(entityName + "_box_inner_right", endPosX - 0.5, posY, endPosX, endPosY, color);
                } else {
                    newDrawRect(entityName + "_gradient_top_left", posX + 0.5, posY, posX - 1.0, posY + (endPosY - posY) / 4.0 + 0.5, black);
                    newDrawRect(entityName + "_gradient_bottom_left", posX - 1.0, endPosY, posX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black);
                    newDrawRect(entityName + "_gradient_top_middle", posX - 1.0, posY - 0.5, posX + (endPosX - posX) / 3.0 + 0.5, posY + 1.0, black);
                    newDrawRect(entityName + "_gradient_top_right", endPosX - (endPosX - posX) / 3.0 - 0.5, posY - 0.5, endPosX, posY + 1.0, black);
                    newDrawRect(entityName + "_gradient_top_left", endPosX - 1.0, posY, endPosX + 0.5, posY + (endPosY - posY) / 4.0 + 0.5, black);
                    newDrawRect(entityName + "_gradient_bottom_left", endPosX - 1.0, endPosY, endPosX + 0.5, endPosY - (endPosY - posY) / 4.0 - 0.5, black);
                    newDrawRect(entityName + "_gradient_bottom_middle", posX - 1.0, endPosY - 1.0, posX + (endPosX - posX) / 3.0 + 0.5, endPosY + 0.5, black);
                    newDrawRect(entityName + "_gradient_bottom_right", endPosX - (endPosX - posX) / 3.0 - 0.5, endPosY - 1.0, endPosX + 0.5, endPosY + 0.5, black);
                    newDrawRect(entityName + "_gradient_inner_top_left", posX, posY, posX - 0.5, posY + (endPosY - posY) / 4.0, color);
                    newDrawRect(entityName + "_gradient_inner_bottom_left", posX, endPosY, posX - 0.5, endPosY - (endPosY - posY) / 4.0, color);
                    newDrawRect(entityName + "_gradient_inner_top_middle", posX - 0.5, posY, posX + (endPosX - posX) / 3.0, posY + 0.5, color);
                    newDrawRect(entityName + "_gradient_inner_top_right", endPosX - (endPosX - posX) / 3.0, posY, endPosX, posY + 0.5, color);
                    newDrawRect(entityName + "_gradient_inner_top_left", endPosX - 0.5, posY, endPosX, posY + (endPosY - posY) / 4.0, color);
                    newDrawRect(entityName + "_gradient_inner_bottom_left", endPosX - 0.5, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0, color);
                    newDrawRect(entityName + "_gradient_inner_bottom_middle", posX, endPosY - 0.5, posX + (endPosX - posX) / 3.0, endPosY, color);
                    newDrawRect(entityName + "_gradient_inner_bottom_right", endPosX - (endPosX - posX) / 3.0, endPosY - 0.5, endPosX - 0.5, endPosY, color);
                }
                if (!(collectedEntity instanceof EntityLivingBase)) continue;
                EntityLivingBase entityLivingBase = (EntityLivingBase) collectedEntity;
                if (health) {
                    float EntityHealth = entityLivingBase.getHealth();
                    float MaxHealth = entityLivingBase.getMaxHealth();

                    double HealthRatio = EntityHealth / MaxHealth;

                    newDrawRect(entityName + "_health", posX - 3.5, posY - 0.5, posX - 1.5, endPosY + 0.5, this.backgroundColor);


                    if (EntityHealth > 0.0f) {
                        Color healthColor = getHealthColor(EntityHealth, MaxHealth);
                        double gap = 0.5; // Define the size of the gap between each health bar
                        double barheight = (endPosY - posY - gap * EntityHealth) / 20.0; // Define the height of each health bar
                        double yvalue = endPosY;
                        if (EntityHealth > 40) {
                            // prevent lagging out because of too many rectangles
                            YolBi.instance.getRenderManager().rectangle(entityName + "_health_total", posX - 3.5, posY - 0.5, 1.5, (endPosY - posY) * HealthRatio, healthColor);
                        } else {
                            for (int i = 0; i < EntityHealth; i++) {
                                YolBi.instance.getRenderManager().rectangle(entityName + "_health_" + i, posX - 1.5, yvalue - barheight, 1, barheight, healthColor);
                                yvalue -= barheight + gap;
                            }
                        }
                    }
                }
                if (this.armorBar.getValue() && collectedEntity instanceof EntityPlayer) {
                    double constHeight = (endPosY - posY) / 4.0;
                    for (m = 4; m > 0; --m) {
                        String peice = "helmet";
                        ItemStack armorStack = entityLivingBase.getCurrentArmor(m - 1);
                        if (m == 3) {
                            peice = "chestplate";
                        }
                        if (m == 2) {
                            peice = "leggings";
                        }
                        if (m == 1) {
                            peice = "boots";
                        }
                        double theHeight = constHeight + 0.25;
                        if (armorStack != null) {
                            newDrawRect(entityName + "_armor_" + peice, endPosX + 1.5, endPosY + 0.5 - theHeight * (double) m, endPosX + 3.5, endPosY + 0.5 - theHeight * (double) (m - 1), new Color(0, 0, 0, 120).getRGB());
                            newDrawRect(entityName + "_armor_" + peice + "_durability", endPosX + 2.0, endPosY + 0.5 - theHeight * (double) (m) - 0.25, endPosX + 3.0, endPosY + 0.5 - theHeight * (double) (m) - 0.25 - (constHeight - 0.25) * MathUtils.clamp((double) InventoryUtils.getItemDurability(armorStack) / (double) armorStack.getMaxDamage(), 0.0, 1.0), new Color(0, 255, 255).getRGB());
                            newDrawRect(entityName + "_armor_" + peice, endPosX + 1.5, endPosY + 0.5 - theHeight * (double) m, endPosX + 3.5, endPosY + 0.5 - theHeight * (double) (m - 1), new Color(0, 0, 0, 120).getRGB());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public static Color getHealthColor(float health, float maxHealth) {
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
        float progress = health / maxHealth;
        return blendColors(fractions, colors, progress).brighter();
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return blend(colorRange[0], colorRange[1], 1.0f - weight);
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = color1.getColorComponents(new float[3]);
        float[] rgb2 = color2.getColorComponents(new float[3]);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return color3;
    }

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint = 0;
        int[] range = new int[2];

        while (startPoint < fractions.length && fractions[startPoint] <= progress) startPoint++;

        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color fade(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0f + (float) index / (float) count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static void newDrawRect(String id, double left, double top, double right, double bottom, int hex) {
        float alpha = (float) (hex >> 24 & 0xFF) / 255.0f;
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        YolBi.instance.getRenderManager().rectangle(id, left, top, Math.abs(right - left), Math.abs(bottom - top), new Color(red, green, blue, alpha));
    }


    public Color getColor() {
        if (colorMode.getValue().equals("Custom"))
            return new Color(200, 200, 200);
        return fade(new Color(200, 200, 200), 0, 100);
    }

    private void collectEntities() {
        collectedEntities.clear();
        List<Entity> EntitiesList = mc.theWorld.loadedEntityList;
        for (Entity playerEntity : EntitiesList) {
            if (Range.getValue() && playerEntity.getDistanceSqToEntity(mc.thePlayer) > DisplayRange.getValue().doubleValue())
                continue;
            if (!this.isSelected(playerEntity, false) && (!this.localPlayer.getValue() || !(playerEntity instanceof EntityPlayerSP) || mc.gameSettings.thirdPersonView == 0))
                continue;
            collectedEntities.add(playerEntity);
        }
    }

    public boolean isSelected(Entity entity, boolean canAttackCheck) {
        if (entity instanceof EntityLivingBase && entity.isEntityAlive() && entity != mc.thePlayer) {
            if (entity instanceof EntityPlayer) {
                if (canAttackCheck) {
                    if (((EntityPlayer) entity).isSpectator()) {
                        return false;
                    }
                    return !((EntityPlayer) entity).isPlayerSleeping();
                }
                return true;
            }
            return this.isMob(entity) && this.mobs.getValue() || this.isAnimal(entity) && this.animals.getValue();
        }
        return false;
    }

    public boolean isAnimal(Entity entity) {
        return entity instanceof EntityAnimal || entity instanceof EntitySquid || entity instanceof EntityGolem || entity instanceof EntityVillager || entity instanceof EntityBat;
    }

    public boolean isMob(Entity entity) {
        return entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast || entity instanceof EntityDragon;
    }
}
