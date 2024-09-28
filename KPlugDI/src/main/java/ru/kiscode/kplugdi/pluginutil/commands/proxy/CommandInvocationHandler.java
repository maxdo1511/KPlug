package ru.kiscode.kplugdi.pluginutil.commands.proxy;

import java.lang.reflect.Method;

public class CommandInvocationHandler implements net.sf.cglib.proxy.InvocationHandler {
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return null;
    }
}
