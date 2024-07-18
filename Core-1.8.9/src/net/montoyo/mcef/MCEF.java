package net.montoyo.mcef;

import cn.yapeteam.loader.ResourceManager;
import net.montoyo.mcef.client.ClientProxy;
import net.montoyo.mcef.utilities.Log;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class MCEF {

    public static final String VERSION = "1.20";
    public static boolean ENABLE_EXAMPLE;
    public static boolean SKIP_UPDATES;
    public static boolean WARN_UPDATES;
    public static boolean USE_FORGE_SPLASH;
    public static String FORCE_MIRROR = null;
    public static String HOME_PAGE;
    public static String[] CEF_ARGS = new String[0];
    public static boolean CHECK_VRAM_LEAK;
    public static SSLSocketFactory SSL_SOCKET_FACTORY;
    public static boolean SHUTDOWN_JCEF;
    public static boolean SECURE_MIRRORS_ONLY;
    public static MCEF INSTANCE = new MCEF();
    public static BaseProxy PROXY;

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
        importLetsEncryptCertificate();
        PROXY = new ClientProxy();
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

    //This is needed, otherwise for some reason HTTPS doesn't work
    private static void importLetsEncryptCertificate() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(ResourceManager.resources.getStream("cef/letsencryptauthorityx3.crt"));
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry("letsencrypt", cert);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);
            SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, tmf.getTrustManagers(), new SecureRandom());
            SSL_SOCKET_FACTORY = sslCtx.getSocketFactory();
            Log.info("Successfully loaded Let's Encrypt certificate", new Object[0]);
        } catch (Throwable t) {
            Log.error("Could not import Let's Encrypt certificate!! HTTPS downloads WILL fail...", new Object[0]);
            t.printStackTrace();
        }
    }
}
