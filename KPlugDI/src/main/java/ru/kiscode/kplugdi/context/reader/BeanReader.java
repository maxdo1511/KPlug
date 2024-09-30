package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.factory.BeanFactory;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

public interface BeanReader {

    Object createBean(@NonNull BeanDefinition beanDefinition, @NonNull BeanFactory beanFactory);
}
