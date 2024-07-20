package net.montoyo.mcef;

import cn.yapeteam.loader.Loader;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.utilities.Log;
import net.montoyo.mcef.utilities.Util;

import java.io.File;

public class MCEF {
    public static boolean SKIP_UPDATES;
    public static boolean WARN_UPDATES;
    public static boolean USE_FORGE_SPLASH;
    public static String HOME_PAGE;
    public static String[] CEF_ARGS = new String[0];
    public static boolean CHECK_VRAM_LEAK;
    public static boolean SHUTDOWN_JCEF;
    public static boolean SECURE_MIRRORS_ONLY;
    public static MCEF INSTANCE = new MCEF();
    public static final BaseProxy PROXY = new ClientProxy();

    public void onInit(String mcRoot) {
        Log.info("Extracting natives...");
        try {
            Util.unzip(new File(Loader.YOLBI_DIR, "resources/cef/natives.zip").getAbsolutePath(), mcRoot);
        } catch (Exception e) {
            Log.error("Failed to extract natives");
            Log.exception(e);
            ClientProxy.VIRTUAL = true;
            return;
        }
        Log.info("Loading MCEF config...");

        //Config: main
        SKIP_UPDATES = true;
        WARN_UPDATES = true;
        USE_FORGE_SPLASH = false;
        CEF_ARGS = new String[]{"--disable-gpu"};
        SHUTDOWN_JCEF = false;
        SECURE_MIRRORS_ONLY = false;

        //Config: debug
        CHECK_VRAM_LEAK = true;
        PROXY.onInit(mcRoot);
    }

    public static void onMinecraftShutdown() {
        Log.info("Minecraft shutdown hook called!");
        PROXY.onShutdown();
    }
}
