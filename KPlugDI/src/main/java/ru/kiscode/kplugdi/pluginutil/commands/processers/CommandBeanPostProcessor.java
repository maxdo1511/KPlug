package ru.kiscode.kplugdi.pluginutil.commands.processers;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kiscode.kplugdi.context.processor.BeanPostProcessor;
import ru.kiscode.kplugdi.pluginutil.commands.TestCommand;
import ru.kiscode.kplugdi.pluginutil.commands.annotations.Command;
import ru.kiscode.kplugdi.pluginutil.commands.annotations.CommandParams;
import ru.kiscode.kplugdi.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Object> targets = new HashMap<>();
    private Map<String, String> commands = new HashMap<>();
    private Map<String, Map<String, Method>> args = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName, JavaPlugin plugin) {
        if (ReflectionUtil.hasAnnotation(bean.getClass(), Command.class)) {
            targets.put(beanName, bean);
            commands.put(beanName, bean.getClass().getAnnotation(Command.class).command());
            Map<String, Method> map = new HashMap<>();
            for (Method method : bean.getClass().getMethods()) {
                if (ReflectionUtil.hasAnnotation(method, CommandParams.class)) {
                    map.put(method.getAnnotation(CommandParams.class).args(), method);
                }
            }
            args.put(beanName, map);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName, JavaPlugin plugin) {
        Object target = targets.get(beanName);
        if (target == null) {
            return bean;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setInterfaces(new Class[]{CommandExecutor.class, TabCompleter.class});
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if (method.getName().equals("onCommand")) {
                    if (objects[3] == null) {
                        Method noArgs = target.getClass().getDeclaredMethod("noArgs", CommandSender.class);
                        noArgs.invoke(target, objects[0]);
                        return true;
                    }
                    return onCommand(o, (CommandSender) objects[0], args.get(beanName), (String) objects[2], ((String[]) objects[3]));
                }
                if (method.getName().equals("onTabComplete")) {
                    return onTabComplete((CommandSender) objects[0], (org.bukkit.command.Command) objects[1], (String) objects[2], ((String[]) objects[3]));
                }
                return methodProxy.invokeSuper(o, objects);
            }
        });
        Object proxy = enhancer.create();
        plugin.getCommand(commands.get(beanName)).setExecutor((CommandExecutor) proxy);
        return proxy;
    }

    public void execute(Object origin, CommandSender sender, Method method, String label, String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String command = method.getAnnotation(CommandParams.class).args();
        List<String> params = findParams(command, args);
        Parameter[] parameters = method.getParameters();
        Object[] objects = new Object[parameters.length];
        for (int i = 1; i < parameters.length; i++) {
            objects[i] = parseCommandParam(params.get(i - 1), parameters[i].getType());
        }
        objects[0] = sender;
        method.invoke(origin, objects);
    }

    public List<String> complete(CommandSender sender, String[] args){
        return null;
    }

    public boolean onCommand(Object origin, CommandSender sender, Map<String, Method> methods, String label, String[] args){
        try {
            execute(origin, sender, findMethod(methods, args), label, args);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args){
        if (list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (String arg : list){
            if (arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }
        return result;
    }

    private List<String> findParams(String params, String[] args) {
        List<String> list = new ArrayList<>();
        String[] split = params.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith("{") && split[i].endsWith("}")) {
                list.add(args[i]);
            }
        }
        return list;
    }

    private <T> T parseCommandParam(String string, Class<T> clazz) {
        if (Player.class.isAssignableFrom(clazz)) {
            T player = (T) Bukkit.getPlayer(string);
            if (player == null) {
                player = (T) Bukkit.getOfflinePlayer(string);
            }
            return player;
        }
        switch (clazz.getSimpleName()) {
            case "Integer":
            case "int":
                return (T) Integer.valueOf(string);
            case "Double":
            case "double":
                return (T) Double.valueOf(string);
            case "Boolean":
            case "boolean":
                return (T) Boolean.valueOf(string);
            default:
                return (T) string;
        }
    }

    private Method findMethod(Map<String, Method> methods, String[] args) {
        for (Map.Entry<String, Method> entry : methods.entrySet()) {
            boolean flag = true;
            String[] split = entry.getKey().split(" ");
            if (split.length != args.length) {
                flag = false;
            } else {
                for (int i = 0; i < split.length; i++) {
                    if (!(split[i].startsWith("{") && split[i].endsWith("}"))) {
                        if (!split[i].equals(args[i])) {
                            flag = false;
                            break;
                        }
                    }
                }
            }
            if (flag) return entry.getValue();
        }
        return null;
    }

}
