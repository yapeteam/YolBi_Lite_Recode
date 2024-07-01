package cn.yapeteam.yolbi.server.handlers.modules;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.utils.web.URLUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModuleInfoHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String Displayname = URLUtil.getValues(httpExchange)[0];
        boolean Enabled = Boolean.parseBoolean(URLUtil.getValues(httpExchange)[1]);


        JsonObject jsonObject = new JsonObject();
        JsonObject result = new JsonObject();

        AtomicBoolean moduleFound = new AtomicBoolean(false);

        YolBi.instance.getModuleManager().getModules().stream().filter(module -> module.getName().equalsIgnoreCase(Displayname)).forEach(Module -> {
            Module.setEnabled(Enabled);
            moduleFound.set(true);
        });

        if (!moduleFound.get()) {
            result.addProperty("message", "No module found");
            jsonObject.add("result", result);
            jsonObject.addProperty("success", false);
        } else {
            jsonObject.add("result", result);
            jsonObject.addProperty("success", true);
        }

        byte[] response = jsonObject.toString().getBytes(StandardCharsets.UTF_8);

        // Set CORS headers
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // Allow requests from any origin
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

        httpExchange.sendResponseHeaders(200, response.length);

        OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.flush();
        out.close();
    }
}
