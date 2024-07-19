/**
 * @author wzhy233
 * @time 2024-07-19  11:53
 */

package cn.yapeteam.yolbi.server;

import cn.yapeteam.loader.logger.Logger;
import cn.yapeteam.yolbi.server.handlers.modules.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.Random;

public class WebServer {

    private static HttpServer server;
    private static final int DEFAULT_PORT = 23333;
    private static final int MAX_PORT = 99999;

    public static void start() throws IOException {
        int port = findAvailablePort(DEFAULT_PORT);

        server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/", new HtmlHttpHandler());
        server.createContext("/api/modulesList", new ModulesHttpHandler());
        //server.createContext("/api/setStatus", new StatusHttpHandler());
        server.createContext("/api/updateModulesInfo", new ModuleInfoHttpHandler());
        //server.createContext("/api/categoriesList", new CategoriesHttpHandler());
        server.createContext("/api/getModuleSetting", new ModuleSettingsHttpHandler());
        server.createContext("/api/setModuleSettingValue", new SetModuleSettingsHttpHandler());
        //server.createContext("/api/getAltAccounts", new HtmlAltAccountHandler());
        //server.createContext("/api/AltLogin", new HtmlAltLoginHandler());
        //server.createContext("/api/DeleteAlt", new HtmlAltDeleteHandler());
        //server.createContext("/api/AddAlt", new HtmlAddAltHandler());
        //server.createContext("/api/setBind", new BindHttpHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));

        server.start();
        Logger.info("Server started on port {}", port);
    }

    public static void stop() {
        server.stop(0);
    }

    private static int findAvailablePort(int defaultPort) {
        int port = defaultPort;
        Random random = new Random();

        while (!isPortAvailable(port)) {

            port = random.nextInt((MAX_PORT - DEFAULT_PORT) + 1) + DEFAULT_PORT;
            Logger.info("Port not available!");
            Logger.info("Found available port {}", port);
        }

        return port;
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
