package top.rinoux.exception;

/**
 * Created by rinoux on 2019-06-24.
 */
public class AccountErrorException extends Exception {
    public AccountErrorException() {
    }

    public AccountErrorException(String message) {
        super(message);
    }
}
