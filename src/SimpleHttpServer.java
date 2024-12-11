import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class SimpleHttpServer {

    private static volatile boolean running = true;
    private static volatile String serverDirectory;
    public static void main(String[] args) throws IOException {
        
        Thread stopListener = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (running) { 
                String input = sc.nextLine();
                if ("stop".equalsIgnoreCase(input.trim())) {
                    running = false;
                    sc.close();
                    System.out.println("Shutdown.");
                    System.exit(0);
                }
            }
        });
        // 设置为守护线程，确保主程序退出该线程也退出
        stopListener.setDaemon(true);
        stopListener.start();

        // 读取配置文件
        Properties config = new Properties();
        try (FileReader reader = new FileReader("server.properties")) {
            config.load(reader);
        } catch (IOException e) {
            System.err.println("无法加载配置文件，使用默认设置。");
        }

        // 从配置文件获取端口号和 IP 地址
        serverDirectory = config.getProperty("server-dir", "./"); // 默认目录
        int port = Integer.parseInt(config.getProperty("server-port", "8080")); // 默认端口 8080
        String ipAddress = config.getProperty("server-ip", "0.0.0.0"); // 默认绑定到所有地址

        HttpServer server = HttpServer.create(new InetSocketAddress(ipAddress, port), 0);

        // 设置处理器
        server.createContext("/", new FileDownloadHandler());

        server.setExecutor(null); // 使用默认执行器
        System.out.println("服务器运行于: http://" + ipAddress + ":" + port);
        server.start();

        System.out.println("使用 stop 命令可停止运行服务器.");
    }

    // 文件下载和目录浏览处理器
    static class FileDownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            System.out.println("请求URL: " + path);

            if (path == null || path.equals("/")) {
                path = "/";
            }

            File directory = new File(serverDirectory + path);

            if (!directory.exists()) {
                // 如果路径不存在，返回 404
                String response = "File or directory not found";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            if (directory.isDirectory()) {
                // 如果是目录，返回文件和子目录列表
                File[] files = directory.listFiles();
                Arrays.sort(files);
                StringBuilder responseBuilder = new StringBuilder("<html><body><h1>Directory Listing</h1><ul>");
                if (!path.equals("/")) {
                    responseBuilder.append("<li><a href=\"../\">.. (parent directory)</a></li>");
                }
                for (File file : files) {
                    String filePath = path + (path.endsWith("/") ? "" : "/") + file.getName();
                    if (file.isDirectory()) {
                        responseBuilder.append("<li><a href=\"").append(filePath).append("/\">[DIR] ").append(file.getName()).append("</a></li>");
                    } else {
                        responseBuilder.append("<li><a href=\"").append(filePath).append("\">").append(file.getName()).append("</a></li>");
                    }
                }
                responseBuilder.append("</ul></body></html>");

                String response = responseBuilder.toString();
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // 如果是文件，提供下载
            if (directory.isFile()) {
                // 设置响应头
                exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
                exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=\"" + directory.getName() + "\"");

                // 发送文件内容
                exchange.sendResponseHeaders(200, directory.length());
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(directory));
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }
}