import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log2Console
{
    private static final Logger logger = Logger.getLogger(Log2Console.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static void log(Level level, String mesg){
        logger.log(level, format(level,new Date(), mesg));
    }

    private static String format(Level level, Date date, String mesg){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(DATE_FORMAT.format(new Date())).append("]").append(" ").append("[").append(level).append("]").append(" ").append(mesg);
        return sb.toString();
    }

    // 严重信息
    public static void severe(String msg) {
        log(Level.SEVERE, msg);
    }

    // 警告信息
    public static void warning(String msg) {
        log(Level.WARNING, msg);
    }

    // info信息
    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    // 设定配置信息
    public static void config(String msg) {
        log(Level.CONFIG, msg);
    }

    // 级别小信息
    public static void fine(String msg) {
        log(Level.FINE, msg);
    }

    // 级别更小信息
    public static void finer(String msg) {
        log(Level.FINE, msg);
    }

    // 级别最小信息
    public static void finest(String msg) {
        log(Level.FINE, msg);
    }
}
