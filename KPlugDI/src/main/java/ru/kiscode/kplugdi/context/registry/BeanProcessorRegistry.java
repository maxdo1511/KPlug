package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
@Getter
public class BeanProcessorRegistry {

    private final Set<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    private final Set<BeanPostProcessor> beanPostProcessors;
    private final Set<BeanDefinitionReader> beanDefinitionReaders;
    private Set<Class<?>> classes;

    public BeanProcessorRegistry(){
        beanDefinitionPostProcessors = new HashSet<>();
        beanPostProcessors = new HashSet<>();
        beanDefinitionReaders = new HashSet<>();
    }

    public void register(@NonNull Set<Class<?>> classes){
        this.classes = classes;
        for(Class<?> clazz : classes){
            if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;
            Object classInstance = null;
            if(clazz.isInstance(BeanDefinitionPostProcessor.class)){
                classInstance = ReflectionUtil.newInstance(clazz);
                BeanDefinitionPostProcessor beanDefinitionPostProcessor = (BeanDefinitionPostProcessor) classInstance;
                beanDefinitionPostProcessors.add(beanDefinitionPostProcessor);
            }
            if(clazz.isInstance(BeanPostProcessor.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) classInstance;
                beanPostProcessors.add(beanPostProcessor);
            }
            if(clazz.isInstance(BeanDefinitionReader.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanDefinitionReader beanDefinitionReader = (BeanDefinitionReader) classInstance;
                beanDefinitionReaders.add(beanDefinitionReader);
            }
        }
    }
}
