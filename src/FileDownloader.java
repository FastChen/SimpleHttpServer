import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FileDownloader {

    public static void downloadFile(HttpExchange exchange, File file) throws IOException {
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
        exchange.getResponseHeaders().add("Content-Disposition", "attachment;filename*=UTF-8''" + encodedFileName);
        exchange.sendResponseHeaders(200, file.length());

        Log2Console.info("[下载文件]: " + file.getPath());
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
