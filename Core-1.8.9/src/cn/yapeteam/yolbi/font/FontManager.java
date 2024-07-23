package cn.yapeteam.yolbi.font;

import cn.yapeteam.yolbi.font.cfont.CFontRenderer;
import lombok.Getter;

import java.awt.*;

@Getter
public class FontManager {
    public FontManager() {
        JelloRegular18 = new CFontRenderer("JelloRegular.ttf", 18, Font.PLAIN, true, true);
        JelloLight18 = new CFontRenderer("JelloLight.ttf", 18, Font.PLAIN, true, true);
        JelloMedium18 = new CFontRenderer("JelloMedium.ttf", 18, Font.PLAIN, true, true);
        PingFang10 = new CFontRenderer("PingFang_Normal.ttf", 10, Font.PLAIN, true, true);
        PingFang12 = new CFontRenderer("PingFang_Normal.ttf", 12, Font.PLAIN, true, true);
        PingFang13 = new CFontRenderer("PingFang_Normal.ttf", 13, Font.PLAIN, true, true);
        PingFang14 = new CFontRenderer("PingFang_Normal.ttf", 14, Font.PLAIN, true, true);
        PingFang18 = new CFontRenderer("PingFang_Normal.ttf", 18, Font.PLAIN, true, true);
        PingFang16 = new CFontRenderer("PingFang_Normal.ttf", 16, Font.PLAIN, true, true);
        PingFangBold18 = new CFontRenderer("PingFang_Bold.ttf", 18, Font.PLAIN, true, true);
        FLUXICON14 = new CFontRenderer("fluxicon.ttf", 18, Font.PLAIN, true, true);
        RobotoLight = new CFontRenderer("RobotoLight.ttf", 16, Font.PLAIN, true, true);
        default18 = new CFontRenderer(new Font(null, Font.PLAIN, 18), true, true);
    }

    private final AbstractFontRenderer JelloRegular18;
    private final AbstractFontRenderer JelloLight18;
    private final AbstractFontRenderer JelloMedium18;
    private final AbstractFontRenderer PingFang10;
    private final AbstractFontRenderer PingFang12;
    private final AbstractFontRenderer PingFang13;
    private final AbstractFontRenderer PingFang14;
    private final AbstractFontRenderer PingFang16;
    private final AbstractFontRenderer PingFang18;
    private final AbstractFontRenderer PingFangBold18;
    private final AbstractFontRenderer FLUXICON14;
    private final AbstractFontRenderer default18;
    private final AbstractFontRenderer RobotoLight;
}
