package cn.yapeteam.yolbi.utils.reflect;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.ymixin.utils.Mapper;
import com.mojang.blaze3d.platform.NativeImage;

import java.lang.reflect.Field;

public class ReflectUtils {
    public static Field NativeImage$pixels;

    static {
        try {
            NativeImage$pixels = NativeImage.class.getDeclaredField(Mapper.map("com.mojang.blaze3d.platform.NativeImage", "pixels", null, Mapper.Type.Field));
        } catch (NoSuchFieldException e) {
            Logger.exception(e);
        }
    }

    public static long NativeImage$pixels(NativeImage obj) {
        try {
            return (long) NativeImage$pixels.get(obj);
        } catch (IllegalAccessException e) {
            Logger.exception(e);
        }
        return 0;
    }
}
