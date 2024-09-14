package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;


public interface BeanDefinitionReader {

    BeanDefinition createBeanDefinition(@NonNull Class<?> clazz);
}
