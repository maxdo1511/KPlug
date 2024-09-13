package ru.kiscode.kplugdi.exception;

import lombok.NonNull;

public class BeanCreatingException extends RuntimeException {

    public BeanCreatingException(@NonNull String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }

    public BeanCreatingException(@NonNull String errorMessage, Throwable e, Object... args) {
        super(String.format(errorMessage, args), e);
    }
}
