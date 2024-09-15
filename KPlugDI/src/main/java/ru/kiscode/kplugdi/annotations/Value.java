package ru.kiscode.kplugdi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kisapa
 * @since 04.06.2024
 *
 * Kplug by Kisapa - <a href="https://github.com/maxdo1511">github</a>
 *
 * Аннотация для значений конфигурационных параметров
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {

    String value();

    String defaultValue() default "";

}
