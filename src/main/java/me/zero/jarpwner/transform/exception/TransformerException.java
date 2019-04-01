package me.zero.jarpwner.transform.exception;

/**
 * @author Brady
 * @since 4/1/2019
 */
public class TransformerException extends RuntimeException {

    public TransformerException() {}

    public TransformerException(String message) {
        super(message);
    }

    public TransformerException(String message, Object... args) {
        super(String.format(message, args));
    }
}
