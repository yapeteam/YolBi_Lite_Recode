module cn.yapeteam.yolbi {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires static lombok;
    requires minecraft;
    requires lwjgl;
    requires cn.yapeteam.loader;
    requires gson;
    requires cn.yapeteam.ymixin;
    requires asm.all;
    requires netty.all;
    requires jdk.httpserver;
    requires lwjgl.util;
    requires commons.lang3;
    requires org.jetbrains.annotations;
    requires guava;

    // Modify this line to open to javafx.swing instead of java.base
    opens javafx.swing to java.base;
}