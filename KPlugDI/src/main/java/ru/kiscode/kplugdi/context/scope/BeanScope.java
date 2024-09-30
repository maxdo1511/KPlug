package ru.kiscode.kplugdi.context.scope;

import lombok.NonNull;

public interface BeanScope {

    Object get(@NonNull Object beanObject);
}
