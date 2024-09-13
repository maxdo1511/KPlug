package ru.kiscode.kplugdi.context.reader;

import lombok.NonNull;
import ru.kiscode.kplugdi.test.bean.BeanDefinition;

public interface BeanDefinitionReader {

    BeanDefinition createBeanDefinition(@NonNull Class<?> classes);
}
