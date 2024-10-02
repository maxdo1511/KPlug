package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.context.reader.BeanReader;
import ru.kiscode.kplugdi.context.scope.BeanScope;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
@Getter
public class BeanProcessRegistry {

    private final Set<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    private final Set<BeanPostProcessor> beanPostProcessors;
    private final Set<BeanDefinitionReader> beanDefinitionReaders;
    private final Set<BeanReader> beanReaders;
    private Set<Class<?>> classes;

    public BeanProcessRegistry(){
        beanDefinitionPostProcessors = new HashSet<>();
        beanPostProcessors = new HashSet<>();
        beanDefinitionReaders = new HashSet<>();
        beanReaders = new HashSet<>();
    }

    public void findAndRegisterProcessors(@NonNull Set<Class<?>> classes){
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
                if(clazz.isInstance(BeanScope.class)) continue;
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) classInstance;
                beanPostProcessors.add(beanPostProcessor);
            }
            if(clazz.isInstance(BeanDefinitionReader.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanDefinitionReader beanDefinitionReader = (BeanDefinitionReader) classInstance;
                beanDefinitionReaders.add(beanDefinitionReader);
            }
            if(clazz.isInstance(BeanReader.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanReader beanReader = (BeanReader) classInstance;
                beanReaders.add(beanReader);
            }
        }
    }


    public void registerBeanDefinitionPostProcess(@NonNull BeanDefinitionPostProcessor beanDefinitionPostProcessor){
        beanDefinitionPostProcessors.add(beanDefinitionPostProcessor);
    }

    public void registerBeanPostProcess(@NonNull BeanPostProcessor beanPostProcessor){
        beanPostProcessors.add(beanPostProcessor);
    }

    public void registerBeanDefinitionReader(@NonNull BeanDefinitionReader beanDefinitionReader){
        beanDefinitionReaders.add(beanDefinitionReader);
    }

    public void registerBeanReader(@NonNull BeanReader beanReader){
        beanReaders.add(beanReader);
    }
}
