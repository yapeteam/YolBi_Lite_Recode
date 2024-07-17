package cn.yapeteam.yolbi.font.renderer;

import net.minecraft.resources.ResourceLocation;

public class Texture extends ResourceLocation {
    public Texture(String path) {
        super("thunderhack", validatePath(path));
    }

    public Texture(ResourceLocation i) {
        super(i.getNamespace(), i.getPath());
    }

    static String validatePath(String path) {
        if (isValidResourceLocation(path)) {
            return path;
        }
        StringBuilder ret = new StringBuilder();
        for (char c : path.toLowerCase().toCharArray()) {
            if (isAllowedInResourceLocation(c)) {
                ret.append(c);
            }
        }
        return ret.toString();
    }
}