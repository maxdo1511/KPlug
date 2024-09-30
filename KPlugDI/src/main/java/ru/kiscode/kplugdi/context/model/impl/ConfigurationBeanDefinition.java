package ru.kiscode.kplugdi.context.model.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.kiscode.kplugdi.context.model.BeanDefinition;

import java.lang.reflect.Method;

@Getter
@Setter
@SuperBuilder
public class ConfigurationBeanDefinition extends BeanDefinition {
    private Method configurationMethod;
}
