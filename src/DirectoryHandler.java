import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

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
        // TODO: 继续优化结构和生成排序。

        StringBuilder sb = new StringBuilder();

        String pathName = URLDecoder.decode(rawUrlPath, StandardCharsets.UTF_8);

        sb.append("<html><head><title>Index of ").append(pathName).append("</title></head>");
        sb.append("<body>");
        sb.append("<h1>Index of ").append(pathName).append("</h1>");

        sb.append("<table><tr><th valign=\"top\">[ICO]</th><th>文件名</th><th>最后修改时间</th><th>文件大小</th></tr>");
        sb.append("<tr><th colspan=\"4\"><hr></th></tr>");

        if (!rawUrlPath.equals("/")) {
            String parentPath = rawUrlPath.substring(0, rawUrlPath.lastIndexOf('/'));
            if (parentPath.isEmpty()) {
                parentPath = "/";
            }
            sb.append("<tr>");
            sb.append(" <td valign=\"top\">[PARENTDIR]</td>");
            sb.append(" <td><a href=\"").append(parentPath).append("\">返回上级目录</a></td>");
            sb.append(" <td>&nbsp;</td>");
            sb.append(" <td align=\"right\"> - </td>");
            sb.append("</tr>");
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                boolean isDir = file.isDirectory();
                String fileType = isDir ? "DIR" : "FILE";
                
                String fileName = file.getName();
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                String encodeURLPath = rawUrlPath + (rawUrlPath.endsWith("/") ? "" : "/") + encodedFileName.replace("+", "%20");

                sb.append("<tr>");
                sb.append(" <td valign=\"top\">[").append(fileType).append("]</td>");
                sb.append(" <td><a href=\"").append(encodeURLPath).append("\">").append(fileName).append("</a></td>");
                sb.append(" <td align=\"right\">").append(df.format(file.lastModified())).append("</td>");
                sb.append(" <td align=\"right\">").append(file.length()).append(" B</td>");
                sb.append("</tr>");

            }
        }

        sb.append("<tr><th colspan=\"4\"><hr></th></tr></table></body></html>");
        return sb.toString();
    }
}
