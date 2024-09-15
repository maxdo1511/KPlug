package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import java.util.Set;

public interface BeanDefinitionReader {
    Set<BeanDefinition> createBeanDefinition(@NonNull Class<?> clazz);
}
