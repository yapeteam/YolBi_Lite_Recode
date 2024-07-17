package cn.yapeteam.yolbi.utils.font;





import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;

public class FontManager {
    public static FontRenderer inkFree;
    public static FontRenderer tenacity;
    public static FontRenderer tenacity20;
    public static FontRenderer simkai;
    public static void init(){

    }
    public static Font getFont(int size, InputStream is) {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT,is).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }
    public static FontRenderer createFontRenderer(InputStream font,int size) {
        return new FontRenderer(getFont(size,font),size/2f);
    }

}
