import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        // 启动命令监听
        CommandListener.start();

        // 加载配置
        HttpServerConfig config = new HttpServerConfig();

        // 创建并启动HttpServer
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getIpAddress(), config.getPort()), 0);
        server.createContext("/", new DirectoryHandler());
        server.setExecutor(config.getExecutor()); // 设置线程池
        server.start();

        // 打印日志
        Log2Console.info("服务器运行于: http://" + config.getIpAddress() + ":" + config.getPort());
    }
}
