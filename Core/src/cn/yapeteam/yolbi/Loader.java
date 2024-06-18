package cn.yapeteam.yolbi;

import cn.yapeteam.loader.JVMTIWrapper;
import cn.yapeteam.loader.NativeWrapper;
import cn.yapeteam.loader.SocketSender;
import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.loader.utils.ASMUtils;
import cn.yapeteam.loader.utils.ClassUtils;
import cn.yapeteam.ymixin.Transformer;
import cn.yapeteam.ymixin.annotations.Mixin;
import cn.yapeteam.yolbi.mixin.MixinManager;
import cn.yapeteam.yolbi.mixin.injection.MixinLoadHook;
import org.objectweb.asm_9_2.tree.ClassNode;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings("unused")
public class Loader {
    public static void start() {
        try {
            if (JVMTIWrapper.instance == null)
                JVMTIWrapper.instance = new NativeWrapper();
            Logger.info("Start Loading!");
            Logger.warn("Loading Initialize Hook...");
            Transformer transformer = new Transformer(JVMTIWrapper.instance::getClassBytes);
            byte[] hookClassBytes = ClassUtils.getClassBytes(MixinLoadHook.class.getName());
            ClassNode hookClassNode = ASMUtils.node(hookClassBytes);
            transformer.addMixin(hookClassNode);
            Class<?> targetClass = Objects.requireNonNull(Mixin.Helper.getAnnotation(hookClassNode)).value();
            byte[] transformedBytes = transformer.transform().get(targetClass.getName());
            Logger.info("Redefined {} ReturnCode: {}", targetClass, JVMTIWrapper.instance.redefineClass(targetClass, transformedBytes));
            Logger.info("Initializing MixinLoader...");
            MixinManager.init();
            Logger.warn("Start transforming!");
            MixinManager.transform();
            Logger.success("Welcome {} ver {}", YolBi.name, YolBi.version);
            SocketSender.send("CLOSE");
            SocketSender.close();
            // YolBi.initialize();
        } catch (Throwable e) {
            Logger.exception(e);
            try {
                Logger.writeCache();
                Desktop.getDesktop().open(Logger.getLog());
            } catch (Throwable ignored) {
            }
        }
    }
}
