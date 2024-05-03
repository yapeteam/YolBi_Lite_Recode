package cn.yapeteam.yolbi.server.handlers.alts;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.ui.alt.account.Account;
import dev.hermes.ui.alt.account.MicrosoftLogin;
import dev.hermes.ui.alt.impl.AuthThread;
import dev.hermes.utils.url.URLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HtmlAltLoginHandler implements HttpHandler {

    Minecraft mc = Minecraft.getMinecraft();

    private MicrosoftLogin.LoginData loginWithRefreshToken(String refreshToken) {
        final MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        mc.session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft");
        return loginData;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String altname = URLUtil.getValues(httpExchange)[0];

        System.out.println("Logging in with " + altname);

        Hermes.accountManager.get("alts").read();
        JsonObject jsonObject = new JsonObject();
        boolean isFound = false;
        for (Account alt : Hermes.accountManager.getAccounts()) {
            if (alt.getUsername().equalsIgnoreCase(altname)) {
                AuthThread loginThread;
                isFound = true;
                if (alt.getAccountType().equalsIgnoreCase("microsoft")) {
                    loginThread = new AuthThread(alt.getUsername(), "", alt.getRefreshToken(), "MICROSOFT");
                    loginThread.start();
                } else {
                    loginThread = new AuthThread(alt.getUsername(), "", "", "CRACKED");
                    loginThread.start();
                }
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
