import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerConfig {

    private String serverDirectory;
    private String ipAddress;
    private int port;
    private ExecutorService executor;

    public HttpServerConfig() {
        // 加载配置文件
        Properties config = new Properties();
        try (FileReader reader = new FileReader("server.properties")) {
            config.load(reader);
        } catch (IOException e) {
            Log2Console.warning("无法加载配置文件，使用默认设置。");
        }

        // 读取配置项
        this.serverDirectory = config.getProperty("server-dir", "./");
        this.port = Integer.parseInt(config.getProperty("server-port", "8080"));
        this.ipAddress = config.getProperty("server-ip", "0.0.0.0");
        this.executor = Executors.newCachedThreadPool();
    }

    public String getServerDirectory() {
        return serverDirectory;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
