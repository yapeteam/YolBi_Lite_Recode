package cn.yapeteam.yolbi.server;


import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.server.handlers.modules.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {

    private static HttpServer server;
    public static final int PORT = 1342;

    public static void start() throws IOException {

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", new HtmlHttpHandler());
        server.createContext("/api/modulesList", new ModulesHttpHandler());
//        server.createContext("/api/setStatus", new StatusHttpHandler());
        server.createContext("/api/updateModulesInfo", new ModuleInfoHttpHandler());
//        server.createContext("/api/categoriesList", new CategoriesHttpHandler());
        server.createContext("/api/getModuleSetting", new ModuleSettingsHttpHandler());
        server.createContext("/api/setModuleSettingValue", new SetModuleSettingsHttpHandler());
        //server.createContext("/api/getAltAccounts", new HtmlAltAccountHandler());
        //server.createContext("/api/AltLogin", new HtmlAltLoginHandler());
        //server.createContext("/api/DeleteAlt", new HtmlAltDeleteHandler());
        //server.createContext("/api/AddAlt", new HtmlAddAltHandler());
//        server.createContext("/api/setBind", new BindHttpHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        Logger.info("Server started on port {}", PORT);
    }

    public static void stop() {
        server.stop(0);
    }
}