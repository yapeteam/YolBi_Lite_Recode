package cn.yapeteam.wrapper;

import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import lombok.Getter;

// Minecraft.getMinecraft().thePlayer.swingItem();
// vanilla:
// invokestatic net/minecraft/client/Minecraft.getMinecraft()Lnet/minecraft/client/Minecraft;
// getfield net/minecraft/client/Minecraft.thePlayer Lnet/minecraft/entity/player/EntityPlayer;
// invokevirtual net/minecraft/entity/player/EntityPlayer.swingItem()V

// transformed:
//invokestatic cn/yapeteam/loader/wrapper/MinecraftWrapper.getMinecraft()Lcn/yapeteam/loader/wrapper/MinecraftWrapper;
//getfield cn/yapeteam/loader/wrapper/client/MinecraftWrapper.thePlayer Lcn/yapeteam/loader/wrapper/client/entity/EntityPlayerSPWrapper;
//invokevirtual cn/yapeteam/loader/wrapper/client/entity/EntityPlayerSPWrapper.swingItem()V

@Getter
public class Wrapper {
    private final Object instance;

    public Wrapper(Object instance) {
        this.instance = instance;
    }

    public static Class<?> getClass(String className) {
        return ClassUtils.getClass(Mapper.getObfClass(className));
    }
}
