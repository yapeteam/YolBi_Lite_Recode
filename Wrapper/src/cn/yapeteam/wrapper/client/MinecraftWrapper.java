package cn.yapeteam.wrapper.client;

import cn.yapeteam.wrapper.Wrapper;
import net.minecraft.client.Minecraft;

public class MinecraftWrapper extends Wrapper {
    private static final MinecraftWrapper INSTANCE = new MinecraftWrapper();


    public MinecraftWrapper() {
        super(Minecraft.getMinecraft());
    }

    public static MinecraftWrapper getMinecraft() {
        return INSTANCE;
    }
}
