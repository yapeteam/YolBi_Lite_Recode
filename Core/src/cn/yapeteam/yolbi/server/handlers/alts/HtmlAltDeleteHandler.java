package cn.yapeteam.yolbi.server.handlers.alts;


import cn.yapeteam.yolbi.utils.web.URLUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HtmlAltDeleteHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String altname = URLUtil.getValues(httpExchange)[0];

        JsonObject jsonObject = new JsonObject();
        boolean isFound = false;
        for(Account alt : Hermes.accountManager.getAccounts()){
            if(alt.getUsername().equalsIgnoreCase(altname)){
                AuthThread loginThread;
                isFound = true;
                Hermes.accountManager.getAccounts().remove(alt);
                Hermes.accountManager.get("alts").write();
            }
        }

        jsonObject.addProperty("success", isFound);

        if (!isFound) jsonObject.addProperty("reason", "Can't find alt");

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
