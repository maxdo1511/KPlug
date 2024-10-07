package ru.kiscode.kplugdi.context.registry;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.IgnoreContext;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.context.reader.BeanReader;
import ru.kiscode.kplugdi.context.scope.BeanScope;
import ru.kiscode.kplugdi.utils.ReflectionUtil;

import java.util.HashSet;
import java.util.Set;

import static ru.kiscode.kplugdi.context.ApplicationContext.logger;

@Getter
public class BeanProcessRegistry {

    private final Set<BeanDefinitionPostProcessor> beanDefinitionPostProcessors;
    private final Set<BeanPostProcessor> beanPostProcessors;
    private final Set<BeanDefinitionReader> beanDefinitionReaders;
    private final Set<BeanReader> beanReaders;
    private Set<Class<?>> loadedClasses;

    public BeanProcessRegistry(){
        beanDefinitionPostProcessors = new HashSet<>();
        beanPostProcessors = new HashSet<>();
        beanDefinitionReaders = new HashSet<>();
        beanReaders = new HashSet<>();
    }

    public void findAndRegisterProcessors(@NonNull Set<Class<?>> classes, JavaPlugin plugin) {
        this.loadedClasses = classes;
        for(Class<?> clazz : classes){
            if(ReflectionUtil.isAbstractOrInterface(clazz) || ReflectionUtil.hasAnnotation(clazz, IgnoreContext.class)) continue;
            Object classInstance = null;
            logger.warning("Process class: " + clazz.getName());
            if(ReflectionUtil.hasInterfaceOrSuperClass(clazz, BeanDefinitionPostProcessor.class)) {
                classInstance = ReflectionUtil.newInstance(clazz, plugin);
                BeanDefinitionPostProcessor beanDefinitionPostProcessor = (BeanDefinitionPostProcessor) classInstance;
                beanDefinitionPostProcessors.add(beanDefinitionPostProcessor);
            }
            if(ReflectionUtil.hasInterface(clazz, BeanScope.class)) {
                classInstance = ReflectionUtil.newInstance(clazz, plugin);
                BeanScope beanScope = (BeanScope) classInstance;
                ApplicationContext.getApplicationContext().getBeanRegistry().getBeanScopes().put(beanScope.getScopeName(), beanScope);
                continue;
            }
            if(ReflectionUtil.hasInterfaceOrSuperClass(clazz, BeanPostProcessor.class)) {
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz, plugin);
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) classInstance;
                beanPostProcessors.add(beanPostProcessor);
            }
            if(ReflectionUtil.hasInterfaceOrSuperClass(clazz, BeanDefinitionReader.class)) {
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz, plugin);
                BeanDefinitionReader beanDefinitionReader = (BeanDefinitionReader) classInstance;
                beanDefinitionReaders.add(beanDefinitionReader);
            }
            if(ReflectionUtil.hasInterfaceOrSuperClass(clazz, BeanReader.class)) {
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz, plugin);
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
