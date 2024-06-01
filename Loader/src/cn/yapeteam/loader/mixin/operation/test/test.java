package cn.yapeteam.loader.mixin.operation.test;

import cn.yapeteam.loader.Mapper;
import cn.yapeteam.loader.ResourceManager;
import cn.yapeteam.loader.mixin.Transformer;
import cn.yapeteam.loader.mixin.operation.impl.InjectOperation;

import java.io.File;
import java.nio.file.Files;

public class test {
    static class CustomLoader extends ClassLoader {
        public Class<?> load(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Throwable {
        Mapper.setMode(Mapper.Mode.None);
        Transformer transformer = new Transformer((name) -> ResourceManager.readStream(InjectOperation.class.getResourceAsStream("/" + name.getName().replace('.', '/') + ".class")));
        transformer.addMixin(source.class);
        byte[] bytes = transformer.transform().get("cn.yapeteam.loader.mixin.operation.test.target");
        Files.write(new File("target.class").toPath(), bytes);
        new CustomLoader().load(bytes).getMethod("func").invoke(null);
    }
}
