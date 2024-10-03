package ru.kiscode.kplugdi.context.model.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

@Getter
@Setter
@SuperBuilder
public class ApplicationContextBeanDefinition extends BeanDefinition {
    private ApplicationContext applicationContext;
}
