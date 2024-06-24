module cn.yapeteam.ymixin {
    requires asm.all;
    requires org.jetbrains.annotations;
    requires static lombok;
    exports cn.yapeteam.ymixin.annotations;
    exports cn.yapeteam.ymixin.utils;
    exports cn.yapeteam.ymixin;
    // include other exports as necessary
}