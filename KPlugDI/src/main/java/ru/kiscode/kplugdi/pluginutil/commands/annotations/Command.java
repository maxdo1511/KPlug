package ru.kiscode.kplugdi.pluginutil.commands.annotations;


import ru.kiscode.kplugdi.annotations.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
@Component
public @interface Command {
    String command();
}
