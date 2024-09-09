package ru.kiscode.kplugdi.exception;

import lombok.NonNull;

/**
 * Exception thrown when there is an issue creating a bean.
 * <p>
 * This exception is used to indicate errors that occur during the creation of beans
 * within the dependency injection plugin.
 * </p>
 */
public class BeanCreatingException extends RuntimeException {

    /**
     * Constructs a new {@code BeanCreatingException} with the specified detail message.
     *
     * @param errorMessage the detail message
     * @param args optional arguments to format the error message
     */
    public BeanCreatingException(@NonNull String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }

    /**
     * Constructs a new {@code BeanCreatingException} with the specified detail message
     * and cause.
     *
     * @param errorMessage the detail message
     * @param e the cause of the exception
     * @param args optional arguments to format the error message
     */
    public BeanCreatingException(@NonNull String errorMessage, Throwable e, Object... args) {
        super(String.format(errorMessage, args), e);
    }
}
