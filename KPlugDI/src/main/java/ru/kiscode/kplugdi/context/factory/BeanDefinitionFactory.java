package ru.kiscode.kplugdi.context.factory;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.model.impl.PluginBeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanDefinitionPostProcessor;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;

import ru.kiscode.kplugdi.context.reader.BeanDefinitionReader;
import ru.kiscode.kplugdi.context.reader.impl.DefaultBeanDefinitionReader;
import ru.kiscode.kplugdi.context.scope.ScopeType;
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

    public BeanDefinitionFactory(@NonNull JavaPlugin plugin){
        beanDefinitionReaders = new ArrayList<>();
        beanDefinitions = new HashSet<>();
        beanDefinitionPostProcessors = new ArrayList<>();
        beanPostProcessors = new ArrayList<>();
        beanDefinitions.add(getPluginBeanDefinition(plugin));
        beanDefinitionReaders.add(new DefaultBeanDefinitionReader(plugin));
    }

    public void createBeanDefinitions(@NonNull Set<Class<?>> classes) {
        for(Class<?> clazz : classes) {
            if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) continue;
            Object classInstance = null;
            if(clazz.isInstance(BeanDefinitionReader.class)){
                classInstance = ReflectionUtil.newInstance(clazz);
                BeanDefinitionReader beanDefinitionReader = (BeanDefinitionReader) classInstance;
                if(!beanDefinitionReaders.contains(beanDefinitionReader)) beanDefinitionReaders.add(beanDefinitionReader);
            }
            if(clazz.isInstance(BeanDefinitionPostProcessor.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanDefinitionPostProcessor beanDefinitionPostProcessor = (BeanDefinitionPostProcessor) classInstance;
                if(!beanDefinitionPostProcessors.contains(beanDefinitionPostProcessor)) beanDefinitionPostProcessors.add(beanDefinitionPostProcessor);
            }
            if(clazz.isInstance(BeanPostProcessor.class)){
                if(classInstance == null) classInstance = ReflectionUtil.newInstance(clazz);
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) classInstance;
                if(!beanPostProcessors.contains(beanPostProcessor)) beanPostProcessors.add(beanPostProcessor);
            }
            for(BeanDefinitionReader reader : beanDefinitionReaders) {
                Set<BeanDefinition> beanDefinitionSet = reader.createBeanDefinition(clazz);
                beanDefinitions.addAll(beanDefinitionSet);
            }
        }
    }

    private BeanDefinition getPluginBeanDefinition(@NonNull JavaPlugin plugin ){
        return PluginBeanDefinition.builder()
                .pluginInstance(plugin)
                .name(plugin.getClass().getName())
                .beanClass(plugin.getClass())
                .implementInterfaces(plugin.getClass().getInterfaces())
                .scopeType(ScopeType.SINGLETON)
                .build();
    }

}
