package ru.kiscode.kplugdi.context.scope;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.KPlugDI;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;

public interface BeanScope extends BeanPostProcessor {

    Object getBean(Object bean, String beanName, JavaPlugin plugin, BeanRegistry beanRegistry);

    String getScopeName();
    @Override
    default Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        return getBean(bean,beanName,plugin, KPlugDI.getInstance().getBeanRegistry());
    }

}
