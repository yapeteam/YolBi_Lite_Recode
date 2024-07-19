package cn.yapeteam.yolbi.font;

import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.font.renderer.FontRenderer;
import lombok.Getter;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Getter
public class FontManager {
    public FontManager() {
        JelloRegular18 = createFontRenderer("JelloRegular.ttf", 18);
        JelloLight18 = createFontRenderer("JelloLight.ttf", 18);
        JelloMedium18 = createFontRenderer("JelloMedium.ttf", 18);
        PingFang12 = createFontRenderer("PingFang_Normal.ttf", 12);
        PingFang14 = createFontRenderer("PingFang_Normal.ttf", 14);
        PingFang18 = createFontRenderer("PingFang_Normal.ttf", 18);
        PingFangBold18 = createFontRenderer("PingFang_Bold.ttf", 18);
        FLUXICON14 = createFontRenderer("fluxicon.ttf", 18);
        RobotoLight = createFontRenderer("RobotoLight.ttf",16);
        default18 = createFontRenderer(new Font(null, Font.PLAIN, 18), 18);
    }

    private final AbstractFontRenderer JelloRegular18;
    private final AbstractFontRenderer JelloLight18;
    private final AbstractFontRenderer JelloMedium18;
    private final AbstractFontRenderer PingFang12;
    private final AbstractFontRenderer RobotoLight;
    private final AbstractFontRenderer PingFang14;
    private final AbstractFontRenderer PingFang18;
    private final AbstractFontRenderer PingFangBold18;
    private final AbstractFontRenderer FLUXICON14;
    private final AbstractFontRenderer default18;

    public static Font getFont(int size, InputStream is) {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            Logger.exception(ex);
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static FontRenderer createFontRenderer(String font, int size) {
        return new FontRenderer(getFont(size, new ByteArrayInputStream(Objects.requireNonNull(ResourceManager.resources.get("fonts/" + font)))), size / 2f);
    }

    public static FontRenderer createFontRenderer(Font font, int size) {
        return new FontRenderer(font, size / 2f);
    }
}
