package cn.yapeteam.yolbi.utils.font;

import com.fun.inject.injection.wrapper.impl.MinecraftWrapper;
import com.fun.inject.injection.wrapper.impl.gui.ScaledResolutionWrapper;
import com.fun.inject.injection.wrapper.impl.render.GlStateManagerWrapper;
import com.fun.utils.RenderManager;
import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.HieroSettings;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;

public class UnicodeFontRenderer {
   public final UnicodeFont font;
   public int FONT_HEIGHT = 9;
   ScaledResolutionWrapper sr;
   public String gs="";

   public UnicodeFontRenderer(Font awtFont) {
      HieroSettings hieroSettings = new HieroSettings();
      hieroSettings.setGlyphPageWidth(2048);
      hieroSettings.setGlyphPageHeight(2048);
      this.sr = new ScaledResolutionWrapper(MinecraftWrapper.get());
      hieroSettings.setFontSize((int)((float)awtFont.getSize() / 2.0F * (float)this.sr.getScaleFactor()));
      this.font = new UnicodeFont(awtFont, hieroSettings);//神B
      //this.font.addAsciiGlyphs();
      //this.font.set
      font.getEffects().add(new ColorEffect(Color.WHITE));


      this.FONT_HEIGHT = this.font.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789") / this.sr.getScaleFactor();
   }
   public int getHeight(){
      return this.FONT_HEIGHT;
   }
   public void load(String string){
      if(!gs.contains(string)) {
         font.addGlyphs(string);
         gs+=string;
         try {
            font.loadGlyphs();
         } catch (SlickException e) {}

      }
   }

   public float drawString(String string, float x, float y, int color) {
      if (string == null) {
         return 0.0F;
      } else {
         //this.sr = new ScaledResolutionWrapper(MinecraftWrapper.get());
         //Thread.currentThread().setContextClassLoader(Agent.classLoader);

         load(string);
         PoseStack stack = RenderManager.currentPoseStack;

         stack.pushPose();

         stack.scale(
            (float) (1.0F / (int)this.sr.getScaleFactor()),
            (float) (1.0F / (int)this.sr.getScaleFactor()),
            (float) (1.0F / (int)this.sr.getScaleFactor())
         );
         boolean blend = GL11.glIsEnabled(3042);
         boolean lighting = GL11.glIsEnabled(2896);
         boolean texture = GL11.glIsEnabled(3553);
         if (!blend) {
            GL11.glEnable(3042);
         }
         if (lighting) {
            GL11.glDisable(2896);
         }

         if (texture) {
            GL11.glDisable(3553);
         }
         //ClassLoader
         x *= (float)this.sr.getScaleFactor();
         y *= (float)this.sr.getScaleFactor();

         this.font.drawString((int)x, (int)y, string, new org.newdawn.slick.Color(color));
         if (texture) {
            GL11.glEnable(3553);
         }

         if (lighting) {
            GL11.glEnable(2896);
         }

         if (!blend) {
            GL11.glDisable(3042);
         }

         GlStateManagerWrapper.color(0.0F, 0.0F, 0.0F);
         stack.popPose(); //GL11.glPopMatrix();
         GlStateManagerWrapper.bindTexture(0);
         return x;
      }
   }

   public float drawStringWithShadow(String text, float x, float y, int color) {
      this.drawString(text, x + 0.5F, y + 0.5F, -16777216);
      return this.drawString(text, x, y, color);
   }

   public int getCharWidth(char c) {
      return this.getStringWidth(Character.toString(c));
   }

   public int getStringWidth(String string) {
      return (this.font.getWidth(string) / this.sr.getScaleFactor())+2;
   }

   public int getStringHeight(String string) {
      return this.font.getHeight(string) / this.sr.getScaleFactor();
   }

   public void drawCenteredString(String text, float x, float y, int color) {
      this.drawString(text, x - (float)(this.getStringWidth(text) / 2), y, color);
   }
}
