module cn.yapeteam.loader {
    requires lwjgl;
    requires static lombok;
    requires gson;
    requires asm.all;
    requires cn.yapeteam.ymixin;
    requires minecraft;
    requires java.instrument;
    requires org.jetbrains.annotations;
    requires com.formdev.flatlaf;
    requires java.desktop;
    exports cn.yapeteam.loader.api.module.values;
    exports cn.yapeteam.loader.api.module.values.impl;
    exports cn.yapeteam.loader.logger;
    exports cn.yapeteam.loader;
    exports cn.yapeteam.loader.utils.vector;
    exports cn.yapeteam.loader.utils;
    exports cn.yapeteam.loader.api.module;
    // include other exports as necessary
}