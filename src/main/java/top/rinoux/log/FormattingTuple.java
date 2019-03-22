package top.rinoux.log;

public class FormattingTuple {
    public static FormattingTuple NULL = new FormattingTuple((String) null);
    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public FormattingTuple(String message) {
        this(message, (Object[]) null, (Throwable) null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        if (throwable == null) {
            this.argArray = argArray;
        } else {
            this.argArray = trimmedCopy(argArray);
        }

    }

    public static Object[] trimmedCopy(Object[] argArray) {
        if (argArray != null && argArray.length != 0) {
            int trimmedLen = argArray.length - 1;
            Object[] trimmed = new Object[trimmedLen];
            System.arraycopy(argArray, 0, trimmed, 0, trimmedLen);
            return trimmed;
        } else {
            throw new IllegalStateException("non-sensical empty or null argument array");
        }
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}

