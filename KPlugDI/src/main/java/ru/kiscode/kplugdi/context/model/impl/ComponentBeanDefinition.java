package ru.kiscode.kplugdi.context.model.impl;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

@Getter
@Setter
@SuperBuilder
public class ComponentBeanDefinition extends BeanDefinition {
    private Class<?> superClass;

}

