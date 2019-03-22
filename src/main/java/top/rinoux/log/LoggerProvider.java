package top.rinoux.log;

/**
 * Created by rinoux on 2018-12-27.
 */
public interface LoggerProvider {


    void sql(String sql);


    boolean isDebugEnabled();


    void debug(String msg);


    void debug(String format, Object... args);


    boolean isInfoEnabled();


    void info(String msg);


    void info(String format, Object... args);


    void warn(String msg);


    void warn(String format, Object... args);


    void warn(String msg, Throwable e);


    void warn(Throwable e, String format, Object... args);


    void error(String msg);


    void error(String format, Object... args);


    void error(String msg, Throwable e);


    void error(Throwable e, String format, Object... args);


    void declareSQLEnd(String sql);

}

