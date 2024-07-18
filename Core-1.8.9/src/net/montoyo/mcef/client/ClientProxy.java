package net.montoyo.mcef.client;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventKey;
import cn.yapeteam.yolbi.event.impl.game.EventLoadWorld;
import cn.yapeteam.yolbi.event.impl.game.EventLoop;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.montoyo.mcef.BaseProxy;
import net.montoyo.mcef.MCEF;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.IDisplayHandler;
import net.montoyo.mcef.api.IJSQueryHandler;
import net.montoyo.mcef.api.IScheme;
import net.montoyo.mcef.example.ExampleMod;
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
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
    private final String updateStr = "666";
    private final Minecraft mc = Minecraft.getMinecraft();
    private final DisplayHandler displayHandler = new DisplayHandler();
    private final HashMap<String, String> mimeTypeMap = new HashMap<>();
    private final AppHandler appHandler = new AppHandler();
    private final ExampleMod exampleMod = new ExampleMod();

    @Override
    public void onPreInit() {
        exampleMod.onPreInit(); //Do it even if example mod is disabled because it registers the "mod://" scheme
    }

    @Listener
    private void onKey(EventKey e) {
        if (e.getKey() == Keyboard.KEY_L)
            exampleMod.display();
    }

    @Override
    public void onInit() {
        appHandler.setArgs(MCEF.CEF_ARGS);

        ROOT = mc.mcDataDir.getAbsolutePath().replaceAll("\\\\", "/");
        if (ROOT.endsWith("."))
            ROOT = ROOT.substring(0, ROOT.length() - 1);

        if (ROOT.endsWith("/"))
            ROOT = ROOT.substring(0, ROOT.length() - 1);

        Log.info("Now adding \"%s\" to java.library.path", ROOT);

        try {
            Field pathsField = ClassLoader.class.getDeclaredField("usr_paths");
            pathsField.setAccessible(true);

            String[] paths = (String[]) pathsField.get(null);
            String[] newList = new String[paths.length + 1];

            System.arraycopy(paths, 0, newList, 1, paths.length);
            newList[0] = ROOT.replace('/', File.separatorChar);
            pathsField.set(null, newList);
        } catch (Exception e) {
            Log.error("Failed to do it! Entering virtual mode...");
            e.printStackTrace();

            VIRTUAL = true;
            return;
        }

        Log.info("Done without errors.");
        String exeSuffix;
        if (OS.isWindows())
            exeSuffix = ".exe";
        else
            exeSuffix = "";

        File subproc = new File(ROOT, "jcef_helper" + exeSuffix);
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = true;
        settings.background_color = settings.new ColorType(0, 255, 255, 255);
        settings.locales_dir_path = (new File(ROOT, "MCEFLocales")).getAbsolutePath();
        settings.cache_path = (new File(ROOT, "MCEFCache")).getAbsolutePath();
        settings.browser_subprocess_path = subproc.getAbsolutePath();
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_DEFAULT;

        try {
            ArrayList<String> libs = new ArrayList<>();

            if (OS.isWindows()) {
                libs.add("d3dcompiler_47.dll");
                libs.add("libGLESv2.dll");
                libs.add("libEGL.dll");
                libs.add("chrome_elf.dll");
                libs.add("libcef.dll");
                libs.add("jcef.dll");
            }

            for (String lib : libs) {
                File f = new File(ROOT, lib);
                try {
                    f = f.getCanonicalFile();
                } catch (IOException ex) {
                    f = f.getAbsoluteFile();
                }

                System.load(f.getPath());
            }

            CefApp.startup();
            cefApp = CefApp.getInstance(settings);
            //cefApp.myLoc = ROOT.replace('/', File.separatorChar);

            MimeTypeLoader.loadMimeTypeMapping(mimeTypeMap);
            CefApp.addAppHandler(appHandler);
            cefClient = cefApp.createClient();
        } catch (Throwable t) {
            Log.error("Going in virtual mode; couldn't initialize CEF.");
            t.printStackTrace();

            VIRTUAL = true;
            return;
        }

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

        YolBi.instance.getEventManager().register(this);
        if (MCEF.ENABLE_EXAMPLE)
            exampleMod.onInit();

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
    public void openExampleBrowser(String url) {
        if (MCEF.ENABLE_EXAMPLE)
            exampleMod.showScreen(url);
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

    @Listener
    public void onTick(EventLoop e) {
        mc.mcProfiler.startSection("MCEF");

        if (cefApp != null)
            cefApp.N_DoMessageLoopWork();

        for (CefBrowserOsr b : browsers)
            b.mcefUpdate();

        displayHandler.update();
        mc.mcProfiler.endSection();
    }

    @Listener
    public void onLogin(EventLoadWorld e) {
        if (!MCEF.WARN_UPDATES)
            return;

        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(updateStr));
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
