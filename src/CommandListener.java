import java.util.Scanner;

public class CommandListener {

    public static void start() {
        Thread commandListener = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String input = sc.nextLine();
                if ("ping".equalsIgnoreCase(input.trim())) {
                    Log2Console.info("Pong");
                }
                if ("stop".equalsIgnoreCase(input.trim())) {
                    sc.close();
                    Log2Console.info("服务器关闭: Shutdown.");
                    System.exit(0);
                }
            }
        });
        commandListener.setDaemon(true);
        commandListener.start();
    }
}
