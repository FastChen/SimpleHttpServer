import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DirectoryHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        HttpServerConfig config = new HttpServerConfig();

        File directory = new File(config.getServerDirectory() + path);

        Log2Console.info("[访问路径]: "+ path + " | " + directory.getPath());

        try {
            if (!directory.exists()) {
                sendResponse(exchange, 404, "404. File or directory not found");
                return;
            }

            if (directory.isDirectory()) {
                String response = generateDirectoryTree(exchange.getRequestURI().getRawPath(), directory);
                sendResponse(exchange, 200, response);
            } else if (directory.isFile()) {
                FileDownloader.serveFile(exchange, directory);
            }
        } catch (Exception e) {
            Log2Console.warning(e.getMessage());
            sendResponse(exchange, 500, "500 Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private String generateDirectoryTree(String rawUrlPath, File directory) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><h1>📦 Index of ").append(URLDecoder.decode(rawUrlPath, StandardCharsets.UTF_8)).append("</h1><ul>");

        if (!rawUrlPath.equals("/")) {
            String parentPath = rawUrlPath.substring(0, rawUrlPath.lastIndexOf('/'));
            if (parentPath.isEmpty()) {
                parentPath = "/";
            }
            sb.append("<li><a href=\"").append(parentPath).append("\">\uD83D\uDD19 .. (返回上级目录)</a></li>");
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean isDir = file.isDirectory();
                String fileName = file.getName();
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                String encodeURLPath = rawUrlPath + (rawUrlPath.endsWith("/") ? "" : "/") + encodedFileName.replace("+", "%20");
                sb.append("<li><a href=\"").append(encodeURLPath).append("\">").append((isDir ? "📂" : "")).append(fileName).append("</a></li>");
            }
        }
        sb.append("</ul></body></html>");
        return sb.toString();
    }
}
