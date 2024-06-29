package cn.yapeteam.wrapper.client;

import cn.yapeteam.loader.Version;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.wrapper.Wrapper;

public class MinecraftWrapper extends Wrapper {
    private static MinecraftWrapper INSTANCE;


    public MinecraftWrapper() {
        super(ClassUtils.getClass("net.minecraft.client.Minecraft"));

    }

    public static MinecraftWrapper getMinecraft() {
        return INSTANCE;
    }
}
