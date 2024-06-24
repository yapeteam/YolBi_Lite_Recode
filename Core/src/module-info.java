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

    // Add this line to allow access to the internal JDK class
    opens javafx.embed.swing to java.base;
}