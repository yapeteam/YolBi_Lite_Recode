package cn.yapeteam.yolbi.server.handlers.alts;

import cn.yapeteam.yolbi.utils.web.URLUtil;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.hermes.Hermes;
import dev.hermes.ui.alt.account.Account;
import dev.hermes.ui.alt.account.MicrosoftLogin;
import dev.hermes.utils.SkinUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class HtmlAddAltHandler implements HttpHandler {

    private MicrosoftLogin.LoginData loginWithRefreshToken(String refreshToken) {
        final MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        Minecraft.getMinecraft().session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft");
        return loginData;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String type = URLUtil.getValues(httpExchange)[0];

        String username = URLUtil.getValues(httpExchange)[1];

        JsonObject jsonObject = new JsonObject();

        System.out.println("Type: " + type);

        final CountDownLatch latch = new CountDownLatch(1);

        if (type.equals("microsoft")) {
            System.out.println("Microsoft");
            MicrosoftLogin.getRefreshToken(refreshToken -> {
                if (refreshToken != null) {
                    new Thread(() -> {
                        // logging in
                        MicrosoftLogin.LoginData loginData = loginWithRefreshToken(refreshToken);
                        Account account = new Account(loginData.username, SkinUtil.uuidOf(loginData.username), loginData.newRefreshToken);
                        if (!Hermes.accountManager.getAccounts().contains(account)) {
                            Hermes.accountManager.getAccounts().add(account);
                            // writes the file
                            Hermes.accountManager.get("alts").write();
                            jsonObject.addProperty("success", true);
                        } else {
                            jsonObject.addProperty("success", false);
                            jsonObject.addProperty("error", "Alt already exists");
                        }
                        latch.countDown(); // Signal that the operation is complete
                    }).start();
                }
            });
        } else {
            if (!Hermes.accountManager.getAccounts().contains(username)) {
                Hermes.accountManager.getAccounts().add(new Account(username));
                // writes the file
                Hermes.accountManager.get("alts").write();
                jsonObject.addProperty("success", true);
            } else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("error", "Alt already exists");
            }
            latch.countDown(); // Signal that the operation is complete
        }

        try {
            latch.await(); // Wait for the operation to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
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
