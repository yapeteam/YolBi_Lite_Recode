package cn.yapeteam.yolbi.server.handlers.alts;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.ui.alt.account.Account;

import java.io.IOException;
import java.io.OutputStream;

public class HtmlAltAccountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // Create a JSON object to store the response
        JsonObject response = new JsonObject();

        Hermes.accountManager.get("alts").read();
        for (Account alt : Hermes.accountManager.getAccounts()) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("username", alt.getUsername());
            moduleJson.addProperty("accounttype", alt.getAccountType());
            response.add(alt.getUsername(), moduleJson);
        }
        // Send the JSON response with CORS headers
        sendJsonResponse(httpExchange, 200, response);

    }

    private void sendJsonResponse(HttpExchange httpExchange, int statusCode, JsonObject response) throws IOException {
        // Convert JSON to string
        String jsonResponse = response.toString();
        byte[] jsonResponseBytes = jsonResponse.getBytes();

        // Set CORS headers
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // Allow requests from any origin
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");

        // Send the response status and length
        httpExchange.sendResponseHeaders(statusCode, jsonResponseBytes.length);

        // Write the JSON response to the output stream
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(jsonResponseBytes);
        }
    }
}
