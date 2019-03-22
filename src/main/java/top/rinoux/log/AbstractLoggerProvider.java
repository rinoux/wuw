package top.rinoux.log;

/**
 * Created by rinoux on 2018-12-27.
 */
public abstract class AbstractLoggerProvider implements LoggerProvider {

    @Override
    public void sql(String sql) {
        System.out.println("[SQL]" + sql);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        System.out.println("[DEBUG]" + msg);
    }

    @Override
    public void debug(String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.out.println("[DEBUG]" + tuple.getMessage());
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
        System.out.println("[INFO]" + msg);
    }

    @Override
    public void info(String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.out.println("[INFO]" + tuple.getMessage());
    }

    @Override
    public void warn(String msg) {
        System.err.println("[WARN]" + msg);
    }

    @Override
    public void warn(String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.err.println("[WARN]" + tuple.getMessage());
    }

    @Override
    public void warn(String msg, Throwable e) {
        System.err.println("[WARN]" + msg);
        e.printStackTrace();
    }

    @Override
    public void warn(Throwable e, String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.err.println("[WARN]" + tuple.getMessage());
        e.printStackTrace();
    }

    @Override
    public void error(String msg) {
        System.err.println("[ERROR]" + msg);
    }

    @Override
    public void error(String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.err.println("[ERROR]" + tuple.getMessage());
    }

    @Override
    public void error(String msg, Throwable e) {
        System.err.println("[ERROR]" + msg);
        e.printStackTrace();
    }

    @Override
    public void error(Throwable e, String format, Object... args) {
        MessageFormatter.FormattingTuple tuple = MessageFormatter.arrayFormat(format, args);
        System.err.println("[ERROR]" + tuple.getMessage());
        e.printStackTrace();
    }

    @Override
    public void declareSQLEnd(String sql) {

    }
}

