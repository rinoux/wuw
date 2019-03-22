package top.rinoux.log;

/**
 * Created by rinoux on 2018-12-27.
 */
public class LoggerFactory {

    static final LoggerProvider SOUT = new AbstractLoggerProvider() {

    };

    private static LoggerProvider provider = SOUT;

    public static LoggerProvider getLogger() {
        return provider;
    }

}
