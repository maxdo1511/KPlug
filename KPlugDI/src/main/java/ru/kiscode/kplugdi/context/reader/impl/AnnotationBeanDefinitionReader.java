package ru.kiscode.kplugdi.context.reader.impl;

import lombok.NonNull;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;


public class AnnotationBeanDefinitionReader implements BeanDefinitionReader {
    @Override
    public BeanDefinition createBeanDefinition(@NonNull Class<?> clazz) {
        return null;
    }
}
