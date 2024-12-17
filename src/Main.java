import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    // 服务器路径
    private static volatile String serverDirectory;

    public static void main(String[] args) throws IOException {
        // 命令监听
        Thread commanListener = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String input = sc.nextLine();
                if("ping".equalsIgnoreCase(input.trim())){
                    Log2Console.info("Pong");
                }
                if ("stop".equalsIgnoreCase(input.trim())) {
                    sc.close();
                    Log2Console.info("服务器关闭: Shutdown.");
                    System.exit(0);
                }
            }
        });
        // 设置为守护线程，确保主程序退出该线程也退出
        commanListener.setDaemon(true);
        commanListener.start();

        // 读取配置文件
        Properties config = new Properties();
        try (FileReader reader = new FileReader("server.properties")) {
            config.load(reader);
        } catch (IOException e) {
            // System.err.println("无法加载配置文件，使用默认设置。");
            Log2Console.warning("无法加载配置文件，使用默认设置。");
        }

        // 从配置文件获取端口号和 IP 地址
        serverDirectory = config.getProperty("server-dir", "./"); // 默认目录
        int port = Integer.parseInt(config.getProperty("server-port", "8080")); // 默认端口 8080
        String ipAddress = config.getProperty("server-ip", "0.0.0.0"); // 默认绑定到所有地址

        // 创建 Http 服务
        HttpServer server = HttpServer.create(new InetSocketAddress(ipAddress, port), 0);
        server.createContext("/", new SimpleHttpHandler());
        server.setExecutor(null);
        server.start();

        Log2Console.info("服务器运行于: http://" + ipAddress + ":" + port);
    }

    static class SimpleHttpHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String path = exchange.getRequestURI().getPath();

                        Log2Console.info("收到请求地址: " + path);

                        // 传递请求地址，设置工作目录
                        File directory = new File(serverDirectory + path);

                        if (!directory.exists()) {
                            // 如果路径不存在，返回 404
                            String response = "404. File or directory not found";
                            exchange.sendResponseHeaders(404, response.length());
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                            return;
                        }

                        if(directory.isDirectory()){
                            String response = GenerateTreeDirectory(path, directory);
                            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                            exchange.sendResponseHeaders(200, 0);
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes());
                            os.close();

                        } else if (directory.isFile()) {
                            String encodedFileName = URLEncoder.encode(directory.getName(), "UTF-8").replaceAll("\\+", "%20");
                            exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
                            exchange.getResponseHeaders().add("Content-Disposition", "attachment;filename*=UTF-8''" +  encodedFileName);
                            exchange.sendResponseHeaders(200, directory.length());

                            Log2Console.info("访问路径: " + path +" | 下载文件: " + encodedFileName);

                            OutputStream os = exchange.getResponseBody();
                            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(directory))) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = bis.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                            }
                            finally {
                                os.flush();
                                os.close();
                            }

                        }

                    }catch (IOException ie) {
                        Log2Console.warning(ie.getMessage());
                        ie.printStackTrace();
                    } catch (Exception e) {
                        Log2Console.warning(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // 遍历当前文件夹
    static String GenerateTreeDirectory(String urlPath, File directory) {
        Log2Console.info("访问路径: " + urlPath + " | 文件路径: " + directory.getPath());
        StringBuilder sb = new StringBuilder();

        if (directory.isDirectory()) {
            // Header
            sb.append("<html><body><h1>📦 Index of " + urlPath + "</h1><ul>");

             if (!urlPath.equals("/")) {
                 // 添加返回上级目录链接
                 String parentPath = urlPath.substring(0, urlPath.lastIndexOf('/'));
                 if (parentPath.isEmpty()) {
                     parentPath = "/";
                 }
                 sb.append("<li><a href=\"").append(parentPath).append("\">\uD83D\uDD19 .. (parent directory)</a></li>");
             }

            File[] files = directory.listFiles();
            for (File file : files) {
                Boolean isDir = file.isDirectory();
                String fileName = (isDir == true ? "📂" : "") + file.getName();
                String filePath = urlPath + (urlPath.endsWith("/") ? "" : "/") + file.getName();
                sb.append("<li><a href=\"" + filePath  + "\">" + fileName + "</a></li>");
            }
            // Footer
            sb.append("</ul></body></html>");
        }

        return sb.toString();
    }
}
