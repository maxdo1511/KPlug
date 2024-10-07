package ru.kiscode.kplugdi.context.processor.bean;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.annotations.Autowired;
import ru.kiscode.kplugdi.context.ApplicationContext;
import ru.kiscode.kplugdi.context.registry.BeanRegistry;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.exception.BeanCreatingException;
import ru.kiscode.kplugdi.utils.ReflectionUtil;
import ru.kiscode.kplugdi.utils.ValidationUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private final BeanRegistry beanRegistry = ApplicationContext.getApplicationContext().getBeanRegistry();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        try {
            for (Field field : ReflectionUtil.getAllFieldsAnnotatedWith(bean.getClass(), Autowired.class, true)) {
                ValidationUtil.validateStaticField(field, Autowired.class);
                Autowired autowired = field.getAnnotation(Autowired.class);
                Object autowiredBean = autowired.name().isEmpty() ? beanRegistry.getBean(field.getType(), plugin) : beanRegistry.getBean(autowired.name(), plugin);
                field.setAccessible(true);
                try {
                    field.set(bean, autowiredBean);
                } catch (IllegalAccessException e) {
                    throw new BeanCreatingException("Can't autowire field << %s >> in class << %s >>", e, field.getName(), bean.getClass().getName());
                }
            }
            for (Method method : ReflectionUtil.getAllMethodsAnnotatedWith(bean.getClass(), Autowired.class, true)) {
                ValidationUtil.validateStaticMethod(method, Autowired.class);
                ValidationUtil.validateReturnType(method, true, Autowired.class);
                if (method.getParameterCount() != 1) {
                    throw new BeanCreatingException("@Autowired method << %s >> in class << %s >> should have one parameter.", method.getName(), method.getDeclaringClass().getName());
                }
                Autowired autowired = method.getAnnotation(Autowired.class);
                Class<?> parameterClass = method.getParameterTypes()[0];
                Object autowiredBean = autowired.name().isEmpty() ? beanRegistry.getBean(parameterClass, plugin) : beanRegistry.getBean(autowired.name(), plugin);
                method.setAccessible(true);
                try {
                    method.invoke(bean, autowiredBean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new BeanCreatingException("Can't autowire method << %s >> in class << %s >>", e, method.getName(), bean.getClass().getName());
                }
            }
            return bean;
        }catch (StackOverflowError error) {
            throw new BeanCreatingException("Cycle dependency in class << %s >>", error, bean.getClass().getName());
        }
    }
}
