package net.montoyo.mcef.client;

import cn.yapeteam.loader.Loader;
import lombok.Getter;
import net.montoyo.mcef.BaseProxy;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IDisplayHandler;
import net.montoyo.mcef.api.IJSQueryHandler;
import net.montoyo.mcef.api.IScheme;
import net.montoyo.mcef.utilities.Log;
import net.montoyo.mcef.virtual.VirtualBrowser;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefBrowserOsr;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefMessageRouter.CefMessageRouterConfig;
import org.cef.browser.CefRenderer;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientProxy extends BaseProxy {
    public static String ROOT = ".";
    public static boolean VIRTUAL = false;

    @Getter
    private CefApp cefApp;
    private CefClient cefClient;
    private CefMessageRouter cefRouter;
    private final ArrayList<CefBrowserOsr> browsers = new ArrayList<>();
    private final DisplayHandler displayHandler = new DisplayHandler();
    private final HashMap<String, String> mimeTypeMap = new HashMap<>();
    private final AppHandler appHandler = new AppHandler();

    @Override
    public void onInit(String root) {
        if (!OS.isWindows()) {
            VIRTUAL = true;
            Log.error("Unsupported Platform: %s", OS.getOSType());
            return;
        }

        appHandler.setArgs(MCEF.CEF_ARGS);

        ROOT = root;
        if (ROOT.endsWith("."))
            ROOT = ROOT.substring(0, ROOT.length() - 1);

        if (ROOT.endsWith("/"))
            ROOT = ROOT.substring(0, ROOT.length() - 1);

        String exeSuffix = ".exe";

        File subproc = new File(ROOT, "jcef_helper" + exeSuffix);
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        settings.background_color = new CefSettings.ColorType(0, 0, 0, 0);
        settings.locales_dir_path = (new File(ROOT, "MCEFLocales")).getAbsolutePath();
        settings.cache_path = (new File(Loader.YOLBI_DIR, "MCEFCache")).getAbsolutePath();
        settings.browser_subprocess_path = subproc.getAbsolutePath();
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DEFAULT;

        try {
            ArrayList<String> libs = new ArrayList<>();

            libs.add("d3dcompiler_47.dll");
            libs.add("libGLESv2.dll");
            libs.add("libEGL.dll");
            libs.add("chrome_elf.dll");
            libs.add("libcef.dll");
            libs.add("jcef.dll");

            for (String lib : libs) {
                File f = new File(ROOT, lib);
                try {
                    f = f.getCanonicalFile();
                } catch (IOException ex) {
                    f = f.getAbsoluteFile();
                }

                System.load(f.getPath());
            }

        } catch (Throwable t) {
            Log.error("Going in virtual mode; couldn't initialize CEF.");
            Log.exception(t);

            VIRTUAL = true;
            return;
        }

        CefApp.startup();
        cefApp = CefApp.getInstance(settings);
        MimeTypeLoader.loadMimeTypeMapping(mimeTypeMap);
        CefApp.addAppHandler(appHandler);
        cefClient = cefApp.createClient();
        Log.info(cefApp.getVersion().toString());
        cefRouter = CefMessageRouter.create(new CefMessageRouterConfig("mcefQuery", "mcefCancel"));
        cefClient.addMessageRouter(cefRouter);
        cefClient.addDisplayHandler(displayHandler);
        cefClient.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean doClose(CefBrowser browser) {
                browser.close(true);
                return false;
            }
        });
        Log.info("MCEF loaded successfuly.");
    }

    @Override
    public IBrowser createBrowser(String url, boolean transp) {
        if (VIRTUAL)
            return new VirtualBrowser();

        CefBrowserOsr ret = (CefBrowserOsr) cefClient.createBrowser(url, true, transp);
        ret.setCloseAllowed();
        ret.createImmediately();

        browsers.add(ret);
        return ret;
    }

    @Override
    public void registerDisplayHandler(IDisplayHandler idh) {
        displayHandler.addHandler(idh);
    }

    @Override
    public boolean isVirtual() {
        return VIRTUAL;
    }

    @Override
    public void registerJSQueryHandler(IJSQueryHandler iqh) {
        if (!VIRTUAL) {
            cefRouter.addHandler(new MessageRouter(iqh), false);
        }
    }

    @Override
    public void registerScheme(String name, Class<? extends IScheme> schemeClass, boolean std, boolean local, boolean displayIsolated, boolean secure, boolean corsEnabled, boolean cspBypassing, boolean fetchEnabled) {
        appHandler.registerScheme(name, schemeClass, std, local, displayIsolated, secure, corsEnabled, cspBypassing, fetchEnabled);
    }

    @Override
    public boolean isSchemeRegistered(String name) {
        return appHandler.isSchemeRegistered(name);
    }

    public void update() {
        if (cefApp != null)
            cefApp.N_DoMessageLoopWork();

        for (CefBrowserOsr b : browsers)
            b.mcefUpdate();

        displayHandler.update();
    }

    public void removeBrowser(CefBrowserOsr b) {
        browsers.remove(b);
    }

    @Override
    public IBrowser createBrowser(String url) {
        return createBrowser(url, false);
    }

    private void runMessageLoopFor(long ms) {
        final long start = System.currentTimeMillis();

        do {
            cefApp.N_DoMessageLoopWork();
        } while (System.currentTimeMillis() - start < ms);
    }

    @Override
    public void onShutdown() {
        if (VIRTUAL)
            return;

        Log.info("Shutting down JCEF...");
        CefBrowserOsr.CLEANUP = false; //Workaround

        for (CefBrowserOsr b : browsers)
            b.close();

        browsers.clear();

        if (MCEF.CHECK_VRAM_LEAK)
            CefRenderer.dumpVRAMLeak();

        runMessageLoopFor(100);
        CefApp.forceShutdownState();
        cefClient.dispose();

        if (MCEF.SHUTDOWN_JCEF)
            cefApp.N_Shutdown();
    }

    @Override
    public String mimeTypeFromExtension(String ext) {
        ext = ext.toLowerCase();
        String ret = mimeTypeMap.get(ext);
        if (ret != null)
            return ret;

        //If the mimeTypeMap couldn't be loaded, fall back to common things
        switch (ext) {
            case "htm":
            case "html":
                return "text/html";

            case "css":
                return "text/css";

            case "js":
                return "text/javascript";

            case "png":
                return "image/png";

            case "jpg":
            case "jpeg":
                return "image/jpeg";

            case "gif":
                return "image/gif";

            case "svg":
                return "image/svg+xml";

            case "xml":
                return "text/xml";

            case "txt":
                return "text/plain";

            default:
                return null;
        }
    }
}
