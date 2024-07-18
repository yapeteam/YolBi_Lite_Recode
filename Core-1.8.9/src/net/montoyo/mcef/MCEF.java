package net.montoyo.mcef;

import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.utilities.Log;

import javax.net.ssl.SSLSocketFactory;

public class MCEF {
    public static boolean ENABLE_EXAMPLE;
    public static boolean SKIP_UPDATES;
    public static boolean WARN_UPDATES;
    public static boolean USE_FORGE_SPLASH;
    public static String HOME_PAGE;
    public static String[] CEF_ARGS = new String[0];
    public static boolean CHECK_VRAM_LEAK;
    public static SSLSocketFactory SSL_SOCKET_FACTORY;
    public static boolean SHUTDOWN_JCEF;
    public static boolean SECURE_MIRRORS_ONLY;
    public static MCEF INSTANCE = new MCEF();
    public static final BaseProxy PROXY = new ClientProxy();

    public void onPreInit() {
        Log.info("Loading MCEF config...");

        //Config: main
        SKIP_UPDATES = true;// cfg.getBoolean("skipUpdates"      , "main", false          , "Do not update binaries.");
        WARN_UPDATES = true;// cfg.getBoolean("warnUpdates"      , "main", true           , "Tells in the chat if a new version of MCEF is available.");
        USE_FORGE_SPLASH = false;// cfg.getBoolean("useForgeSplash"   , "main", true           , "Use Forge's splash screen to display resource download progress (may be unstable).");
        CEF_ARGS = new String[]{};        //= cfg.getString ("cefArgs"          , "main", "--disable-gpu", "Command line arguments passed to CEF. For advanced users.").split("\\s+");
        SHUTDOWN_JCEF = false;// cfg.getBoolean("shutdownJcef"     , "main", false          , "Set this to true if your Java process hangs after closing Minecraft. This is disabled by default because it makes the launcher think Minecraft crashed...");
        SECURE_MIRRORS_ONLY = false; //cfg.getBoolean("secureMirrorsOnly", "main", true           , "Only enable secure (HTTPS) mirror. This should be kept to true unless you know what you're doing.");

        //Config: exampleBrowser
        ENABLE_EXAMPLE = true;
        HOME_PAGE = "https://www.bilibili.com";// cfg.getString("home", "exampleBrowser", "mod://mcef/home.html", "The home page of the F10 browser.");

        //Config: debug
        CHECK_VRAM_LEAK = true;
        PROXY.onPreInit();
    }

    public void onInit() {
        PROXY.onInit();
    }

    //Called by Minecraft.run() if the ShutdownPatcher succeeded
    public static void onMinecraftShutdown() {
        Log.info("Minecraft shutdown hook called!");
        PROXY.onShutdown();
    }
}
