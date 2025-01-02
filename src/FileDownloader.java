import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileDownloader {

    public static void serveFile(HttpExchange exchange, File file) throws IOException {
        // 确定内容类型
        String contentType = getContentType(file);
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        // 设置响应头部
        exchange.getResponseHeaders().set("Content-Type", contentType);

        // 如果是可查看的文件类型（例如HTML、TXT、MD、PNG），则在浏览器中直接显示
        // 否则，作为附件下载
        if (isViewable(contentType)) {
            Log2Console.info("[打开文件]: " + file.getPath());
            exchange.getResponseHeaders().set("Content-Disposition", "inline;filename*=UTF-8''" + encodedFileName);
        } else {
            Log2Console.info("[下载文件]: " + file.getPath());
            exchange.getResponseHeaders().set("Content-Disposition", "attachment;filename*=UTF-8''" + encodedFileName);
        }

        // 发送响应头部
        exchange.sendResponseHeaders(200, file.length());

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    // 根据文件扩展名确定MIME类型
    private static String getContentType(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (fileName.endsWith(".txt")) return "text/plain; charset=UTF-8"; // 指定UTF-8字符集
        if (fileName.endsWith(".md")) return "text/markdown; charset=UTF-8";
        if (fileName.endsWith(".png")) return "image/png"; // 图像不需要字符集
        // 添加其他文件类型的MIME类型...
        return "application/octet-stream"; // 默认为二进制流
    }

    // 判断文件类型是否可以直接在浏览器中查看
    private static boolean isViewable(String contentType) {
        // 定义可直接查看的MIME类型
        Set<String> viewableTypes = new HashSet<>(Arrays.asList(
                "text/html; charset=UTF-8", "text/plain; charset=UTF-8", "text/markdown; charset=UTF-8", "image/png"
                // 添加其他可查看的MIME类型...
        ));
        return viewableTypes.contains(contentType);
    }
}
