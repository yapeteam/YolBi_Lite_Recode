package cn.yapeteam.yolbi.server.handlers.modules;

import cn.yapeteam.yolbi.utils.file.FileUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HtmlHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestedPath = httpExchange.getRequestURI().getPath();
        System.out.println("Requested Path: " + requestedPath); // Debugging log

        // Assuming "/assets/hermes/ui" is at the root of your classpath
        String basePath = "web";
        String resourcePath = basePath + (requestedPath.equals("/") ? "/index.html" : requestedPath);

        // Determine content type based on the file extension
        String contentType = getContentType(requestedPath);
        System.out.println("Content Type: " + contentType); // Debugging log

        if (isBinaryContent(requestedPath)) {
            // Handle binary content (images)
            byte[] responseContent = FileUtils.readBinaryPath(resourcePath);
            if (responseContent == null) {
                sendNotFound(httpExchange);
                return;
            }

            httpExchange.getResponseHeaders().set("Content-Type", contentType);
            httpExchange.sendResponseHeaders(200, responseContent.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(responseContent);
            }
        } else {
            // Handle text content (HTML, CSS, JS)
            String responseContent = FileUtils.readPath(resourcePath);
            if (responseContent == null) {
                sendNotFound(httpExchange);
                return;
            }

            byte[] responseContentByte = responseContent.getBytes(StandardCharsets.UTF_8);
            httpExchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
            httpExchange.sendResponseHeaders(200, responseContentByte.length);
            try (OutputStream out = httpExchange.getResponseBody()) {
                out.write(responseContentByte);
            }
        }
    }

    private void sendNotFound(HttpExchange httpExchange) throws IOException {
        String response = "404 Not Found";
        System.out.println("Sending 404 Not Found"); // Debugging log
        httpExchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private boolean isBinaryContent(String path) {
        return path.matches(".*\\.(png|jpg|jpeg|gif)$");
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        }
        // Default content type for HTML or any other text-based content
        return "text/html; charset=utf-8";
    }
}
