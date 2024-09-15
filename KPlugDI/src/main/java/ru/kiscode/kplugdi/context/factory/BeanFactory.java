package ru.kiscode.kplugdi.context.factory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.model.BeanDefinition;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.scope.ScopeType;
import ru.kiscode.kplugdi.exception.BeanCreatingException;

import java.util.*;

public abstract class BeanFactory {

    protected Map<String, BeanDefinition> beanDefinitionsFromName;
    protected Map<String, Object> singletonObjectsByName;
    protected Multimap<Class<?>, BeanDefinition> beanDefinitionsByClass;
    protected Multimap<Class<?>,Object> singletonObjectsByClass;
    protected List<BeanPostProcessor> beanPostProcessors;

    public BeanFactory(){
        beanDefinitionsFromName = new HashMap<>();
        singletonObjectsByName = new HashMap<>();
        singletonObjectsByClass = ArrayListMultimap.create();
        beanDefinitionsByClass = ArrayListMultimap.create();
        beanPostProcessors = new ArrayList<>();
    }

    public abstract void createBeans(@NonNull Set<BeanDefinition> beanDefinitions, @NonNull List<BeanPostProcessor> beanPostProcessors);
    public abstract Object createBean(@NonNull BeanDefinition beanDefinition);


    public <T> T getBean(@NonNull Class<T> clazz, @NonNull JavaPlugin plugin) {
        return null;
    }

    public <T> T getBean(@NonNull Class<T> clazz, @NonNull String packageToScan) {
        return null;
    }

    public <T> T getBean(@NonNull Class<T> clazz) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        if (singletonObjectsByName.containsKey(name)) {
            return (T) singletonObjectsByName.get(name);
        }
        BeanDefinition beanDefinition = beanDefinitionsFromName.get(name);
        if (beanDefinition == null) {
            throw new BeanCreatingException("Bean not found. Name: " + name);
        }
        if (beanDefinition.getScopeType() == ScopeType.PROTOTYPE) {
            return (T) createBean(beanDefinition);
        }
        throw new BeanCreatingException("Bean not found. Name: " + name);
    }
}
