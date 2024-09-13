package ru.kiscode.kplugdi.context.factory;

import lombok.Getter;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.context.reader.AnnotationBeanDefinitionReader;
import ru.kiscode.kplugdi.test.bean.BeanDefinition;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class BeanDefinitionFactory {
    private final List<BeanDefinitionReader> beanDefinitionReaders;
    private final List<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    private final List<BeanPostProcessor> beanPostProcessors;
    private final Set<BeanDefinition> beanDefinitions;

    public BeanDefinitionFactory(){
        beanDefinitionReaders = new ArrayList<>();
        beanDefinitions = new HashSet<>();
        beanDefinitionPostProcessors = new ArrayList<>();
        beanPostProcessors = new ArrayList<>();

        beanDefinitionReaders.add(new AnnotationBeanDefinitionReader());
    }

    public void createBeanDefinitions(Set<Class<?>> classes) {
        for(Class<?> clazz : classes) {
            if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;
            Object classInstance = null;
            if(BeanDefinitionReader.class.isAssignableFrom(clazz)){
                classInstance = ReflectionUtil.newInstance(clazz);
                beanDefinitionReaders.add((BeanDefinitionReader) classInstance);
            }
            if(BeanDefinitionPostProcessor.class.isAssignableFrom(clazz)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                beanDefinitionPostProcessors.add((BeanDefinitionPostProcessor) classInstance);
            }
            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                beanPostProcessors.add((BeanPostProcessor) classInstance);
            }
            beanDefinitionReaders.forEach(beanDefinitionReader ->
                    beanDefinitions.add(beanDefinitionReader.createBeanDefinition(clazz)));
        }
    }
}
