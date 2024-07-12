-injars build/injection-1.12.2/injection-1.12.2.jar
-outjars build/injection-1.12.2/injection-1.12.2-o.jar

-libraryjars minecraft-lib/minecraft-1.12.2.jar
-libraryjars Builder/libs/rt.jar

-target 1.8
-forceprocessing
-dontshrink
-dontoptimize
-printmapping build/injection-1.12.2.mapping
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
-overloadaggressively
-dontusemixedcaseclassnames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
-dontwarn
-ignorewarnings



-keep class cn.yapeteam.yolbi.mixin.injection.** {
    <fields>;
    <methods>;
}

-keep class cn.yapeteam.yolbi.Loader {
    public static void start();
}

-keep,allowshrinking class cn.yapeteam.yolbi.mixin.injection.** {
    <fields>;
    <methods>;
}

# Keep - Applications. Keep all application classes, along with their 'main' methods.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver

# Also keep - Swing UI L&F. Keep all extensions of javax.swing.plaf.ComponentUI,
# along with the special 'createUI' method.
-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

# Keep - Native method names. Keep all native class/method names.
-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}
